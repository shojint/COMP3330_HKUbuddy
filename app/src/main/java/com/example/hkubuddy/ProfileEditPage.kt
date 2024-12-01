package com.example.hkubuddy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity

class ProfileEditPage : AppCompatActivity() {
    private val PICK_IMAGE = 1
//    private val TAKE_PHOTO = 2

    private lateinit var profilePicture: ImageView
    private lateinit var nameEdit: EditText
    private lateinit var tasksEdit: EditText
    private lateinit var saveButton: Button
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_edit_page)

        profilePicture = findViewById(R.id.profile_picture)
        nameEdit = findViewById(R.id.name_edit)
//        tasksEdit = findViewById(R.id.tasks_edit)
        saveButton = findViewById(R.id.save_button)

        // Get existing profile data
        val intent = intent
        val currentName = intent.getStringExtra("name")
//        val currentTasks = intent.getIntExtra("tasks", 0)


        imageUri = intent.getStringExtra("image_uri")?.let { Uri.parse(it) }

//        Log.d("uri", imageUri.toString())
        // Set existing values
        nameEdit.setText(currentName)
//        tasksEdit.setText(currentTasks.toString())
        imageUri?.let {
            profilePicture.setImageURI(it)
        }

        profilePicture.setOnClickListener {
            showImageSelectionDialog()
        }

        saveButton.setOnClickListener {
            val updatedName = nameEdit.text.toString()
//            val updatedTasks = tasksEdit.text.toString().toIntOrNull() ?: 0

            // Show a toast message (replace this with your own saving logic)
            Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()

            // Return the updated data
            val resultIntent = Intent().apply {
                putExtra("name", updatedName)
//                putExtra("tasks", updatedTasks)
                putExtra("image_uri", imageUri?.toString())
            }
            setResult(RESULT_OK, resultIntent)
            Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun showImageSelectionDialog() {
        // Create intent to pick an image from gallery
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.data
            profilePicture.setImageURI(imageUri)
        }
    }
}
