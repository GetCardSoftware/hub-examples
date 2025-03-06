package com.getcard.pinpadpdvexample

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import com.getcard.pinpadpdvexample.database.HubDatabase
import com.getcard.pinpadpdvexample.notifications.AvailableNotifications
import com.getcard.pinpadpdvexample.notifications.ServiceNotifications
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class WebSocketService : Service() {

    private lateinit var webSocketManager: Stomp
    private var wakeLock: PowerManager.WakeLock? = null
    private var transactionReceiver: BroadcastReceiver? = null

    private val notificationService = ServiceNotifications(this)


    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    companion object {
        const val TAG = "WebSocketService"
    }

    private lateinit var hubDatabase: HubDatabase

    override fun onCreate() {
        super.onCreate()
        notificationService.setNotification(
            AvailableNotifications.websocketConnecting(
                this
            )
        )
        Log.v("Serviço", "Criando serviço")

        webSocketManager = Stomp(this)

        hubDatabase = HubDatabase.getInstance(applicationContext)

        val powerManager = applicationContext.getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "hubapp::WakeLockTag"
        )
        // Adquirindo o WakeLock para manter o CPU ativo (isso não mantém a tela acesa)
        wakeLock?.acquire()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Starting WebSocket service")
        scope.launch {
            webSocketManager.startConnection()
            Log.v("Serviço", "Serviço iniciado")
            notificationService.setNotification(
                AvailableNotifications.websocketConnected(
                    this@WebSocketService
                )
            )
            webSocketManager.startListenTransaction { response ->
                val result = Intent("com.getcard.pinpadpdvexample.TRANSACTION_RESULT")
                result.putExtra("TRANSACTION_RESULT", response)
                sendBroadcast(result)
            }

        }
        return START_STICKY
    }

    override fun onDestroy() {
        wakeLock?.release()
        super.onDestroy()
        runBlocking {
            webSocketManager.closeConnection()
        }
        job.cancel()

        unregisterReceiver(transactionReceiver)

    }

    override fun onBind(intent: Intent?): IBinder? = null
}
