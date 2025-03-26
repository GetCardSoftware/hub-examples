package com.getcard.completepinpadexample

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.getcard.completepinpadexample.database.HubDatabase
import com.getcard.hubinterface.OperationStatus
import com.getcard.hubinterface.transaction.TransactionParams
import com.getcard.hubinterface.transaction.TransactionResponse
import kotlinx.coroutines.launch

class RefundActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "RefundActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val paymentProvider = Utils.getPaymentProvider(this)

        if (paymentProvider == null) {
            Toast.makeText(
                this,
                "Nenhuma configuração de pagamento encontrada",
                Toast.LENGTH_LONG
            ).show()
            finish()
            return
        }

        val transactionId = intent.getIntExtra("TRANSACTION_ID", -1)
        if (transactionId == -1) {
            Log.e(TAG, "TRANSACTION_ID nulo")
            finish()
            return
        }

        val refundParams = intent.getParcelableExtra<TransactionParams>("REFUND_PARAMS")
        if (refundParams == null) {
            Log.e(TAG, "Parametros nulos")
            finish()
            return
        }
        Log.d(TAG, "Refund Params -> $refundParams")

        lifecycleScope.launch {
            val refundResult = try {
                paymentProvider.startTransaction(
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
                "Result - Code: ${refundResult.status} " +
                        "| Message: ${refundResult.message} " +
                        "| NsuHost: ${refundResult.nsuHost} " +
                        "| TransactionTimestamp: ${refundResult.transactionTimestamp}"
            )

            if (refundResult.status == OperationStatus.SUCCESS) {
                HubDatabase.getInstance(this@RefundActivity).transactionsDao()
                    .setRefunded(transactionId)

                val receipts = "Via do Estabelecimento\n" +
                        "${refundResult.establishmentReceipt}\n" +
                        "\n" +
                        "\n" +
                        "Via do Cliente\n" +
                        refundResult.customerReceipt!!

                val builder = AlertDialog.Builder(this@RefundActivity)
                builder.setTitle("Transação Concluida")
                builder.setMessage("ID: ${refundResult.nsuHost} | Timestamp: ${refundResult.transactionTimestamp} \n Comprovante: $receipts")
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