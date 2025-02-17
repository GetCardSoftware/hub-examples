package com.getcard.completepinpadexample

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.getcard.completepinpadexample.InitRefundActivity.Companion.TAG
import com.getcard.hubinterface.OperationStatus
import com.getcard.hubinterface.transaction.TransactionParams
import com.getcard.hubinterface.transaction.TransactionResponse
import kotlinx.coroutines.launch

class RefundActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        val refundParams = intent.getParcelableExtra<TransactionParams>("REFUND_PARAMS")

        if (refundParams == null) {
            Log.e(TAG, "Parametros nulos")
            finish()
            return
        }
        Log.d(
            TAG,
            "Refund Params -> $refundParams"
        )

        lifecycleScope.launch {
            val refundResult = try {
                paymentCentral.pay(
                    this@RefundActivity,
                    refundParams
                )
            } catch (e: Exception) {
                Toast.makeText(
                    this@RefundActivity,
                    "Erro ao realizar o estorno: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()

                TransactionResponse(
                    OperationStatus.FAILED,
                    "Erro ao realizar o estorno: ${e.message}",
                    System.currentTimeMillis()
                )
            }

            Log.d(
                TAG,
                "Result - Code: ${refundResult.code} " +
                        "| Message: ${refundResult.message} " +
                        "| TransactionID: ${refundResult.transactionId} " +
                        "| TransactionTimestamp: ${refundResult.transactionTimestamp}"
            )

            if (refundResult.code == OperationStatus.SUCCESS) {
                val builder = AlertDialog.Builder(this@RefundActivity)
                builder.setTitle("Transação Concluida")
                builder.setMessage("ID: ${refundResult.transactionId} | Timestamp: ${refundResult.transactionTimestamp} \n Comprovante: ${refundResult.costumerReceipt}")
                builder.setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }
                val dialog = builder.create()
                dialog.show()

                val resultIntent = intent
                resultIntent.putExtra("TRANSACTION_RESULT", refundResult)
                setResult(RESULT_OK, resultIntent)
            } else {
                finish()
            }
        }
    }
}