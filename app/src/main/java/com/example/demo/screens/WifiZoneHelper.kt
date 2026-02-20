package com.example.demo.screens

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.firebase.database.FirebaseDatabase

val ZONE_MAP = mapOf(
    "aa:bb:cc:dd:ee:01" to "Main Entrance",
    "aa:bb:cc:dd:ee:02" to "Reception",
    "aa:bb:cc:dd:ee:03" to "Conference Room",
    "aa:bb:cc:dd:ee:04" to "Lab Block",
    "c4:e9:0a:e3:d2:02" to "my room"
)

fun determineZone(scanResults: List<android.net.wifi.ScanResult>): String {
    scanResults.forEach {
        Log.d("WIFI_DISCOVER", "SSID=${it.SSID}  BSSID=${it.BSSID}  RSSI=${it.level}")
    }

    var bestZone = "Unknown Zone"
    var bestSignal = Int.MIN_VALUE

    for (result in scanResults) {
        val bssid = result.BSSID.lowercase()
        if (ZONE_MAP.containsKey(bssid) && result.level > bestSignal) {
            bestSignal = result.level
            bestZone = ZONE_MAP[bssid]!!
        }
    }
    return bestZone
}

fun pushZoneToFirebase(uid: String, zone: String) {
    val dbRef = FirebaseDatabase.getInstance().getReference("visitorRequests")

    dbRef.orderByChild("uid").equalTo(uid).get()
        .addOnSuccessListener { snapshot ->
            val latestRequest = snapshot.children.maxByOrNull {
                it.child("timestamp").getValue(Long::class.java) ?: 0L
            }
            latestRequest?.ref?.updateChildren(
                mapOf(
                    "currentZone" to zone,
                    "lastZoneUpdate" to com.google.firebase.database.ServerValue.TIMESTAMP
                )
            )?.addOnSuccessListener {
                Log.d("WIFI_ZONE", "Zone updated → $zone")
            }
        }
}

class WifiScanHelper(private val context: Context) {

    private val wifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private var receiver: BroadcastReceiver? = null
    private val handler = Handler(Looper.getMainLooper())
    private var currentUid: String = ""

    private val scanRunnable = Runnable {
        wifiManager.startScan()
    }

    fun startScanning(uid: String) {
        currentUid = uid

        receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                // Permission was already verified in approvedscreen before calling this
                @Suppress("MissingPermission")
                val results = wifiManager.scanResults

                val zone = determineZone(results)
                pushZoneToFirebase(currentUid, zone)

                // Schedule next scan after 15 seconds
                handler.postDelayed(scanRunnable, 15_000)
            }
        }

        context.registerReceiver(
            receiver,
            IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        )

        // Trigger first scan immediately
        wifiManager.startScan()
        Log.d("WIFI_ZONE", "Wi-Fi scanning started for uid=$uid")
    }

    fun stopScanning() {
        handler.removeCallbacks(scanRunnable)
        receiver?.let {
            try {
                context.unregisterReceiver(it)
            } catch (e: Exception) {
                Log.e("WIFI_ZONE", "Receiver already unregistered")
            }
        }
        receiver = null
        Log.d("WIFI_ZONE", "Wi-Fi scanning stopped")
    }
}