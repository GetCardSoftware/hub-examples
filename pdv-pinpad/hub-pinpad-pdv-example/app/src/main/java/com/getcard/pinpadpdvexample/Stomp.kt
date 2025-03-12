package com.getcard.pinpadpdvexample

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.getcard.pinpadpdvexample.database.HubDatabase
import com.getcard.pinpadpdvexample.http.TransactionResponseDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.conversions.kxserialization.json.withJsonConversions
import org.hildan.krossbow.stomp.conversions.kxserialization.subscribe
import org.hildan.krossbow.stomp.use
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient
import java.time.Duration

class Stomp(private val context: Context) {

    private val database = HubDatabase.getInstance(context)
    private val hubSettingsDAO = database.settingsDao()


    private val stompHeaders = mapOf(
        "Authorization" to "${
            runBlocking {
                hubSettingsDAO.findFirst()?.token
            }
        }"
    )
    private lateinit var session: StompSession

    companion object {
        const val TAG = "PDV"
    }


    suspend fun startConnection() {
        if (::session.isInitialized) {
            return
        }
        val okHttpClient = OkHttpClient.Builder()
            .callTimeout(Duration.ofMinutes(1))
            .pingInterval(Duration.ofSeconds(2))
            .build()

        val wsClient = OkHttpWebSocketClient(okHttpClient)
        val client = StompClient(wsClient)

        try {
            session = client.connect(
                "ws://dev-hubpay.tefbr.com.br/ws/pdv",
                customStompConnectHeaders = stompHeaders
            )
            Log.d(TAG, "Conectado ao WebSocket")
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao conectar ao WebSocket, verifique o token! \n${e.message}")
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Erro ao conectar ao WebSocket, verifique o token!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    suspend fun closeConnection() {
        if (::session.isInitialized) {
            session.disconnect()
            Log.d(TAG, "Desconectado do websocket")
        }
    }

    suspend fun startListenTransaction(
        onReceived: (TransactionResponseDTO) -> Unit,
    ) {
        try {
            startConnection()
            val jsonStompSession = session.withJsonConversions(configure = {
                ignoreUnknownKeys = true
            })


            jsonStompSession.use { s ->
                val messages: Flow<TransactionResponseDTO> =
                    s.subscribe(
                        destination = "/client/topic/response-transaction-pdv",
                        deserializer = TransactionResponseDTO.serializer()
                    )

                messages.collect { params ->
                    Log.d(TAG, "Received: $params")
                    onReceived(params)
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Erro ao se inscrever no canal: $e")
        }
    }

}