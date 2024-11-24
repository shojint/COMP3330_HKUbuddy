package com.example.hkubuddy

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.TimePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale

class TaskPage : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private val taskList = mutableListOf<Task>()
    private lateinit var taskAdapter: TaskAdapter
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.no_animation, R.anim.no_animation)
    }
    override fun startActivity(intent: Intent, options: Bundle?) {
        super.startActivity(intent, options)
        overridePendingTransition(R.anim.no_animation, R.anim.no_animation)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_page)

        dbHelper = DatabaseHelper(this)

        val taskRecyclerView = findViewById<RecyclerView>(R.id.taskRecyclerView)
        val taskAdapter = TaskAdapter(this, taskList){ task ->
            deleteTask(task)
        }
        taskRecyclerView.layoutManager = LinearLayoutManager(this)
        taskRecyclerView.adapter = taskAdapter

        setupRecyclerView()
        loadTasksFromDatabase()

        val taskAddButton = findViewById<FloatingActionButton>(R.id.add_task)
        taskAddButton.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.popup_add_task, null)
            val dialogBuilder = AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Add Task")

            val alertDialog = dialogBuilder.show()

            val taskNameEditText = dialogView.findViewById<EditText>(R.id.taskNameEditText)
            val taskDescriptionEditText = dialogView.findViewById<EditText>(R.id.taskDescriptionEditText)
            val calendarView = dialogView.findViewById<CalendarView>(R.id.popupCalendarView)
            val timePicker = dialogView.findViewById<TimePicker>(R.id.timePicker)
            val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)
            val saveButton = dialogView.findViewById<Button>(R.id.saveButton)

            var selectedDate = ""
            calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                selectedDate = "$year/${month + 1}/$dayOfMonth"
                println(selectedDate)
            }

            cancelButton.setOnClickListener {
                alertDialog.dismiss()
            }

            saveButton.setOnClickListener {
                val taskName = taskNameEditText.text.toString()
                val taskDescription = taskDescriptionEditText.text.toString()
                val hour = timePicker.hour
                val minute = timePicker.minute
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                }
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

                val deadline = "$selectedDate ${timeFormat.format(calendar.time)}"

                if (taskName.isEmpty()) {
                    Toast.makeText(
                        this@TaskPage,
                        "Task name cannot be empty.",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                } else if (selectedDate.isEmpty()) {
                    Toast.makeText(this@TaskPage, "Date cannot be empty.", Toast.LENGTH_LONG)
                        .show()
                    return@setOnClickListener
                } else {
                    val tempTask = Task(0, taskName, taskDescription, deadline, false)
                    val id = dbHelper.addTask(tempTask)
                    if (id != -1L) {
                        val newTask = Task(id, taskName, taskDescription, deadline, false)
                        taskList.add(newTask)
                        taskAdapter.notifyItemInserted(taskList.size - 1)
                        Toast.makeText(
                            this@TaskPage,
                            "Task was added successfully.",
                            Toast.LENGTH_LONG
                        ).show()
                        setupRecyclerView()
                    } else {
                        Toast.makeText(this@TaskPage, "Failed to add task.", Toast.LENGTH_LONG)
                            .show()
                    }
                    alertDialog.dismiss()
                }
            }
        }
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_calendar -> {
                    startActivity(Intent(this, CalendarPage::class.java))
                    finish()
                    true
                }
                R.id.navigation_task -> true // We're already here
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
        bottomNavigation.selectedItemId = R.id.navigation_task
    }

    //Repaint the task list
    fun setupRecyclerView() {
        val taskRecyclerView = findViewById<RecyclerView>(R.id.taskRecyclerView)
        taskAdapter = TaskAdapter(this, taskList) { task ->
            deleteTask(task)
        }
        taskRecyclerView.layoutManager = LinearLayoutManager(this)
        taskRecyclerView.adapter = taskAdapter
    }

    private fun loadTasksFromDatabase() {
        taskList.clear()
        taskList.addAll(dbHelper.getAllTasks())
        if (::taskAdapter.isInitialized) {
            taskAdapter.notifyDataSetChanged()
        }
    }

    private fun deleteTask(task: Task) {
        val deletedRows = dbHelper.deleteTask(this, task.id)
        if (deletedRows > 0) {
            taskList.remove(task)
            taskAdapter.notifyDataSetChanged()
            Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task failed to delete", Toast.LENGTH_SHORT).show()
        }
    }

    class TaskAdapter(
        private val context: Context,
        private val taskList: MutableList<Task>,
        private val onDeleteClick: (Task) -> Unit

    ) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {
        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val taskName: TextView = itemView.findViewById(R.id.taskName)
            val taskDescription: TextView = itemView.findViewById(R.id.taskDescription)
            val taskDeadline: TextView = itemView.findViewById(R.id.taskDeadline)
            val taskComplete: CheckBox = itemView.findViewById(R.id.taskComplete)
            val deleteButton: ImageButton = itemView.findViewById(R.id.taskDelete)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.list_item_task, parent, false)
            return ViewHolder(view)
        }

        private val dbHelper: DatabaseHelper = DatabaseHelper(context)

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val task = taskList[position]
            holder.taskName.text = task.name
            holder.taskDescription.text = task.description
            holder.taskDeadline.text = task.deadline
            holder.taskComplete.setOnCheckedChangeListener(null)
            holder.taskComplete.isChecked = task.isComplete

            holder.taskComplete.setOnCheckedChangeListener { _, isChecked ->
                task.isComplete = isChecked
                dbHelper.updateTask(task)
            }
            holder.deleteButton.setOnClickListener {
                onDeleteClick(task)
            }
        }

        override fun getItemCount() = taskList.size
    }
}
