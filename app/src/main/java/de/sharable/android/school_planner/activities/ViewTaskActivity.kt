package de.sharable.android.school_planner.activities

import android.content.Intent
import android.nfc.Tag
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.KeyListener
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import de.sharable.android.school_planner.R
import de.sharable.android.school_planner.TaskEntry
import de.sharable.android.school_planner.sqlite.DatabaseInterface
import de.sharable.android.school_planner.util.Constants
import de.sharable.android.school_planner.util.UtilFunctions
import java.lang.Exception
import java.security.Key

class ViewTaskActivity : AppCompatActivity() {

    private lateinit var titleText: TextView
    private lateinit var descriptionText: TextView

    private lateinit var dueAtText: TextView
    private lateinit var reminderText: TextView
    private lateinit var subjectText: TextView

    private lateinit var editButton: ImageView
    private lateinit var deleteButton: ImageView
    private lateinit var markCompletedButton: Button
    private lateinit var backArrow: ImageView

    private lateinit var taskEntry: TaskEntry
    private var taskIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_task)

        if(intent.getParcelableExtra<TaskEntry>("task") == null)
            throw Exception("Must provide task through intent!")

        taskEntry = intent.getParcelableExtra("task")!!
        taskIndex = intent.getIntExtra("taskIndex", -1)

        titleText = findViewById(R.id.viewtask_title_edittext)
        descriptionText = findViewById(R.id.viewtask_description_edittext)

        dueAtText = findViewById(R.id.viewtask_dueat_text)
        reminderText = findViewById(R.id.viewtask_notification_text)
        subjectText = findViewById(R.id.viewtask_subject_text)

        editButton = findViewById(R.id.viewtask_edit_button)
        deleteButton = findViewById(R.id.viewtask_delete_button)
        markCompletedButton = findViewById(R.id.viewtask_markcompleted_button)
        backArrow = findViewById(R.id.viewtask_arrowback_button)

        editButton.setOnClickListener { editTask() }
        deleteButton.setOnClickListener { deleteTask() }
        markCompletedButton.setOnClickListener { onButtonClick() }
        backArrow.setOnClickListener { goBack() }

        setValues()
    }

    private fun setValues() {
        titleText.text = taskEntry.title
        descriptionText.text = taskEntry.description

        dueAtText.text = Constants.shortFormatter.format(taskEntry.dueAtDate)
        val reminderCopy = taskEntry.reminder

        val choice: String? = Constants.reminderChoiceFromLocalDate(reminderCopy, taskEntry.dueAtDate)
        if(choice != null)
            reminderText.text = choice
        else
            reminderText.text = Constants.longFormatter.format(taskEntry.reminder)

        if(taskEntry.subject != null)
            subjectText.text = resources.getString(taskEntry.subject!!.actualName)
        else
            subjectText.text = resources.getString(R.string.view_task_subject_invalid)

        if(taskEntry.completed) {
            markCompletedButton.setBackgroundResource(R.drawable.viewtask_task_completed_button_bg)
            markCompletedButton.text = resources.getString(R.string.view_task_button_completed)
        }
    }

    private fun editTask() {
        val intent = Intent(this, CreateTaskActivity::class.java)
        intent.putExtra("returnToMain", false)
        intent.putExtra("task", taskEntry)

        if(taskIndex == -1) throw Exception("taskIndex must not be -1")
        intent.putExtra("taskIndex", taskIndex)
        startActivity(intent)
    }

    private fun deleteTask() {
        val alertDialog: AlertDialog = UtilFunctions.createConfirmAlertDialog(this,
            resources.getString(R.string.view_task_delete_alert_title), resources.getString(R.string.view_task_delete_alert_message),
            resources.getString(R.string.view_task_button_delete), resources.getString(R.string.general_cancel), { dialog, _ ->
                DatabaseInterface.deleteEntry(taskEntry.databaseID, this)
                MainActivity.readTasks()
                dialog.dismiss()
                goBack()
            }, { dialog, _ ->
                dialog.cancel()
            })

        alertDialog.show()
    }

    private fun goBack() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun onButtonClick() {
        if(taskEntry.completed) {
            taskEntry.completed = false
            markCompletedButton.setBackgroundResource(R.drawable.button_enabled)
            markCompletedButton.text = resources.getString(R.string.view_task_button_markcompleted)
        }
        else {
            taskEntry.completed = true
            markCompletedButton.setBackgroundResource(R.drawable.viewtask_task_completed_button_bg)
            markCompletedButton.text = resources.getString(R.string.view_task_button_completed)
        }

        DatabaseInterface.updateData(taskEntry, this)
        MainActivity.readTasks()
    }

}
