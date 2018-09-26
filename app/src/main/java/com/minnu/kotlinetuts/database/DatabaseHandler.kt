package com.minnu.kotlinetuts.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.widget.Toast
import com.minnu.kotlinetuts.listeners.Completion
import com.minnu.kotlinetuts.listeners.Listener
import com.minnu.kotlinetuts.log.L.Companion.l
import com.minnu.kotlinetuts.model.Tasks


class DatabaseHandler(var context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
            const val TAG = "DatabaseHandler"

            const val DATABASE_NAME = "MyDatabase"
            const val DATABASE_VERSION = 1
            const val TABLE_NAME = "Tasks"

        // Defining Columns
        private const val COLUMN_ID = BaseColumns._ID
        private const val COLUMN_TASK_NAME = "task"
        private const val COLUMN_TASK_DATE = "task_date"
        private const val COLUMN_TASK_TIME = "task_time"
        private const val COLUMN_TASK_START_TIME = "start_time"
        private const val COLUMN_TASK_END_TIME = "end_time"
        private const val COLUMN_TASK_REMAINING_TIME = "remaining_time"
        private const val COLUMN_TASK_STATUS = "task_status"

        const val createTable =
            "CREATE TABLE " + TABLE_NAME +
                    " ( " +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TASK_NAME + " text, " +
                    COLUMN_TASK_DATE + " text, " +
                    COLUMN_TASK_TIME + " text, " +
                    COLUMN_TASK_END_TIME + " text, " +
                    COLUMN_TASK_START_TIME + " text, " +
                    COLUMN_TASK_REMAINING_TIME + " text, " +
                    COLUMN_TASK_STATUS + " text " +
                    " ); "
}

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    fun insertTaskToSQLiteDb(task: Tasks, completion: Completion) {
        try {
            val db = this.writableDatabase
            val cv = ContentValues()
            cv.put(COLUMN_TASK_NAME, task.task)
            cv.put(COLUMN_TASK_DATE, task.taskDate)
            cv.put(COLUMN_TASK_TIME, task.taskTime)
            cv.put(COLUMN_TASK_REMAINING_TIME, task.taskRemainingTime)
            cv.put(COLUMN_TASK_START_TIME, task.taskStartTime)
            cv.put(COLUMN_TASK_STATUS, task.taskStatus)

            val result = db.insert(TABLE_NAME, null, cv)
            if (result == (-1).toLong()) Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            else Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show()

            completion.onComplete(result.toInt())

        } catch (ex: SQLiteException) {
            l(TAG, "SQLiteException: " + ex.message)
        }
    }

    fun readData(listener: Listener) {
        val db = this.readableDatabase
        val query = "SELECT * from $TABLE_NAME"
        val cursor = db.rawQuery("" + query, null)
        if (cursor.columnCount == 0) {
            listener.onComplete(null, -1)
        }
        if (cursor.moveToFirst()) {
            do {
                val tasks = Tasks()
                tasks.id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                tasks.task = cursor.getString(cursor.getColumnIndex(COLUMN_TASK_NAME))
                tasks.taskDate = cursor.getString(cursor.getColumnIndex(COLUMN_TASK_DATE))
                tasks.taskTime = cursor.getString(cursor.getColumnIndex(COLUMN_TASK_TIME))
                tasks.taskStartTime = cursor.getLong(cursor.getColumnIndex(COLUMN_TASK_START_TIME))
                tasks.taskRemainingTime = cursor.getLong(cursor.getColumnIndex(COLUMN_TASK_REMAINING_TIME))
                val taskStatus = cursor.getString(cursor.getColumnIndex(COLUMN_TASK_STATUS))
                l(TAG, "taskStatus : $taskStatus")
                if (taskStatus == "true") {
                    tasks.taskStatus = true
                } else {
                    tasks.taskStatus = false
                }
                tasks.taskCurrentTimeToEndTime = tasks.taskStartTime + tasks.taskRemainingTime

                tasks.print()
                listener.onComplete(tasks, 1)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
    }

    fun deleteTaskFromSQLiteDb(task: Tasks, completion: Completion) {
        try {
            val db = writableDatabase
            val result = db.delete(TABLE_NAME, "$COLUMN_ID  =? ", arrayOf(task.id.toString()))
            completion.onComplete(result)
            db.close()
        } catch (e: SQLiteException) {
            completion.onComplete(-1)
            l(TAG, "SQLiteException: " + e.message)
        }
    }

    fun deleteAllTaskFromSQLiteDb(completion: Completion) {
        val db = writableDatabase
        val result = db.delete(TABLE_NAME, null, null).toLong()
        completion.onComplete(result.toInt())
        db.close()
    }

    fun updateData(tasks: Tasks, completion: Completion) {

        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TASK_NAME, tasks.task)
        values.put(COLUMN_TASK_DATE, tasks.taskDate)
        values.put(COLUMN_TASK_TIME, tasks.taskTime)
        values.put(COLUMN_TASK_REMAINING_TIME, tasks.taskRemainingTime)
        values.put(COLUMN_TASK_START_TIME, tasks.taskStartTime)

        val result = db.update(TABLE_NAME, values, "$COLUMN_ID  =? ", arrayOf(tasks.id.toString())).toLong()
        completion.onComplete(result.toInt())
        db.close()
    }

    fun updateStatus(status: Boolean, id: Int) {
        l(TAG, "ID $id")
        try {
            val db = this.writableDatabase
            val values = ContentValues()
            values.put(COLUMN_TASK_STATUS, status)

            val success = db.update(TABLE_NAME, values, "$COLUMN_ID  =? ", arrayOf(id.toString())).toLong()
            db.close()
        } catch (e: SQLiteException) {
            l(TAG, "${e.message}")
        }
    }
}