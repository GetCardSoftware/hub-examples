package com.getcard.pinpadpdvexample.notifications

import android.app.Notification
import android.content.Context
import com.getcard.pinpadpdvexample.R

class AvailableNotifications {

    companion object {
        private const val CHANNEL_ID = "WebSocket"

        fun websocketConnected(context: Context): NotificationPair {
            val notification = Notification.Builder(context, CHANNEL_ID)
                .setContentTitle("Websocket")
                .setContentText("Conexão estabelecida!")
                .setSmallIcon(
                    R.drawable.ic_launcher_foreground
                )
                .build()

            return Pair(notification, 1)
        }

        fun websocketDisconnected(context: Context): NotificationPair {
            val notification = Notification.Builder(context, CHANNEL_ID)
                .setContentTitle("Websocket")
                .setContentText("Websocket desconectado!")
                .setSmallIcon(
                    R.drawable.ic_launcher_foreground
                )
                .build()

            return Pair(notification, 2)
        }


        fun websocketConnecting(context: Context): NotificationPair {
            val notification = Notification.Builder(context, CHANNEL_ID)
                .setContentTitle("Websocket")
                .setContentText("Conectando no servidor...")
                .setSmallIcon(
                    R.drawable.ic_launcher_foreground
                )
                .build()

            return Pair(notification, 2)
        }

        fun websocketReconnectingNotification(context: Context): NotificationPair {
            val notification = Notification.Builder(context, CHANNEL_ID)
                .setContentTitle("Websocket")
                .setContentText("Conexão perdida! Reconectando...")
                .setSmallIcon(
                    R.drawable.ic_launcher_foreground
                )
                .build()
            return Pair(notification, 2)
        }
    }
}

