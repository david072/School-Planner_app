package de.sharable.android.school_planner.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import de.sharable.android.school_planner.R
import de.sharable.android.school_planner.activities.MainActivity

class Notifications {

    companion object {
        private lateinit var notificationManager: NotificationManager
        private lateinit var notificationChannel: NotificationChannel
        private const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
        private lateinit var pendingIntent: PendingIntent

        fun createNotificationChannel(context: Context) {
            notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationChannel = NotificationChannel(PRIMARY_CHANNEL_ID, "Job Intent Service notification",
                NotificationManager.IMPORTANCE_HIGH)

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Notifications from Job Intent Service"

            notificationManager.createNotificationChannel(notificationChannel)

            pendingIntent = PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT)
        }

        fun notifyUser(context: Context, message: String, title: String) {
            val builder = NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)

            notificationManager.notify(0, builder.build())
        }

        fun free() {
            notificationManager.deleteNotificationChannel(notificationChannel.id)
        }
    }

}