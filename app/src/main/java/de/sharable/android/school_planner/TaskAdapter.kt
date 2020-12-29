package de.sharable.android.school_planner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat

class TaskAdapter(private val context: Context,
                  private val dataSource: ArrayList<TaskEntry>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.task_listview_item, parent, false)

        val mainConstraintLayout: ConstraintLayout = rowView.findViewById(R.id.task_listview_main_layout)
        val titleTextView: TextView = rowView.findViewById(R.id.task_listview_tasktitle)
        val dueAtTextView: TextView = rowView.findViewById(R.id.task_listview_dueatdate)
        val subjectAbbreviationTextView: TextView = rowView.findViewById(R.id.task_listview_subject_abbreviation)

        val taskEntry: TaskEntry = getItem(position) as TaskEntry
        titleTextView.text = taskEntry.title
        dueAtTextView.text = taskEntry.stringDate()
        subjectAbbreviationTextView.text = taskEntry.subject?.abbreviation

        val color: Int
        if(taskEntry.subject?.color != null)
            color = taskEntry.subject.color
        else
            throw IllegalArgumentException("Color can't be null!")

        subjectAbbreviationTextView.setTextColor(ContextCompat.getColor(context, color))

        if(taskEntry.completed) {
            mainConstraintLayout.setBackgroundResource(R.drawable.viewtask_task_completed_button_bg)
            titleTextView.setTextColor(ContextCompat.getColor(context, R.color.white))
        }

        return rowView
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return dataSource.size
    }

}