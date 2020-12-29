package de.sharable.android.school_planner.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        val SQL_CREATE_TASKS_DB_TABLE: String = "CREATE TABLE " +
                TaskContract.TaskEntry.TABLE_NAME + " (" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TaskContract.TaskEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                TaskContract.TaskEntry.COLUMN_DUE_AT + " TEXT NOT NULL, " +
                TaskContract.TaskEntry.COLUMN_REMINDER + " TEXT NOT NULL, " +
                TaskContract.TaskEntry.COLUMN_SUBJECT_ABBREVIATION + " TEXT NOT NULL, " +
                TaskContract.TaskEntry.COLUMN_DESCRIPTION + " TEXT, " +
                TaskContract.TaskEntry.COLUMN_COMPLETED + " INTEGER" +
                ");"

        db?.execSQL(SQL_CREATE_TASKS_DB_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS " + TaskContract.TaskEntry.TABLE_NAME)
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME: String = "tasks_database.db"
        const val DATABASE_VERSION: Int = 1
    }

}