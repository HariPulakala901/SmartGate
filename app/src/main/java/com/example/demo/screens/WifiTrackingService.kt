package com.example.demo.screens

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.demo.R
import com.google.firebase.auth.FirebaseAuth

class WifiTrackingService : Service() {

    private lateinit var wifiScanHelper: WifiScanHelper
    private val CHANNEL_ID = "wifi_tracking_channel"
    private val NOTIFICATION_ID = 1001

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        wifiScanHelper = WifiScanHelper(applicationContext)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // FIX: Use FirebaseAuth as primary source, fall back to SharedPrefs
        val uid = FirebaseAuth.getInstance().currentUser?.uid
            ?: run {
                val prefs = getSharedPreferences("visitor_prefs", MODE_PRIVATE)
                prefs.getString("uid", null)
            }
            ?: run {
                Log.w("WIFI_SERVICE", "No UID found — stopping service")
                stopSelf()
                return START_NOT_STICKY
            }

        startForeground(NOTIFICATION_ID, buildNotification())
        wifiScanHelper.startScanning(uid)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        wifiScanHelper.stopScanning()
        Log.d("WIFI_SERVICE", "Foreground service stopped")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("SmartGate — Visit Active")
            // FIX: Less alarming, more informative notification text
            .setContentText("📡 SmartGate is tracking your campus zone")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Wi-Fi Zone Tracking",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Active while visitor is on campus premises"
        }

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}