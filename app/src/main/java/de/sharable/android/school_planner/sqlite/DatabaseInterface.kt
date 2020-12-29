package de.sharable.android.school_planner.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import de.sharable.android.school_planner.TaskEntry
import de.sharable.android.school_planner.util.Subject
import java.lang.Exception
import java.time.LocalDate

class DatabaseInterface {

    companion object {
        private lateinit var writableDatabase: SQLiteDatabase

        fun init(context: Context) {
            if(!this::writableDatabase.isInitialized || !writableDatabase.isOpen) {
                val dbHelper = DatabaseHelper(context)
                writableDatabase = dbHelper.writableDatabase
            }
        }

        fun free() {
            writableDatabase.close()
        }

        fun getAllItemsCursor(): Cursor {
            val sortOrder = "${TaskContract.TaskEntry.COLUMN_DUE_AT} ASC"

            return writableDatabase.query(
                TaskContract.TaskEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                sortOrder
            )
        }

        fun readAllData(cursor: Cursor, context: Context): ArrayList<TaskEntry> {
            init(context)

            val result: ArrayList<TaskEntry> = ArrayList()
            while (cursor.moveToNext()) {
                val id: Int = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
                val title: String = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TITLE))
                val description: String = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_DESCRIPTION))
                val dueAt: LocalDate = LocalDate.parse(
                    cursor.getString(
                        cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_DUE_AT)))
                val reminder: LocalDate = LocalDate.parse(cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_REMINDER)))
                val subject: Subject? = Subject.fromAbbreviation(
                    cursor.getString(
                        cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_SUBJECT_ABBREVIATION)))

                val _completed = cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_COMPLETED))
                val completed: Boolean = _completed != 0

                val taskEntry = TaskEntry(id, title, description, dueAt, reminder, subject, completed)
                result.add(taskEntry)
            }
    
            return result
        }

        fun insertData(title: String, description: String, dueAt: LocalDate, reminder: LocalDate, subject: Subject, context: Context) {
            init(context)

            val contentValues = ContentValues()
            contentValues.put(TaskContract.TaskEntry.COLUMN_TITLE, title)
            contentValues.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, description)
            contentValues.put(TaskContract.TaskEntry.COLUMN_DUE_AT, dueAt.toString())
            contentValues.put(TaskContract.TaskEntry.COLUMN_REMINDER, reminder.toString())
            contentValues.put(TaskContract.TaskEntry.COLUMN_SUBJECT_ABBREVIATION, subject.abbreviation)
            contentValues.put(TaskContract.TaskEntry.COLUMN_COMPLETED, 0)

            writableDatabase.insert(TaskContract.TaskEntry.TABLE_NAME, null, contentValues)
        }

        /*fun insertData(taskEntry: TaskEntry) {
            val contentValues = ContentValues()
            contentValues.put(TaskContract.TaskEntry.COLUMN_TITLE, taskEntry.title)
            contentValues.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, taskEntry.description)
            contentValues.put(TaskContract.TaskEntry.COLUMN_DUE_AT, taskEntry.dueAtDate.toString())
            contentValues.put(TaskContract.TaskEntry.COLUMN_REMINDER, taskEntry.reminder.toString())

            if(taskEntry.subject != null)
                contentValues.put(TaskContract.TaskEntry.COLUMN_SUBJECT_ABBREVIATION, taskEntry.subject.abbreviation)
            else
                throw Exception("The subject must not be null!")

            if(taskEntry.completed)
                contentValues.put(TaskContract.TaskEntry.COLUMN_COMPLETED, 1)
            else
                contentValues.put(TaskContract.TaskEntry.COLUMN_COMPLETED, 0)

            writableDatabase.insert(TaskContract.TaskEntry.TABLE_NAME, null, contentValues)
        }*/

        fun updateData(taskEntry: TaskEntry, context: Context) {
            init(context)

            val contentValues = ContentValues()
            contentValues.put(TaskContract.TaskEntry.COLUMN_TITLE, taskEntry.title)
            contentValues.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, taskEntry.description)
            contentValues.put(TaskContract.TaskEntry.COLUMN_DUE_AT, taskEntry.dueAtDate.toString())
            contentValues.put(TaskContract.TaskEntry.COLUMN_REMINDER, taskEntry.reminder.toString())

            if(taskEntry.subject != null)
                contentValues.put(TaskContract.TaskEntry.COLUMN_SUBJECT_ABBREVIATION, taskEntry.subject.abbreviation)
            else
                throw Exception("The subject must not be null!")

            if(taskEntry.completed)
                contentValues.put(TaskContract.TaskEntry.COLUMN_COMPLETED, 1)
            else
                contentValues.put(TaskContract.TaskEntry.COLUMN_COMPLETED, 0)

            writableDatabase.update(TaskContract.TaskEntry.TABLE_NAME, contentValues, "_id = ?",
                arrayOf(taskEntry.databaseID.toString()))
        }

        fun deleteEntry(id: Int, context: Context) {
            init(context)

            val selection = "${BaseColumns._ID} LIKE ?"
            val selectionArgs = arrayOf(id.toString())
             writableDatabase.delete(TaskContract.TaskEntry.TABLE_NAME, selection, selectionArgs)
        }
    }

}