package de.sharable.android.school_planner.notifications.service

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import de.sharable.android.school_planner.TaskEntry
import de.sharable.android.school_planner.notifications.Notifications
import de.sharable.android.school_planner.sqlite.DatabaseInterface
import de.sharable.android.school_planner.util.Constants
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class NotificationJobIntentService : JobIntentService() {

    private val tag = "NotificationJobIntentService"

    companion object {
        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, NotificationJobIntentService::class.java, 123, work)
        }
    }

    override fun onCreate() {
        DatabaseInterface.init(this)
        Notifications.createNotificationChannel(this)
        super.onCreate()
    }

    override fun onHandleWork(intent: Intent) {
        val tasks: ArrayList<TaskEntry> = DatabaseInterface.readAllData(DatabaseInterface.getAllItemsCursor(), this)
        for(task: TaskEntry in tasks) {
            if(!task.completed) {
                if(task.reminder.isEqual(LocalDate.now()) || task.reminder.isBefore(LocalDate.now())) {
                    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(Constants.longDateFormat)
                    Notifications.notifyUser(this, "Die Aufgabe ${task.title} ist am " +
                            "${formatter.format(task.dueAtDate)} fällig!", "${task.title} bald fällig " +
                            "(${resources.getString(task.subject!!.actualName)})")
                }
            }
        }
    }

    override fun onDestroy() {
        DatabaseInterface.free()
        super.onDestroy()
    }

}