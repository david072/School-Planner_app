package de.sharable.android.school_planner.util

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle

import androidx.fragment.app.DialogFragment
import java.lang.Exception
import java.util.*

class DatePickerFragment : DialogFragment() {

    var listener: DatePickerDialog.OnDateSetListener? = null

    fun newListener(newListener: DatePickerDialog.OnDateSetListener) {
        listener = newListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if(listener == null)
            throw Exception("DatePickerDialog.OnDateSetListener can't be null!")

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(context!!, listener, year, month, day)
    }

}