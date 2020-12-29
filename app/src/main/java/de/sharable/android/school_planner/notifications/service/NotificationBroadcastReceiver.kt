package de.sharable.android.school_planner.notifications.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.*

class NotificationBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private const val action = "NOTIFICATION_ACTION"
        private const val requestCode = 123

        fun startAlarmManager(context: Context) {
            if(alarmExists(context))
                return

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = PendingIntent.getBroadcast(context, requestCode, getIntent(context), PendingIntent.FLAG_UPDATE_CURRENT)

            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP, Calendar.getInstance().timeInMillis, AlarmManager.INTERVAL_DAY,
                alarmIntent)
        }

        fun cancelAlarm(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if(alarmExists(context)) {
                alarmManager.cancel(PendingIntent.getBroadcast(context, requestCode, getIntent(context), PendingIntent.FLAG_UPDATE_CURRENT))
            }
        }

        private fun getIntent(context: Context): Intent {
            val intent = Intent(context, NotificationBroadcastReceiver::class.java)
            intent.action = action
            return intent
        }

        private fun alarmExists(context: Context): Boolean {
            return PendingIntent.getBroadcast(context, requestCode, getIntent(context), PendingIntent.FLAG_NO_CREATE) != null
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action == action) {
            if(context != null)
                NotificationJobIntentService.enqueueWork(context, intent)
        }
    }

}