package com.pht.roomfinder.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pht.roomfinder.MainActivity
import com.pht.roomfinder.R

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebase : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        if (message.data.isNotEmpty()) {
            message.data.let {
                val title = it["title"] ?: "Thông báo"
                val messageBody = it["message"] ?: "No message"
                if (it["action"] == "send_notification") {
                    updateNotification()
                }
                sendNotification(title, messageBody)
            }
        }
    }

    private fun updateNotification() {
        val workManager = WorkManager.getInstance(applicationContext)
        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>().build()
        workManager.enqueue(workRequest)
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun sendNotification(title: String, message: String) {
        val channelId = "RoomFinder"
        val notificationId = System.currentTimeMillis().toInt()

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Room Finder Notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title).setContentText(message)
            .setSmallIcon(R.drawable.home_icon).setContentIntent(pendingIntent)
            .setAutoCancel(true).build()

        notificationManager.notify(notificationId, notification)
    }
}