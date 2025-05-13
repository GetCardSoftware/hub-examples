package com.getcard.simpleexample

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.getcard.hub.sitefprovider.pos.SitefProvider
import com.getcard.hubinterface.OperationStatus
import com.getcard.hubinterface.PaymentProvider
import com.getcard.hubinterface.authentication.AuthParams
import com.getcard.hubinterface.config.PaymentProviderConfig
import com.getcard.hubinterface.transaction.TransactionParams
import com.getcard.hubinterface.transaction.TransactionResponse
import kotlinx.coroutines.launch


class PaymentActivity : AppCompatActivity() {

    companion object {
        const val TAG = "PaymentActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Inserir os dados do servidor
        val providerConfig = PaymentProviderConfig.builder()
            .setIp("IP")
            .setToken("TOKEN")
            .setCompany("EMPRESA")
            .setTerminal("TERMINAL")
            .build()

        val paymentProvider = SitefProvider(providerConfig)

        val transactionParams = intent.getParcelableExtra<TransactionParams>("TRANSACTION_PARAMS")
        val authParams = intent.getParcelableExtra<AuthParams>("AUTH_PARAMS")

        if (transactionParams == null) {
            Toast.makeText(this, "Nenhum parâmetro transação encontrado", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        Log.d("PaymentActivity", "PaymentParams: $transactionParams")

        if (authParams == null) {
            Toast.makeText(this, "Nenhum parâmetro de autenticação encontrado", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        Log.d("PaymentActivity", "PaymentParams: $authParams")

        lifecycleScope.launch {
            val paymentResult = try {
                paymentProvider.startTransaction(this@PaymentActivity, transactionParams, authParams)
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

            if(paymentResult.status == OperationStatus.SUCCESS) {
                showPrintDialog(paymentProvider, paymentResult.customerReceipt!!)

                val resultIntent = intent
                resultIntent.putExtra("TRANSACTION_RESULT", paymentResult)
                setResult(RESULT_OK, resultIntent)
            }else{
                finish()
            }

        }
    }

    private fun showPrintDialog(
        paymentProvider: PaymentProvider,
        receipt: String,
    ) {
        val builder = AlertDialog.Builder(this)


        builder.setTitle("Impressão de Cupom")
            .setMessage("Deseja imprimir o cupom do Cliente?")
            .setPositiveButton("Sim") { _, _ ->
                lifecycleScope.launch {
                    val result = paymentProvider.print(this@PaymentActivity, receipt)
                    Log.d(TAG, "Print - Code: ${result.status} | Message: ${result.message}")

                    if (result.status != OperationStatus.SUCCESS) {
                        Toast.makeText(
                            this@PaymentActivity,
                            "Erro ao imprimir cupom: ${result.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                        return@launch
                    }

                    finish()
                }
            }
            .setNegativeButton("Não") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }
}
