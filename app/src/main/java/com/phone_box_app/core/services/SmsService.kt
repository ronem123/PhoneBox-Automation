package com.phone_box_app.core.services

import android.app.Service
import android.app.Service.START_NOT_STICKY
import android.content.Intent
import android.os.IBinder
import android.telephony.SmsManager
import android.util.Log
import androidx.core.net.toUri
import com.phone_box_app.util.ArcBroadCastIntentAction
import com.phone_box_app.util.ArgIntent
import com.phone_box_app.util.buildNotification
import com.phone_box_app.util.createNotificationChannel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * Created by Ram Mandal on 12/11/2025
 * @System: Apple M1 Pro
 */
@AndroidEntryPoint
class SmsService : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val TAG = "SmsService"

    private val channelId = "sms_task_channel"
    private val channelName = "sms task runner"


    override fun onCreate() {
        super.onCreate()
        createNotificationChannel(this, channelId, channelName)

        val notification = buildNotification(
            context = this,
            notificationTitle = "Running Task",
            notificationContent = "Executing scheduled sms task...",
            channelId = channelId,
            smallIcon = android.R.drawable.ic_dialog_email,
            setOnGoing = false
        )
        startForeground(103, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val phoneNumber =
            intent?.getStringExtra(ArgIntent.ARG_RECEIVER_PHONE) ?: return START_NOT_STICKY
        val message = intent.getStringExtra(ArgIntent.ARG_MESSAGE).orEmpty()

        scope.launch {
            executeTask(phoneNumber, message)
        }

        return START_NOT_STICKY
    }

    private fun executeTask(phoneNumber: String, message: String) {
        Log.d(TAG, "Alarm received → Sending message to $phoneNumber")
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Log.d(TAG, "SMS sent successfully to $phoneNumber")
            Log.d(TAG, "Message: $message")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send SMS", e)
        }


        stopSelf()
    }


    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

}