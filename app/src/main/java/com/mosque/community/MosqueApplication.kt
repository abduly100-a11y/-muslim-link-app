package com.mosque.community

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log

class MosqueApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val errorTitle = "${throwable.javaClass.simpleName}: ${throwable.message}"
                val stackTrace = Log.getStackTraceString(throwable)
                val fullReport = "FATAL UNCAUGHT CRASH (Thread: ${thread.name})\n$errorTitle\n\n$stackTrace"

                val prefs = getSharedPreferences("crash_logger", Context.MODE_PRIVATE)
                prefs.edit().putString("last_fatal_crash", fullReport).commit()

                val intent = Intent(this, CrashReportActivity::class.java).apply {
                    putExtra("crash_details", fullReport)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
            } catch (t: Throwable) {
                defaultHandler?.uncaughtException(thread, throwable)
            }
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(10)
        }
    }
}
