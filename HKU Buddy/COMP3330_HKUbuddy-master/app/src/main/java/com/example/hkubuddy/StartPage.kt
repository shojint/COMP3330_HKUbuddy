package com.example.hkubuddy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class StartPage : AppCompatActivity() {
    private lateinit var helpBtn: Button
    private lateinit var browseBtn: Button
    private lateinit var fileBtn: Button
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var getContent: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the database helper
        dbHelper = DatabaseHelper(this)

        // Handle file import with the activity result launcher
        getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { selectedUri ->
                try {
                    ICSParser.parseICSFile(selectedUri, this)
                    val intent = Intent(this, CalendarPage::class.java)
                    startActivity(intent)
                    finish()
                    Toast.makeText(this, "Events Imported Successfully", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Log.e("StartPage", "Error handling file", e)
                    Toast.makeText(this, "Failed to import events: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Check if tasks exist in the database
        if (dbHelper.getTaskCount() > 0) {
            // Schedule notifications for all tasks in the database
            dbHelper.scheduleNotificationsForAllTasks(this)

            // Transition to the CalendarPage if tasks exist
            startActivity(Intent(this, CalendarPage::class.java))
            finish()
            return
        } else {
            setContentView(R.layout.start_page)

            // Help button action
            helpBtn = findViewById(R.id.btn_help)
            helpBtn.setOnClickListener {
                startActivity(Intent(this@StartPage, TutorialPage::class.java))
            }

            // Handle insets for UI adjustments
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }

            // Browse button action
            browseBtn = findViewById(R.id.btn_brows)
            val url = "https://moodle.hku.hk/"
            browseBtn.setOnClickListener {
                val sendIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                val chooser = Intent.createChooser(sendIntent, "Browser")
                startActivity(chooser)
            }

            // File import button action
            fileBtn = findViewById(R.id.btn_file)
            fileBtn.setOnClickListener {
                getContent.launch("text/calendar")
            }
        }
    }

    override fun onDestroy() {
        // Close the database helper to free resources
        dbHelper.close()
        super.onDestroy()
    }
}
