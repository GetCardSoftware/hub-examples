package com.getcard.completepinpadexample

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.getcard.completepinpadexample.Utils.Companion.getPaymentProviderType
import com.getcard.completepinpadexample.database.HubDatabase
import com.getcard.completepinpadexample.database.models.TransactionsModel
import com.getcard.hubinterface.OperationStatus
import com.getcard.hubinterface.transaction.TransactionParams
import com.getcard.hubinterface.transaction.TransactionResponse
import kotlinx.coroutines.launch


class PaymentActivity : AppCompatActivity() {

    companion object {
        const val TAG = "PaymentActivity"
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

        val paymentParams = intent.getParcelableExtra<TransactionParams>("TRANSACTION_PARAMS")

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
                paymentProvider.startTransaction(this@PaymentActivity, paymentParams)
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
                "Result - Code: ${paymentResult.status} " +
                        "| Message: ${paymentResult.message} " +
                        "| NsuHost: ${paymentResult.nsuHost} " +
                        "| TransactionTimestamp: ${paymentResult.transactionTimestamp}"
            )
            if (paymentResult.status == OperationStatus.SUCCESS) {
                HubDatabase.getInstance(this@PaymentActivity).transactionsDao().insert(
                    TransactionsModel(
                        status = paymentResult.status,
                        amount = paymentResult.transactionAmount.toString(),
                        paymentType = paymentParams.paymentType,
                        installmentType = paymentParams.installmentType,
                        installmentNumber = paymentParams.installmentNumber,
                        paymentProviderType = getPaymentProviderType(paymentProvider::class),
                        nsuHost = paymentResult.nsuHost,
                        timestamp = paymentResult.transactionTimestamp
                    )
                )

                val receipts = "Via do Estabelecimento\n" +
                        "${paymentResult.establishmentReceipt}\n" +
                        "\n" +
                        "\n" +
                        "Via do Cliente\n" +
                        paymentResult.customerReceipt!!
                val builder = AlertDialog.Builder(this@PaymentActivity)
                builder.setTitle("Transação Concluida")
                builder.setMessage("ID: ${paymentResult.nsuHost} | Timestamp: ${paymentResult.transactionTimestamp} \n Comprovante: $receipts")
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