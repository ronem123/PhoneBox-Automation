package com.phone_box_app.core.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
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
 * Created by Ram Mandal on 11/11/2025
 * @System: Apple M1 Pro
 */
// CallService.kt
@AndroidEntryPoint
class CallService : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val TAG = "CallService"

    private val channelId = "call_task_channel"
    private val channelName = "call task runner"


    override fun onCreate() {
        super.onCreate()
        createNotificationChannel(this, channelId, channelName)

        val notification = buildNotification(
            context = this,
            notificationTitle = "Running Task",
            notificationContent = "Executing scheduled call task...",
            channelId = channelId,
            smallIcon = android.R.drawable.ic_menu_call,
            setOnGoing = false
        )
        startForeground(102, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val phoneNumber =
            intent?.getStringExtra(ArgIntent.ARG_RECEIVER_PHONE) ?: return START_NOT_STICKY
        val duration = intent.getIntExtra(ArgIntent.ARG_DURATION, 30) // in seconds

        scope.launch {
            executeTask(phoneNumber, duration)
        }

        return START_NOT_STICKY
    }

    private suspend fun executeTask(phoneNumber: String, duration: Int) {
        Log.d("TaskAlarmReceiver", "Alarm received → Making call to $phoneNumber")

        // Place call from foreground context
        val callIntent = Intent(Intent.ACTION_CALL).apply {
            data = "tel:$phoneNumber".toUri()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(callIntent)


        //converting minute to milliseconds
        val durationInMilliSeconds = duration * 60L * 1000L

        Log.v(TAG, "Going in delay for $duration")
        //wait until the task duration to complete
        delay(durationInMilliSeconds)
        Log.v(TAG, "Delay ended")


        //send broadcast to tasker after durationInMilliseconds/1000 sec to end the call
        endCallAfter(durationInMilliSeconds / 1000)

        stopSelf()
    }


    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun endCallAfter(delay: Long) {
        Log.d(TAG, "End Call via broadcast with delay: $delay")

        try {
            val intent = Intent(ArcBroadCastIntentAction.END_CALL)
            intent.putExtra(ArgIntent.ARG_DELAY, delay) // Pass the delay as an extra
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
            sendBroadcast(intent) // Send a broadcast to Tasker or a listener
            Log.d(TAG, "END Call broadcast sent successfully with delay: $delay")
        } catch (e: Exception) {
            Log.e(TAG, "Error sending End Call broadcast", e)
        }
    }
}