package com.example.proj1

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : AppCompatActivity() {
    private var help_btn: Button? = null
    private var brows_btn: Button? = null
    private var file_btn: Button? = null
    private var this_file: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView((R.layout.activity_main))

        help_btn = findViewById(R.id.btn_help)
        help_btn!!.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    export_tut::class.java
                )
            )
        })

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        brows_btn = findViewById(R.id.btn_brows)
        val url = "https://moodle.hku.hk/"

        brows_btn!!.setOnClickListener(View.OnClickListener {
            if (url.contains("https://") || url.contains("https://")) {
                val sendIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                val chooser = Intent.createChooser(sendIntent, "Browser")
                startActivity(chooser)
            }
        })

        val pickFile = registerForActivityResult(ActivityResultContracts.GetContent(), ActivityResultCallback {
            this_file = it
        })

        file_btn = findViewById(R.id.btn_file)
        file_btn!!.setOnClickListener {
            pickFile.launch("text/calendar")

            // TODO: make bundle and shift to the calendar page activity

            // replace export_tut with the calendar page
            val i = Intent(this, export_tut::class.java)

            val b  = Bundle()
            b.putString("path", this_file.toString())
            i.putExtras(b)

            //startActivity(i)

            //temp code
            val bg = intent.getStringExtra("path")
            val u = Uri.parse(bg)
        }
    }
}