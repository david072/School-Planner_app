package de.sharable.android.school_planner

import android.os.Parcel
import android.os.Parcelable
import de.sharable.android.school_planner.util.Constants
import de.sharable.android.school_planner.util.Subject
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Parcelize
class TaskEntry(val databaseID: Int, val title: String, val description: String, val dueAtDate: LocalDate, val reminder: LocalDate,
                val subject: Subject?, var completed: Boolean) : Parcelable {

    fun stringDate(): String {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(Constants.shortDateFormat)
        return formatter.format(dueAtDate)
    }

    override fun describeContents(): Int {
        return 0
    }

}