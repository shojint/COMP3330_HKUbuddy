package com.example.proj1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class export_tut : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_export_tut)

        val imageView = findViewById<ImageView>(R.id.imageView)
        val textView = findViewById<TextView>(R.id.textView3)
        val textView_2 = findViewById<TextView>(R.id.textView4)
        val btn_prev = findViewById<Button>(R.id.btn_prev)
        val btn_next = findViewById<Button>(R.id.btn_next)
        val btn_back = findViewById<Button>(R.id.btn_back)

        val imgs: Array<Int> = arrayOf(
            R.drawable.e1,
            R.drawable.e2,
            R.drawable.e3,
            R.drawable.e4,
            R.drawable.e5
        )

        val instr: Array<String> = arrayOf(
            "Log in to the HKU Moodle website",
            "Click the left arrow tab on the top right corner",
            "Scroll to \"Calendar\", then click \"Import or export calendars\"",
            "Select \"Export calendar\"",
            "Set the calendar settings, click \"Export\", and download the file"
        )

        var idx = 0
        val length = imgs.size
        var this_img = idx + 1

        imageView.setImageResource(imgs[0])
        textView.text = "$this_img / $length"
        textView_2.text = instr[idx]

        btn_next.setOnClickListener {
            idx++
            if (idx >= length) {
                idx = 0
                this_img = 0
            }
            imageView.setImageResource(imgs[idx])
            this_img++
            textView.text = "$this_img / $length"
            textView_2.text = instr[idx]
        }

        btn_prev.setOnClickListener {
            idx--
            if (idx < 0) {
                idx = length - 1
                this_img = length + 1
            }
            imageView.setImageResource(imgs[idx])
            this_img--
            textView.text = "$this_img / $length"
            textView_2.text = instr[idx]
        }

        btn_back!!.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    this@export_tut,
                    MainActivity::class.java
                )
            )
        })
    }
}