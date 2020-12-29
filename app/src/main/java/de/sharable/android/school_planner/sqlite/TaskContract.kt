package de.sharable.android.school_planner.sqlite

import android.provider.BaseColumns

object TaskContract {

    object TaskEntry : BaseColumns {
        const val TABLE_NAME = "tasks"
        const val COLUMN_TITLE = "title"
        const val COLUMN_DUE_AT = "due_at"
        const val COLUMN_REMINDER = "reminder"
        const val COLUMN_SUBJECT_ABBREVIATION = "subject_abbreviation"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_COMPLETED = "completed"
    }

}