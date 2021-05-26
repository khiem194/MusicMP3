package com.khiem.musicmp3

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class MyApplication : Application() {
    companion object{
        const val CHANEL_ID = "Chanel Mood Music"
    }
    override fun onCreate() {
        super.onCreate()
        notificationChanel()
    }

    private fun notificationChanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANEL_ID, "NO", importance)
            channel.description = "NO"
            channel.setSound(null, null)
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
}