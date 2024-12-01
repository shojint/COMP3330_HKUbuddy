package com.example.hkubuddy

import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class BrowserPage : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.browser_page)
        setupBottomNavigation()

        webView = findViewById(R.id.webView)

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            cacheMode = WebSettings.LOAD_NO_CACHE
            setSupportMultipleWindows(true)
        }

        webView.clearCache(true)
        webView.clearHistory()

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {
                val newWebView = WebView(this@BrowserPage)
                newWebView.settings.javaScriptEnabled = true
                newWebView.settings.domStorageEnabled = true

                newWebView.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        webView.loadUrl(url)
                        return true
                    }
                }

                val transport = resultMsg?.obj as WebView.WebViewTransport
                transport.webView = newWebView
                resultMsg.sendToTarget()

                return true
            }
        }

        webView.loadUrl("https://moodle.hku.hk/")

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.navigation_browser

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
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
                    webView.loadUrl("https://moodle.hku.hk/")
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
    }

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

                R.id.navigation_browser -> true
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