package de.sharable.android.school_planner.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import de.sharable.android.school_planner.R
import de.sharable.android.school_planner.TaskAdapter
import de.sharable.android.school_planner.TaskEntry
import de.sharable.android.school_planner.notifications.service.NotificationBroadcastReceiver
import de.sharable.android.school_planner.sqlite.DatabaseInterface
import de.sharable.android.school_planner.util.Constants
import java.time.LocalDate
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {

    private lateinit var taskListView: ListView
    private lateinit var currentDate: TextView
    private lateinit var addTaskButton: Button

    private lateinit var completedTasks: TextView
    private lateinit var notCompletedTasks: TextView

    init {
        instance = this
    }

    companion object {
        private var instance: MainActivity? = null
        var tasks: ArrayList<TaskEntry> = ArrayList()

        fun readTasks() {
            tasks = DatabaseInterface.readAllData(DatabaseInterface.getAllItemsCursor(), instance!!)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DatabaseInterface.init(this)

        currentDate = findViewById(R.id.main_title_date)
        taskListView = findViewById(R.id.main_mytasks_list)

        addTaskButton = findViewById(R.id.main_addtask_button)
        addTaskButton.setOnClickListener { addTask() }

        completedTasks = findViewById(R.id.main_completed_tasks_text)
        notCompletedTasks = findViewById(R.id.main_tasks_text)

        currentDate.text = Constants.longFormatter.format(LocalDateTime.now())

        readTasks()
        checkForDeletableTasks()
        readTasks()

        var _completedTasks = 0
        for (task: TaskEntry in tasks) {
            if(task.completed)
                _completedTasks++
        }

        completedTasks.text = _completedTasks.toString()
        notCompletedTasks.text = (tasks.size - _completedTasks).toString()

        val adapter = TaskAdapter(this, tasks)
        taskListView.adapter = adapter
        taskListView.setOnItemClickListener { _, _, position, _ -> onItemClicked(position) }
        adapter.notifyDataSetChanged()

        startAlarmManager()
    }

    private fun startAlarmManager() {
        NotificationBroadcastReceiver.startAlarmManager(this)
    }

    private fun checkForDeletableTasks() {
        for(task in tasks) {
            if(task.dueAtDate.isEqual(LocalDate.now()) || task.dueAtDate.isBefore(LocalDate.now())) {
                DatabaseInterface.deleteEntry(task.databaseID, this)
            }
        }
    }

    private fun onItemClicked(position: Int) {
        val intent = Intent(this,  ViewTaskActivity::class.java)
        intent.putExtra("task", tasks[position])
        intent.putExtra("taskIndex", position)
        startActivity(intent)
    }

    private fun addTask() {
        val intent = Intent(this, CreateTaskActivity::class.java)
        startActivity(intent)
    }

    override fun onStop() {
        DatabaseInterface.free()
        super.onStop()
    }

}
