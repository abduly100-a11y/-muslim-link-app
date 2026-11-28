package com.mosque.community

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class CrashReportActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val crashDetails = intent.getStringExtra("crash_details")
            ?: getSharedPreferences("crash_logger", Context.MODE_PRIVATE)
                .getString("last_fatal_crash", "No detailed stack trace available.")
            ?: "No detailed stack trace available."

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#14171F")) // Midnight Ink
            setPadding(32, 48, 32, 32)
        }

        val headerText = TextView(this).apply {
            text = "App Startup Diagnostics"
            setTextColor(Color.parseColor("#E2B96A")) // Soft Gold
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(0, 0, 0, 8)
        }
        rootLayout.addView(headerText)

        val subText = TextView(this).apply {
            text = "The application encountered an error while starting up.\nTap 'Copy Error Details' below to copy the diagnostic log."
            setTextColor(Color.parseColor("#9DA8B9"))
            textSize = 12f
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(0, 0, 0, 20)
        }
        rootLayout.addView(subText)

        val buttonLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(0, 0, 0, 20)
        }

        val copyButton = Button(this).apply {
            text = "Copy Error Details"
            setBackgroundColor(Color.parseColor("#E2B96A"))
            setTextColor(Color.parseColor("#14171F"))
            setPadding(28, 12, 28, 12)
            setOnClickListener {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Muslim Link Error Report", crashDetails)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this@CrashReportActivity, "Copied error report to clipboard!", Toast.LENGTH_SHORT).show()
            }
        }
        buttonLayout.addView(copyButton)

        val spacer = TextView(this).apply {
            text = "   "
        }
        buttonLayout.addView(spacer)

        val retryButton = Button(this).apply {
            text = "Restart App"
            setBackgroundColor(Color.parseColor("#2B3345"))
            setTextColor(Color.parseColor("#F5F7FA"))
            setPadding(28, 12, 28, 12)
            setOnClickListener {
                val intent = Intent(this@CrashReportActivity, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            }
        }
        buttonLayout.addView(retryButton)

        rootLayout.addView(buttonLayout)

        val scrollView = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.parseColor("#1B202B"))
            setPadding(16, 16, 16, 16)
        }

        val errorTextView = TextView(this).apply {
            text = crashDetails
            setTextColor(Color.parseColor("#FF6B6B"))
            textSize = 11f
            typeface = Typeface.MONOSPACE
            setPadding(12, 12, 12, 12)
        }
        scrollView.addView(errorTextView)
        rootLayout.addView(scrollView)

        setContentView(rootLayout)
    }
}
