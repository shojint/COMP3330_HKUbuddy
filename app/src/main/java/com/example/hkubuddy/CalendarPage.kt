package com.example.hkubuddy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class CalendarPage : AppCompatActivity() {
    private lateinit var calendarView: CalendarView
    private lateinit var selectedDateText: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventAdapter
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var refreshNotificationsButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar_page)

        calendarView = findViewById(R.id.calendarView)
        selectedDateText = findViewById(R.id.selectedDate)
        recyclerView = findViewById(R.id.recyclerView)
        refreshNotificationsButton = findViewById(R.id.refreshNotificationsButton)
        val importButton: Button = findViewById(R.id.importButton)

        dbHelper = DatabaseHelper(this)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = EventAdapter(mutableListOf())
        recyclerView.adapter = adapter

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "$year/${month + 1}/$dayOfMonth"
            selectedDateText.text = "Tasks for $selectedDate"
            displayTasksForDate(selectedDate)
        }

        refreshNotificationsButton.setOnClickListener {
            refreshNotifications()
        }

        importButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
            }
            startActivityForResult(Intent.createChooser(intent, "Select .ics File"), REQUEST_CODE)
        }

        setupBottomNavigation()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            val uri: Uri? = data?.data
            val filePath = FileUtils.getPath(this, uri)
            if (filePath != null) {
                ICSParser.parseICSFile(uri, this)
                Toast.makeText(this, "Events Imported Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to get file path", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayTasksForDate(date: String) {
        val tasks = dbHelper.getTasksForDate(date)

        // Log the tasks for debugging
        Log.d("CalendarPage", "Tasks for date $date: ${tasks.size}")
        tasks.forEach { task ->
            Log.d("CalendarPage", "Task: ${task.name}")
        }

        adapter.updateEvents(tasks)
    }

    private fun refreshNotifications() {
        try {
            dbHelper.scheduleNotificationsForAllTasks(this)
            Toast.makeText(this, "Notifications refreshed for all tasks.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to refresh notifications: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }

    companion object {
        private const val REQUEST_CODE = 100
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.no_animation, R.anim.no_animation)
    }

    override fun startActivity(intent: Intent, options: Bundle?) {
        super.startActivity(intent, options)
        overridePendingTransition(R.anim.no_animation, R.anim.no_animation)
    }

    private fun setupBottomNavigation() {
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_calendar -> true // We're already here
                R.id.navigation_task -> {
                    startActivity(Intent(this, TaskPage::class.java))
                    finish()
                    true
                }
                R.id.navigation_browser -> {
                    startActivity(Intent(this, BrowserPage::class.java))
                    finish()
                    true
                }
                R.id.navigation_profile -> {
                    startActivity(Intent(this, ProfilePage::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
        bottomNavigation.selectedItemId = R.id.navigation_calendar
    }
}
