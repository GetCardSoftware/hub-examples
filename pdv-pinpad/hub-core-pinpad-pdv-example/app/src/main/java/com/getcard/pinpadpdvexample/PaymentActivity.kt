package com.getcard.pinpadpdvexample

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
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Response


class PaymentActivity : AppCompatActivity() {

    companion object {
        const val TAG = "PaymentActivity"
    }

    private lateinit var hubDatabase: HubDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hubDatabase = HubDatabase.getInstance(this)

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


            RetrofitClient.posService.startTransactionOnPos(
                "Bearer $token",
                paymentParams!!
            )
                .enqueue(object : retrofit2.Callback<String> {
                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Toast.makeText(
                            this@PaymentActivity,
                            "Erro ao iniciar transação: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }

                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        val code = response.code()
                        if (code != 201) {
                            Log.d("TAG", "Response: $response")
                            Toast.makeText(
                                this@PaymentActivity,
                                "Erro ao iniciar transação: ${response.body()}",
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                            return
                        }
                        Toast.makeText(
                            this@PaymentActivity,
                            "Transação iniciada com sucesso",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }
                })
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
                "Result - Code: ${paymentResult.code} " +
                        "| Message: ${paymentResult.message} " +
                        "| TransactionID: ${paymentResult.transactionId} " +
                        "| TransactionTimestamp: ${paymentResult.transactionTimestamp}"
            )
            if (paymentResult.code == OperationStatus.SUCCESS) {
                val builder = AlertDialog.Builder(this@PaymentActivity)
                builder.setTitle("Transação Concluida")
                builder.setMessage("ID: ${paymentResult.transactionId} | Timestamp: ${paymentResult.transactionTimestamp} \n Comprovante: ${paymentResult.costumerReceipt}")
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

}