package com.example.demo.screens

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.*

// -----------------------------------------------------------------
// ZONE MAP — replace these BSSIDs with your actual access point
// BSSIDs (phone hotspots for demo, campus APs for production).
//
// How to find a hotspot BSSID:
//   Connect to the hotspot → Settings → Wi-Fi → tap the network
//   → look for "MAC address" or use a Wi-Fi analyzer app.
//
// Format: "bssid_lowercase" to "Zone Name"
// -----------------------------------------------------------------
val ZONE_MAP = mapOf(
    "16:f3:ca:47:62:4c" to "sampath phone",
    "de:38:3b:1e:38:e4" to "Shreya phone"
)

// ─────────────────────────────────────────────────────────────────
// Zone determination — pick the ZONE_MAP entry with strongest signal
// ─────────────────────────────────────────────────────────────────
fun determineZone(scanResults: List<android.net.wifi.ScanResult>): String {
    // Deduplicate by BSSID — keep only the strongest reading per AP
    val bestPerBssid = scanResults
        .groupBy { it.BSSID.lowercase() }
        .mapValues { (_, results) -> results.maxByOrNull { it.level }!! }

    var bestZone   = "Unknown Zone"
    var bestSignal = Int.MIN_VALUE

    for ((bssid, result) in bestPerBssid) {
        Log.d("WIFI_DISCOVER", "BSSID=${result.BSSID}  SSID=${result.SSID}  RSSI=${result.level}")
        if (ZONE_MAP.containsKey(bssid) && result.level > bestSignal) {
            bestSignal = result.level
            bestZone   = ZONE_MAP[bssid]!!
        }
    }

    Log.d("WIFI_ZONE", "Determined zone: $bestZone  (signal: $bestSignal dBm)")
    return bestZone
}

// ─────────────────────────────────────────────────────────────────
// WifiScanHelper
//
// APPROACH: Hybrid active + passive scanning
//
//   1. Triggers startScan() every ACTIVE_SCAN_INTERVAL_MS to force
//      Android to refresh Wi-Fi signal data with fresh RSSI values.
//      Android throttles this (4 per 2 min for foreground apps),
//      but even throttled scans are better than stale data.
//
//   2. Reads wifiManager.scanResults every READ_INTERVAL_MS to
//      check the (now freshly updated) cache for zone changes.
//
//   3. Also registers a BroadcastReceiver to react IMMEDIATELY
//      when Android finishes any scan (triggered by us or by the OS).
// ─────────────────────────────────────────────────────────────────
class WifiScanHelper(private val context: Context) {

    companion object {
        // How often to read cached results
        private const val READ_INTERVAL_MS = 2_000L

        // How often to trigger an active scan (Android may throttle this)
        private const val ACTIVE_SCAN_INTERVAL_MS = 15_000L

        // Only push to Firebase if zone holds stable for this many
        // consecutive reads — prevents flickering between zones
        private const val STABLE_READ_COUNT = 2
    }

    private val wifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private var scanJob: Job? = null
    private var activeScanJob: Job? = null
    private var lastPushedZone: String = ""

    // BroadcastReceiver that fires when Android finishes a Wi-Fi scan
    private var scanReceiver: BroadcastReceiver? = null
    private var cachedRef: com.google.firebase.database.DatabaseReference? = null
    private var stableZone = "Unknown Zone"
    private var stableCount = 0

