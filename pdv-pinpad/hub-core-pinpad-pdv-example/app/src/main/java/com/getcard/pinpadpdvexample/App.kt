package com.getcard.pinpadpdvexample

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        //Create Websocket service notification service
        val channel = NotificationChannel(
            "WebSocket",
            "WebSocket Service",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}