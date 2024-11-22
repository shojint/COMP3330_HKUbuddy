package com.example.myapplication

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    private lateinit var calendarView: CalendarView
    private lateinit var selectedDateText: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventAdapter
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        calendarView = findViewById(R.id.calendarView)
        selectedDateText = findViewById(R.id.selectedDate)
        recyclerView = findViewById(R.id.recyclerView)
        val importButton: Button = findViewById(R.id.importButton)

        dbHelper = DatabaseHelper(this)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = EventAdapter(mutableListOf())
        recyclerView.adapter = adapter

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            selectedDateText.text = "Tasks for $selectedDate"
            displayTasksForDate(selectedDate)
        }

        importButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
            }
            startActivityForResult(Intent.createChooser(intent, "Select .ics File"), REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            val uri: Uri? = data?.data
            val filePath = FileUtils.getPath(this, uri)
            if (filePath != null) {
                ICSParser.parseICSFile(filePath, this)
                Toast.makeText(this, "Events Imported Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to get file path", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayTasksForDate(date: String) {
        val tasks = mutableListOf<String>()
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val cursor = db.query(
            "events", arrayOf("title"), "date=?", arrayOf(date),
            null, null, null
        )
        cursor.use {
            while (it.moveToNext()) {
                tasks.add(it.getString(0))
            }
        }
        adapter.updateEvents(tasks)
    }

    companion object {
        private const val REQUEST_CODE = 100
    }
}
