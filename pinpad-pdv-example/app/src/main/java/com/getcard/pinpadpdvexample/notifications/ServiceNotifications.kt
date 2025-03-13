package com.getcard.pinpadpdvexample.notifications

import android.app.Service


class ServiceNotifications(private val service: Service) {

    private var actualNotification: NotificationPair? = null

    fun setNotification(notification: NotificationPair) {
        if (actualNotification?.second == notification.second) {
            return
        }
        if (actualNotification != null) {
            service.stopForeground(Service.STOP_FOREGROUND_REMOVE)
        }
        actualNotification = notification
        startNotification()
    }

    private fun startNotification() {
        if (actualNotification == null) {
            throw Exception("Notification is null")
        }
        service.startForeground(actualNotification!!.second, actualNotification!!.first)
    }

}