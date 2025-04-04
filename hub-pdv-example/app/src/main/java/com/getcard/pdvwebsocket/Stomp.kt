package com.getcard.pdvwebsocket

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.frame.FrameBody
import org.hildan.krossbow.stomp.headers.StompSendHeaders
import org.hildan.krossbow.stomp.subscribeText
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient
import java.sql.Timestamp
import java.time.Duration
import java.util.UUID

class Stomp(context: Context) {

    private val stompHeaders = mapOf(
    "Authorization" to Configs.AUTH_TOKEN
    )
    private lateinit var session: StompSession
    private var transactionResponse: TransactionResponse = TransactionResponse()

    companion object {
        const val TAG = "PDV"
    }

    private val loadingDialog = LoadingDialog(context)

    private suspend fun startConnection() {
        if(::session.isInitialized){
            return
        }
        val okHttpClient = OkHttpClient.Builder()
            .callTimeout(Duration.ofMinutes(1))
            .pingInterval(Duration.ofSeconds(2))
            .build()

        val wsClient = OkHttpWebSocketClient(okHttpClient)
        val client = StompClient(wsClient)

        session = client.connect("ws://${Configs.API_BASE_URL}/ws/pdv", customStompConnectHeaders = stompHeaders)
        Log.d(TAG, "Conectado ao WebSocket")
    }

    suspend fun startListenTransaction(){
        startConnection()
        try {
            val subscriptionResponse: Flow<String> = session.subscribeText("/client/topic/response-transaction-pdv")
            coroutineScope {
                subscriptionResponse.collect { msg ->
                    val gson = Gson()
                    transactionResponse = gson.fromJson(msg, TransactionResponse::class.java)
                    Log.d(TAG,"Received sub == : $transactionResponse")
                    loadingDialog.updateMessageAndDismiss(transactionResponse.message.toString(), success = transactionResponse.status == "SUCCESS")
                }
            }

        } catch (e: Exception) {
            Log.d(TAG,"Erro ao conectar ao WebSocket: ${e.message}")
        }
        Log.d(TAG,"Escutando transação...")
    }

    suspend fun sendTransaction(){
        startConnection()
        Log.d(TAG,"Enviando Transação")
        try {
            val transactionRequest = TransactionRequest(
                paymentType = "CREDIT",
                installmentType = "ONE_TIME",
                installmentNumber = 1,
                amount = 100,
                refund = false,
                nsuHost = ""
            )

            val gson = Gson()
            val transactionJson = gson.toJson(transactionRequest)

            val sendHeaders = StompSendHeaders(
                destination = "/app/send-transaction-pdv",
                customHeaders = stompHeaders
            )
            val body = FrameBody.Text(transactionJson)

            Log.d(TAG,"transactionRequest $transactionRequest")
            runBlocking {
                session.send(sendHeaders, body)
                loadingDialog.show()
            }
        } catch (e: Exception) {
            Log.d(TAG,"Erro ao conectar ao WebSocket: ${e.message}")
        }

    }

    data class TransactionRequest(
        val paymentType: String,
        val installmentType: String,
        val installmentNumber: Int,
        val amount: Int,
        val refund: Boolean,
        val nsuHost: String
    )

    data class TransactionResponse(
        val id: UUID? = null,
        val status: String? = "",
        val message: String? = "",
        val transactionTimestamp: Timestamp? = null,
        val nsuHost: String? = null,
        val customerReceipt: String? = null,
        val establishmentReceipt: String? = null,
        val terminalToken: String? = null
    )

}