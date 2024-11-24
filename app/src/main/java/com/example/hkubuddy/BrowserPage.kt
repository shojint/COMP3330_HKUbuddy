package com.example.hkubuddy

import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class BrowserPage : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.browser_page)

        setupBottomNavigation()

        // Initialize WebView
        webView = findViewById(R.id.webView)

        // Load Moodle URL
        webView.loadUrl("https://moodle.hku.hk/")

        // Set up bottom navigation
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.navigation_browser

        // Handle bottom navigation item selection
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_calendar -> {
                    startActivity(Intent(this, CalendarPage::class.java))
                    finish()
                    true
                }
                R.id.navigation_task -> {
                    // Navigate to CalendarPage
                    startActivity(Intent(this, TaskPage::class.java))
                    finish()
                    true
                }
                R.id.navigation_browser -> {
                    // Reload Moodle page
                    webView.loadUrl("https://moodle.hku.hk/")
                    true
                }
                R.id.navigation_profile -> {
                    // Navigate to ProfilePage
                    startActivity(Intent(this, ProfilePage::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Handles back button press to navigate within WebView
     */
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
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
                    true
                }
                R.id.navigation_task -> {
                    startActivity(Intent(this, TaskPage::class.java))
                    true
                }
                R.id.navigation_browser -> true // We're already here

                R.id.navigation_profile -> {
                    startActivity(Intent(this, ProfilePage::class.java))
                    true
                }
                else -> false
            }
        }
        bottomNavigation.selectedItemId = R.id.navigation_browser
    }
}