    // ── Start scanning ────────────────────────────────────────────
    fun startScanning(uid: String) {
        if (scanJob?.isActive == true) {
            Log.d("WIFI_ZONE", "Already scanning — skipping duplicate start")
            return
        }

        scanJob = CoroutineScope(Dispatchers.IO).launch {
            Log.d("WIFI_ZONE", "Coroutine scan loop started for uid=$uid")

            // Resolve Firebase ref once — reuse it every loop iteration
            cachedRef = resolveFirebaseRef(uid)
            if (cachedRef == null) {
                Log.e("WIFI_ZONE", "Could not resolve Firebase ref — stopping")
                return@launch
            }

            stableZone  = "Unknown Zone"
            stableCount = 0

            while (isActive) {
                processCurrentScanResults()
                delay(READ_INTERVAL_MS)
            }
        }

        // ── Active scan trigger loop ──────────────────────────────
        // Periodically calls startScan() to force Android to refresh
        // its Wi-Fi cache with up-to-date RSSI values
        activeScanJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                @Suppress("MissingPermission")
                val started = wifiManager.startScan()
                Log.d("WIFI_ZONE", "Active startScan() triggered → accepted=$started")
                delay(ACTIVE_SCAN_INTERVAL_MS)
            }
        }

        // ── Register scan-complete receiver ───────────────────────
        // React immediately when Android finishes a scan instead of
        // waiting for the next READ_INTERVAL_MS tick
        scanReceiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                val success = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    intent?.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false) ?: false
                } else true

                if (success) {
                    Log.d("WIFI_ZONE", "Scan completed broadcast received — processing fresh results")
                    CoroutineScope(Dispatchers.IO).launch {
                        processCurrentScanResults()
                    }
                }
            }
        }

        @Suppress("UnspecifiedRegisterReceiverFlag")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.applicationContext.registerReceiver(
                scanReceiver,
                IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION),
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            context.applicationContext.registerReceiver(
                scanReceiver,
                IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            )
        }

        Log.d("WIFI_ZONE", "Scan loop launched (read=${READ_INTERVAL_MS}ms, active=${ACTIVE_SCAN_INTERVAL_MS}ms, stable=${STABLE_READ_COUNT} reads)")
    }

    // ── Process current scan results ──────────────────────────────
    private suspend fun processCurrentScanResults() {
        @Suppress("MissingPermission")
        val results = wifiManager.scanResults

        val detectedZone = if (results.isEmpty()) {
            Log.d("WIFI_ZONE", "Scan cache empty — waiting")
            "Unknown Zone"
        } else {
            determineZone(results)
        }

        // ── Stability filter ───────────────────────────────
        if (detectedZone == stableZone) {
            stableCount++
        } else {
            stableZone  = detectedZone
            stableCount = 1
            Log.d("WIFI_ZONE", "Zone candidate changed → $detectedZone (stabilizing...)")
        }

        if (stableCount >= STABLE_READ_COUNT &&
            stableZone != lastPushedZone &&
            stableZone != "Unknown Zone"
        ) {
            Log.d("WIFI_ZONE", "Zone confirmed → $stableZone — pushing to Firebase")
            lastPushedZone = stableZone

            withContext(Dispatchers.Main) {
                pushZoneToFirebase(
                    ref  = cachedRef!!,
                    zone = stableZone
                )
            }
        }
    }

    // ── Stop scanning ─────────────────────────────────────────────
    fun stopScanning() {
        scanJob?.cancel()
        scanJob = null
        activeScanJob?.cancel()
        activeScanJob = null
        lastPushedZone = ""

        scanReceiver?.let {
            try {
                context.applicationContext.unregisterReceiver(it)
            } catch (_: Exception) {}
        }
        scanReceiver = null

        Log.d("WIFI_ZONE", "Scan loop stopped")
    }

    // ── Resolve the latest Firebase request ref for this UID ─────
    // Done once at startup — avoids a Firebase query every 2 seconds
    private suspend fun resolveFirebaseRef(
        uid: String
    ): com.google.firebase.database.DatabaseReference? =
        suspendCancellableCoroutine { continuation ->
            FirebaseDatabase.getInstance()
                .getReference("visitorRequests")
                .orderByChild("uid")
                .equalTo(uid)
                .get()
                .addOnSuccessListener { snapshot ->
                    val latestRequest = snapshot.children.maxByOrNull {
                        when (val ts = it.child("timestamp").value) {
                            is Long -> ts
                            is Int  -> ts.toLong()
                            else    -> 0L
                        }
                    }
                    continuation.resume(latestRequest?.ref, null)
                }
                .addOnFailureListener { e ->
                    Log.e("WIFI_ZONE", "Failed to resolve Firebase ref: ${e.message}")
                    continuation.resume(null, null)
                }
        }

    // ── Push zone update to Firebase ──────────────────────────────
    // Only adds to history if the zone is DIFFERENT from the last
    // entry, preventing duplicate history items for the same zone.
    private fun pushZoneToFirebase(
        ref: com.google.firebase.database.DatabaseReference,
        zone: String
    ) {
        val timestamp = java.text.SimpleDateFormat(
            "HH:mm:ss", java.util.Locale.getDefault()
        ).format(java.util.Date())

        // Read existing history first, then append new entry only if zone changed
        ref.child("history").get().addOnSuccessListener { historySnapshot ->

            val existingHistory = historySnapshot.children.map { child ->
                mapOf(
                    "time" to (child.child("time").value ?: ""),
                    "zone" to (child.child("zone").value ?: "")
                )
            }.toMutableList()

            // Only add to history if the zone is different from the last entry
            val lastHistoryZone = existingHistory.lastOrNull()?.get("zone") as? String
            if (lastHistoryZone != zone) {
                existingHistory.add(mapOf("time" to timestamp, "zone" to zone))
            }

            val updates = mapOf(
                "currentZone"    to zone,
                "lastZoneUpdate" to com.google.firebase.database.ServerValue.TIMESTAMP,
                "history"        to existingHistory
            )

            ref.updateChildren(updates)
                .addOnSuccessListener {
                    Log.d("WIFI_ZONE", "Firebase updated → zone=$zone at $timestamp")
                }
                .addOnFailureListener { e ->
                    Log.e("WIFI_ZONE", "Firebase update failed: ${e.message}")
                }
        }
    }
}