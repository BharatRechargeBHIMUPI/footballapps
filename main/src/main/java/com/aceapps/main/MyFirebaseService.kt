package com.aceapps.main

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        val title = remoteMessage.data["title"]
        val body = remoteMessage.data["body"]

        showNotification(title, body)
    }

    private fun showNotification(title: String?, message: String?) {

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // ✅ Create channel (IMPORTANT)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                "channel_id",
                "Football Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, "channel_id")
            .setContentTitle(title ?: "Title")
            .setContentText(message ?: "Message")
            .setSmallIcon(R.drawable.ic_app_logo)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // 🔥 important
            .setAutoCancel(true)

        // ✅ Use unique ID (important)
        manager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}