package com.example.hkubuddy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView


class ProfilePage : AppCompatActivity() {
    private lateinit var nameText: TextView
    private lateinit var tasksCompletedText: TextView
    private lateinit var editProfileButton: Button
    private var profileImageUri: Uri? = null
    private lateinit var profileImageView: ImageView
    private var dbHelper: DatabaseHelper? = DatabaseHelper(this)
    private val EDIT_PROFILE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_page)
        setupBottomNavigation()

        nameText = findViewById(R.id.name_text)
        tasksCompletedText = findViewById(R.id.tasks_completed)
        editProfileButton = findViewById(R.id.edit_profile_button)
        profileImageView = findViewById(R.id.profile_picture)

        editProfileButton.setOnClickListener {
            val intent = Intent(this, ProfileEditPage::class.java).apply {
                putExtra("name", nameText.text.toString())
                putExtra("image_uri", profileImageUri)
            }
            startActivityForResult(intent, EDIT_PROFILE_REQUEST)
        }

        // Set the initial value of completed tasks
        tasksCompletedText.text = "${dbHelper?.getCompletedTask()} Task(s) completed"
    }

    override fun onResume() {
        super.onResume()
        // Refresh the completed tasks count whenever the page is visible
        tasksCompletedText.text = "${dbHelper?.getCompletedTask()} Task(s) completed"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_PROFILE_REQUEST && resultCode == RESULT_OK && data != null) {
            // Update the profile with the new data
            val updatedName = data.getStringExtra("name")
            val updatedImageUriString = data.getStringExtra("image_uri")

            nameText.text = updatedName
            updatedImageUriString?.let {
                profileImageUri = Uri.parse(it)
                profileImageView.setImageURI(profileImageUri)
            }
        }
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
                R.id.navigation_calendar -> {
                    startActivity(Intent(this, CalendarPage::class.java))
                    finish()
                    true
                }
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
                R.id.navigation_profile -> true
                else -> false
            }
        }
        bottomNavigation.selectedItemId = R.id.navigation_profile
    }
}

