package com.getcard.pinpadpdvexample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.getcard.hubinterface.OperationStatus
import com.getcard.hubinterface.transaction.TransactionParams
import com.getcard.hubinterface.transaction.TransactionResponse
import com.getcard.pinpadpdvexample.database.HubDatabase
import com.getcard.pinpadpdvexample.http.RetrofitClient
import com.getcard.pinpadpdvexample.http.TransactionResponseDTO
import com.getcard.pinpadpdvexample.http.handleApiError
import com.getcard.pinpadpdvexample.ui.LoadingDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class PaymentActivity : AppCompatActivity() {

    companion object {
        const val TAG = "PaymentActivity"
    }

    private val loadingDialog = LoadingDialog(this)

    private lateinit var hubDatabase: HubDatabase


    private val transactionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.hasExtra("TRANSACTION_RESULT")
            ) {
                val payload =
                    intent.getParcelableExtra<TransactionResponseDTO>("TRANSACTION_RESULT")
                Log.d(TAG, "Recebido TRANSACTION_RESULT: $payload")
                loadingDialog.updateMessageAndDismiss(
                    payload?.message!!,
                    5000,
                    false
                )
                lifecycleScope.launch {
                    delay(5000L)
                    finish()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hubDatabase = HubDatabase.getInstance(this)
        val hubSettingsDAO = hubDatabase.settingsDao()


        val stompHeaders = mapOf(
            "Authorization" to "${
                runBlocking {
                    hubSettingsDAO.findFirst()?.token
                }
            }"
        )

        val paymentCentral = Utils.getPaymentCentral(this)

        if (paymentCentral == null) {
            Toast.makeText(
                this,
                "Nenhuma configuração de pagamento encontrada",
                Toast.LENGTH_LONG
            ).show()
            finish()
            return
        }

        val paymentParams = intent.getParcelableExtra<TransactionParams>("TRANSACTION_PARAMS")

        val usePos = intent.getBooleanExtra("POS", false)


        if (usePos) {

            val token = runBlocking {
                hubDatabase.settingsDao().findFirst()?.token
            }
            if (token == null) {
                Toast.makeText(
                    this,
                    "Modo PDV não configurado!",
                    Toast.LENGTH_LONG
                ).show()
                finish()
                return
            }


            loadingDialog.show()
            try {
                Log.d(Stomp.TAG, "Enviando Transação")
                RetrofitClient.posService.startTransactionOnPos(
                    stompHeaders["Authorization"]!!,
                    paymentParams!!
                ).enqueue(object : retrofit2.Callback<Unit> {
                    override fun onResponse(
                        call: retrofit2.Call<Unit>,
                        response: retrofit2.Response<Unit>
                    ) {
                        if (!response.isSuccessful) {
                            Log.e(TAG, "Error -> Status Code: ${response.code()}")
                            val error = handleApiError(response)
                            Log.e(TAG, "Error -> Body: $error")

                            if (response.code() == 401) {
                                loadingDialog.updateMessageAndDismiss(
                                    "Erro ao realizar transação: Token inválido!",
                                    5000,
                                    false
                                )
                                lifecycleScope.launch {
                                    delay(5000L)
                                    finish()
                                }
                                return
                            }

                            loadingDialog.updateMessageAndDismiss(
                                "Erro ao realizar transação: ${error?.message}",
                                5000,
                                false
                            )
                            lifecycleScope.launch {
                                delay(5000L)
                                finish()
                            }
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<Unit>, t: Throwable) {
                        Log.e(Stomp.TAG, "Erro ao enviar transação: $t")
                        loadingDialog.updateMessageAndDismiss(
                            "Erro ao realizar transação: $t",
                            5000,
                            false
                        )
                        runBlocking {
                            delay(5000L)
                        }
                    }
                }
                )


            } catch (e: Exception) {
                Log.e(TAG, "Erro ao realizar transação: $e")
            }
            return
        }

        if (paymentParams == null) {
            Log.e(TAG, "Parametros nulos")
            finish()
            return
        }

        Log.d(
            TAG,
            "Transaction Params: $paymentParams"
        )

        lifecycleScope.launch {
            val paymentResult = try {
                paymentCentral.pay(this@PaymentActivity, paymentParams)
            } catch (e: Exception) {
                Toast.makeText(
                    this@PaymentActivity,
                    "Erro ao realizar transação: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                TransactionResponse(
                    OperationStatus.FAILED,
                    "Erro ao realizar transação: ${e.message}",
                    System.currentTimeMillis()
                )
            }

            Log.d(
                TAG,
                "Result - $paymentResult"
            )

            if (paymentResult.status == OperationStatus.SUCCESS) {
                val builder = AlertDialog.Builder(this@PaymentActivity)
                builder.setTitle("Transação Concluida")
                builder.setMessage(
                    "NSU: ${paymentResult.nsuHost} | Timestamp: ${paymentResult.transactionTimestamp} " +
                            "\n Comprovante: ${paymentResult.customerReceipt}"
                )
                builder.setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }
                val dialog = builder.create()
                dialog.show()

                val resultIntent = intent
                resultIntent.putExtra("TRANSACTION_RESULT", paymentResult)
                setResult(RESULT_OK, resultIntent)
            } else {
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter("com.getcard.pinpadpdvexample.TRANSACTION_RESULT")
        registerReceiver(transactionReceiver, filter)
    }

}