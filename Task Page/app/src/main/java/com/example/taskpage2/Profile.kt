package com.example.taskpage2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView


class Profile : AppCompatActivity() {
    private lateinit var nameText: TextView
    private lateinit var tasksCompletedText: TextView
    private lateinit var editProfileButton: Button
    private var profileImageUri: Uri? = null
    private lateinit var profileImageView: ImageView

    private val EDIT_PROFILE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.navigation_profile

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_task -> {
                    // Handle Home navigation
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.navigation_browser -> {
                    openBrowser("https://moodle.hku.hk/")
                    true
                }

                R.id.navigation_profile -> {
                    true
                }

                else -> false
            }
        }

        nameText = findViewById<TextView>(R.id.name_text)
//        tasksCompletedText = findViewById<TextView>(R.id.tasks_completed)
        editProfileButton = findViewById<Button>(R.id.edit_profile_button)
        profileImageView = findViewById(R.id.profile_picture)

        editProfileButton.setOnClickListener {
            // Start EditProfileActivity with current data
            val intent = Intent(this, ProfileEdit::class.java).apply {
                putExtra("name", nameText.text.toString())
//                putExtra("tasks", tasksCompletedText.text.toString().split(" ")[1].toInt())
                putExtra("image_uri", profileImageUri)
            }
            startActivityForResult(intent, EDIT_PROFILE_REQUEST)
        }


    }

    private fun openBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_PROFILE_REQUEST && resultCode == RESULT_OK && data != null) {
            // Update the profile with the new data
            val updatedName = data.getStringExtra("name")
//            val updatedTasks = data.getIntExtra("tasks", 0)
            val updatedImageUriString = data.getStringExtra("image_uri")

            nameText.text = updatedName
//            tasksCompletedText.text = "Tasks $updatedTasks completed"
            updatedImageUriString?.let {
                profileImageUri = Uri.parse(it)
                profileImageView.setImageURI(profileImageUri)
            }
        }
    }
}

