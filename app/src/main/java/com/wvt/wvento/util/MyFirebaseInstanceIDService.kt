package com.wvt.wvento.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.wvt.wvento.R
import com.wvt.wvento.ui.activity.DetailsActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class MyFirebaseInstanceIDService : FirebaseMessagingService() {

    companion object {
        const val TAG = "PushNotification"
        const val NOTIFICATION_CHANNEL_ID = "my_channel_id_05"
        const val NOTIFICATION_CHANNEL = "appName_channel_02"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed Token: $token")
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

        }

        if(remoteMessage.notification!=null) {
            Log.d(TAG, "Message data payload: ${remoteMessage.notification!!.body}")
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            sendNotification(remoteMessage)
        }
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        val intent = Intent(this, DetailsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification", true)
        }

        val resultPendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(remoteMessage.notification?.title)
            .setContentText(remoteMessage.notification?.body)
            .setSmallIcon(R.drawable.ic_wevento)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(resultPendingIntent)

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(Random.nextInt(), notificationBuilder.build())
    }
}