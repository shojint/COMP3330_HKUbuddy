package com.example.hkubuddy

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.util.Log

data class Task(
    val id: Long = -1,
    val name: String,
    val description: String,
    val deadline: String,
    var isComplete: Boolean = false
)

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1 // Keep version as 1
        private const val DATABASE_NAME = "TaskDB"
        private const val TABLE_TASKS = "tasks"
        private const val KEY_ID = "id"
        private const val KEY_NAME = "name"
        private const val KEY_DESCRIPTION = "description" // New field
        private const val KEY_DEADLINE = "deadline"
        private const val KEY_IS_COMPLETE = "is_complete"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE " + TABLE_TASKS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_DEADLINE + " TEXT," + KEY_IS_COMPLETE + " INTEGER" + ")")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        onCreate(db)
    }

    fun addTask(task: Task): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_NAME, task.name)
            put(KEY_DESCRIPTION, task.description)
            put(KEY_DEADLINE, task.deadline)
            put(KEY_IS_COMPLETE, if (task.isComplete) 1 else 0)
        }
        val id = db.insert(TABLE_TASKS, null, values)
        db.close()
        return id
    }

    fun getAllTasks(): List<Task> {
        val taskList = mutableListOf<Task>()
        val selectQuery = "SELECT * FROM $TABLE_TASKS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        cursor.use {
            if (it.moveToFirst()) {
                val idIndex = it.getColumnIndex(KEY_ID)
                val nameIndex = it.getColumnIndex(KEY_NAME)
                val descriptionIndex = it.getColumnIndex(KEY_DESCRIPTION)
                val deadlineIndex = it.getColumnIndex(KEY_DEADLINE)
                val isCompleteIndex = it.getColumnIndex(KEY_IS_COMPLETE)

                do {
                    val id = if (idIndex != -1) it.getLong(idIndex) else -1L
                    val name = if (nameIndex != -1) it.getString(nameIndex) else ""
                    val description = if (descriptionIndex != -1) it.getString(descriptionIndex) else ""
                    val deadline = if (deadlineIndex != -1) it.getString(deadlineIndex) else ""
                    val isComplete = if (isCompleteIndex != -1) it.getInt(isCompleteIndex) == 1 else false

                    val task = Task(id, name, description, deadline, isComplete)
                    taskList.add(task)
                } while (it.moveToNext())
            }
        }
        db.close()
        return taskList
    }

    fun getTasksForDate(date: String): List<Task> {
        val taskList = mutableListOf<Task>()
        val selectQuery = "SELECT * FROM $TABLE_TASKS WHERE $KEY_DEADLINE LIKE ?"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, arrayOf("$date%"))
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val id = it.getLong(it.getColumnIndexOrThrow(KEY_ID))
                    val name = it.getString(it.getColumnIndexOrThrow(KEY_NAME))
                    val description = it.getString(it.getColumnIndexOrThrow(KEY_DESCRIPTION))
                    val deadline = it.getString(it.getColumnIndexOrThrow(KEY_DEADLINE))
                    val isComplete = it.getInt(it.getColumnIndexOrThrow(KEY_IS_COMPLETE)) == 1

                    val task = Task(id, name, description, deadline, isComplete)
                    taskList.add(task)
                } while (it.moveToNext())
            }
        }
        db.close()
        return taskList
    }

    fun getTaskCount(): Int {
        val db = this.readableDatabase
        val countQuery = "SELECT COUNT(*) FROM $TABLE_TASKS"
        val cursor = db.rawQuery(countQuery, null)
        var count = 0
        cursor.use {
            if (it.moveToFirst()) {
                count = it.getInt(0)
            }
        }
        db.close()
        return count
    }

    fun getCompletedTask(): Int {
        val db = this.readableDatabase
        val countQuery = "SELECT COUNT(*) FROM $TABLE_TASKS WHERE $KEY_IS_COMPLETE = 1"
        val cursor = db.rawQuery(countQuery, null)
        var count = 0
        cursor.use {
            if (it.moveToFirst()) {
                count = it.getInt(0)
            }
        }
        db.close()
        return count
    }

    fun updateTask(task: Task): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(KEY_NAME, task.name)
            put(KEY_DESCRIPTION, task.description)
            put(KEY_DEADLINE, task.deadline)
            put(KEY_IS_COMPLETE, if (task.isComplete) 1 else 0)
        }
        return db.update(TABLE_TASKS, contentValues, "$KEY_ID = ?", arrayOf(task.id.toString()))
    }

    fun deleteTask(taskId: Long): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_TASKS, "$KEY_ID = ?", arrayOf(taskId.toString()))
        db.close()
        return result
    }

}