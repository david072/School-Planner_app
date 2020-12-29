package de.sharable.android.school_planner.activities

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import de.sharable.android.school_planner.R
import de.sharable.android.school_planner.TaskEntry
import de.sharable.android.school_planner.sqlite.DatabaseInterface
import de.sharable.android.school_planner.util.Constants
import de.sharable.android.school_planner.util.DatePickerFragment
import de.sharable.android.school_planner.util.Subject
import de.sharable.android.school_planner.util.UtilFunctions
import java.lang.Exception
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CreateTaskActivity : AppCompatActivity() {

    private lateinit var titleEditText: TextView
    private lateinit var descriptionEditText: TextView

    private lateinit var dueAtSelector: ConstraintLayout
    private lateinit var dueAtTextView: TextView

    private lateinit var reminderSelector: ConstraintLayout
    private lateinit var reminderTextView: TextView

    private lateinit var subjectSelector: ConstraintLayout
    private lateinit var subjectTextView: TextView

    private lateinit var addTaskButton: Button
    private lateinit var backArrowButton: ImageView

    private lateinit var datePickerFragment: DatePickerFragment

    private var dueAtDate: LocalDate? = null
    private var reminderDate: LocalDate? = null
    private var subject: Subject? = null

    private var returnToMain: Boolean = true
    private var task: TaskEntry? = null
    private var taskIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task)

        returnToMain = intent.getBooleanExtra("returnToMain", true)
        task = intent.getParcelableExtra("task")
        taskIndex = intent.getIntExtra("taskIndex", -1)

        datePickerFragment = DatePickerFragment()

        titleEditText = findViewById(R.id.createtask_title_edittext)
        descriptionEditText = findViewById(R.id.createtask_description_edittext)

        dueAtSelector = findViewById(R.id.createtask_dueat_layout)
        dueAtTextView = findViewById(R.id.createtask_dueat_text)

        reminderSelector = findViewById(R.id.createtask_notification_layout)
        reminderTextView = findViewById(R.id.createtask_notification_text)

        subjectSelector = findViewById(R.id.createtask_subject_layout)
        subjectTextView = findViewById(R.id.createtask_subject_text)

        addTaskButton = findViewById(R.id.createtask_addtask_button)
        backArrowButton = findViewById(R.id.createtask_arrowback_button)

        if(!returnToMain && task != null) {
            val title: TextView = findViewById(R.id.createtask_title_headline)
            title.text = resources.getString(R.string.create_task_headline_editmode)
            addTaskButton.text = resources.getString(R.string.create_task_button_editmode)

            dueAtDate = task!!.dueAtDate
            reminderDate = task!!.reminder
            subject = task!!.subject

            titleEditText.text = task!!.title
            descriptionEditText.text = task!!.description
            dueAtTextView.text = Constants.longFormatter.format(task!!.dueAtDate)

            val reminderCopy: LocalDate = task!!.reminder
            val choice: String? = Constants.reminderChoiceFromLocalDate(reminderCopy, dueAtDate!!)
            if(choice != null)
                reminderTextView.text = choice
            else
                reminderTextView.text = Constants.longFormatter.format(reminderDate!!)

            if(task!!.subject != null) subjectTextView.text = resources.getString(task!!.subject!!.actualName)
        }

        dueAtSelector.setOnClickListener { selectDueAtDate() }
        reminderSelector.setOnClickListener { selectReminder()}
        subjectSelector.setOnClickListener {  selectSubject() }
        addTaskButton.setOnClickListener { addTask() }
        backArrowButton.setOnClickListener { goBack() }
    }

    private fun goBack() {
        if(returnToMain) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        else {
            val intent = Intent(this, ViewTaskActivity::class.java)
            intent.putExtra("task", task)
            intent.putExtra("taskIndex", taskIndex)
            startActivity(intent)
        }
    }

    private fun selectDueAtDate() {
        datePickerFragment.newListener(DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val actualMonth: Int = month + 1

            val localDate = LocalDate.of(year, actualMonth, dayOfMonth)

            if(localDate.isBefore(LocalDate.now())) {
                Toast.makeText(this, resources.getString(R.string.create_task_date_mustbeafternow_error), Toast.LENGTH_LONG).show()
                return@OnDateSetListener
            }

            val formatter = DateTimeFormatter.ofPattern(Constants.longDateFormat)
            dueAtTextView.text = formatter.format(localDate)

            dueAtDate = localDate
        })
        datePickerFragment.show(supportFragmentManager, "datePicker")
    }

    private fun selectReminder() {
        val dialog: AlertDialog = UtilFunctions.createRadioButtonAlertDialog(this,
            resources.getString(R.string.create_task_reminder_alert_dialog_title),
            resources.getString(R.string.general_cancel), Constants.reminderChoices, -1, { dialog, index ->
                if(dueAtDate == null) {
                    Toast.makeText(this, resources.getString(R.string.create_task_reminder_mustselectdueatbefore_error),
                        Toast.LENGTH_LONG).show()
                    dialog.cancel()
                    return@createRadioButtonAlertDialog
                }

                if(index == Constants.reminderChoices.size - 1) {
                    datePickerFragment.newListener(DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                        val actualMonth: Int = month + 1
                        val localDate = LocalDate.of(year, actualMonth, dayOfMonth)

                        if(!dueAtDate!!.isAfter(localDate)) {
                            Toast.makeText(this, resources.getString(R.string.create_task_reminder_mustbebefore_error),
                                Toast.LENGTH_LONG).show()
                            return@OnDateSetListener
                        }

                        if(localDate.isBefore(LocalDate.now())) {
                            Toast.makeText(this, resources.getString(R.string.create_task_date_mustbeafternow_error), Toast.LENGTH_LONG).show()
                            return@OnDateSetListener
                        }

                        reminderDate = localDate

                        val formatter = DateTimeFormatter.ofPattern(Constants.longDateFormat)
                        reminderTextView.text = formatter.format(localDate)
                    })

                    datePickerFragment.show(supportFragmentManager, "datePicker")
                }
                else {
                    reminderTextView.text = Constants.reminderChoices[index]

                    reminderDate = dueAtDate!!.minusDays(Constants.reminderDays[index].toLong())
                }

                dialog.dismiss()
            }, { dialog, _ ->
                dialog.cancel()
            })

        dialog.show()
    }

    private fun selectSubject() {
        val subjects: Array<String> = Subject.toStringArray(resources)

        val dialog: AlertDialog = UtilFunctions.createRadioButtonAlertDialog(this,
            resources.getString(R.string.create_task_subject_alert_dialog_title),
            resources.getString(R.string.general_cancel), subjects, -1, { dialog, index ->
                subjectTextView.text = subjects[index]
                subject = Subject.fromName(subjects[index], resources)

                dialog.dismiss()
            }, { dialog, _ ->
                dialog.cancel()
            })

        dialog.show()
    }

    private fun addTask() {
        val title: String = titleEditText.text.trim().toString()
        val description: String = descriptionEditText.text.trim().toString()
        var shouldReturn = false

        if(title.isEmpty()) {
            titleEditText.error = resources.getString(R.string.create_task_edittext_isempty_error)
            shouldReturn = true
        }
        if(dueAtDate == null || reminderDate == null || subject == null) {
            Toast.makeText(this, resources.getString(R.string.create_task_informationmissing_error), Toast.LENGTH_LONG).show()
            shouldReturn = true
        }

        if(shouldReturn)
            return

        if(returnToMain) {
            DatabaseInterface.insertData(title, description, dueAtDate!!, reminderDate!!, subject!!, this)
            goBack()
        }
        else {
            updateTaskEntry(title, description)

            if(task != null && taskIndex != -1) {
                val intent = Intent(this, ViewTaskActivity::class.java)
                intent.putExtra("task", MainActivity.tasks[taskIndex])
                intent.putExtra("taskIndex", taskIndex)
                startActivity(intent)
            }
            else if(taskIndex == -1)
                throw Exception("When returnToMain == false the taskIndex must not be -1")
        }
    }

    private fun updateTaskEntry(title: String, description: String) {
        val newEntry = TaskEntry(task!!.databaseID, title, description, dueAtDate!!, reminderDate!!, subject!!, task!!.completed)
        DatabaseInterface.updateData(newEntry, this)
        MainActivity.readTasks()
    }

}