package com.example.taskpage2


import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.TimePicker
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale
//import com.example.taskpage2

data class Task(val name: String, val deadline: String, var isComplete: Boolean)

class MainActivity : AppCompatActivity() {

    private val taskList = mutableListOf<Task>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "HKU Buddy"

        val taskRecyclerView = findViewById<RecyclerView>(R.id.taskRecyclerView)
        val taskAdapter = TaskAdapter(this, taskList)
        taskRecyclerView.layoutManager = LinearLayoutManager(this)
        taskRecyclerView.adapter = taskAdapter

        val taskAddButton = findViewById<FloatingActionButton>(R.id.add_task)
        taskAddButton.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.popup_add_task, null)
            val dialogBuilder = AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Add Task")

            val alertDialog = dialogBuilder.show()

            val taskNameEditText = dialogView.findViewById<EditText>(R.id.taskNameEditText)
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

                val hour = timePicker.hour
                val minute = timePicker.minute
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                }
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

                val deadline = "$selectedDate ${timeFormat.format(calendar.time)}"
                val newTask = Task(taskName, deadline, false)

                if (taskName.isEmpty()) {
                    Toast.makeText(
                        this@MainActivity,
                        "Task name cannot be empty.",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                } else {
                    taskList.add(newTask)
                    taskAdapter.notifyItemInserted(taskList.size - 1)
                    Toast.makeText(
                        this@MainActivity,
                        "Task was added successfully.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                alertDialog.dismiss()
            }
        }

        // Set the actions of navigation buttons
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.navigation_task

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_task -> {
                    // Handle Home navigation
                    true
                }

                R.id.navigation_browser -> {
                    openBrowser("https://moodle.hku.hk/")
                    true
                }

                R.id.navigation_profile -> {
                    val intent = Intent(this, Profile::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }


    }

    private fun openBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }
}

class TaskAdapter(private val context: Context, private val taskList: List<Task>) :
    RecyclerView.Adapter<TaskAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskName: TextView = itemView.findViewById(R.id.taskName)
        val taskDeadline: TextView = itemView.findViewById(R.id.taskDeadline)
        val taskComplete: CheckBox = itemView.findViewById(R.id.taskComplete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item_task, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = taskList[position]
        holder.taskName.text = task.name
        holder.taskDeadline.text = task.deadline
        holder.taskComplete.isChecked = task.isComplete

        holder.taskComplete.setOnCheckedChangeListener { _, isChecked ->
            task.isComplete = isChecked
            // Update task in your database or wherever it is stored
        }
    }

    override fun getItemCount() = taskList.size
}

