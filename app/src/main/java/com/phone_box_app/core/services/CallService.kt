//package com.phone_box_app.core.services
//
//import android.app.Service
//import android.content.Intent
//import android.net.Uri
//import android.os.Handler
//import android.os.IBinder
//import android.os.Looper
//import androidx.core.app.NotificationCompat
//import dagger.hilt.android.AndroidEntryPoint
//
//
///**
// * Created by Ram Mandal on 11/11/2025
// * @System: Apple M1 Pro
// */
//// CallService.kt
//@AndroidEntryPoint
//class CallService : Service() {
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        val phoneNumber = intent?.getStringExtra("phoneNumber")
//
//        if (!phoneNumber.isNullOrEmpty()) {
//            makeCall(phoneNumber)
//        } else {
//            stopSelf()
//        }
//
//        return START_NOT_STICKY
//    }
//
//    private fun makeCall(phoneNumber: String) {
//        val callIntent = Intent(Intent.ACTION_CALL).apply {
//            data = Uri.parse("tel:$phoneNumber")
//            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        }
//
//        // Start foreground first — required for Android 10+ background start limits
//        val notification = NotificationCompat.Builder(this, "call_channel")
//            .setContentTitle("Placing call")
//            .setContentText("Calling $phoneNumber...")
//            .setSmallIcon(R.drawable.ic_call)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .build()
//
//        startForeground(101, notification)
//
//        try {
//            startActivity(callIntent)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        // Optional: stop service after short delay
//        Handler(Looper.getMainLooper()).postDelayed({
//            stopForeground(true)
//            stopSelf()
//        }, 3000)
//    }
//
//    override fun onBind(intent: Intent?): IBinder? = null
//}
