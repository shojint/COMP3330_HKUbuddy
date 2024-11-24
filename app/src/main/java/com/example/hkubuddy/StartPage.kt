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
    private var help_btn: Button? = null
    private var brows_btn: Button? = null
    private var file_btn: Button? = null
    private var dbHelper: DatabaseHelper = DatabaseHelper(this)
    private lateinit var getContent: ActivityResultLauncher<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        if (dbHelper.getTaskCount() > 0){
            startActivity(Intent(this, CalendarPage::class.java))
            finish()
            return
        }else{
            setContentView((R.layout.start_page))

            help_btn = findViewById(R.id.btn_help)
            help_btn!!.setOnClickListener {
                startActivity(
                    Intent(
                        this@StartPage,
                        TutorialPage::class.java
                    )
                )
            }

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }

            brows_btn = findViewById(R.id.btn_brows)
            val url = "https://moodle.hku.hk/"

            brows_btn!!.setOnClickListener {
                if (url.contains("https://") || url.contains("https://")) {
                    val sendIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    val chooser = Intent.createChooser(sendIntent, "Browser")
                    startActivity(chooser)
                }
            }

            file_btn = findViewById(R.id.btn_file)
            file_btn!!.setOnClickListener {
                getContent.launch("text/calendar")
            }
        }
    }
}