package com.getcard.simplepinpadexample

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.getcard.hub.scopeprovider.pinpad.ScopeProvider
import com.getcard.hub.sitefprovider.pinpad.SitefProvider
import com.getcard.hubinterface.OperationStatus
import com.getcard.hubinterface.PaymentProvider
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

        val paymentProvider: PaymentProvider

        val paymentAcquirer = intent.getStringExtra("PAYMENT_ACQUIRER")?.let { PaymentAcquirer.valueOf(it) }
        if (paymentAcquirer == null) {
            Toast.makeText(this, "PAYMENT_ACQUIRER não encontrado", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        when (paymentAcquirer) {
            PaymentAcquirer.SITEF -> {
                val providerConfig = PaymentProviderConfig.builder()
                    .setIp("192.168.1.227")
                    .setToken("2823-0548-7763-1206")
                    .setCompany("00000000")
                    .setTerminal("50201136")
                    .build()

                paymentProvider = SitefProvider(providerConfig)
            }
            PaymentAcquirer.SCOPE -> {
                val providerConfig = PaymentProviderConfig.builder()
                    .setIp("177.72.161.156")
                    .setPort(2046u)
                    .setCompany("1283")
                    .setCompanyBranch("0001")
                    .setTerminal("003")
                    .build()

                paymentProvider = ScopeProvider(providerConfig)
            }
        }

        val transactionParams = intent.getParcelableExtra<TransactionParams>("TRANSACTION_PARAMS")

        if(transactionParams == null) {
            Toast.makeText(this, "Nenhum parâmetro transação encontrado", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        Log.d("PaymentActivity", "PaymentParams: $transactionParams")

        lifecycleScope.launch {
            val paymentResult = try {
                paymentProvider.startTransaction(this@PaymentActivity, transactionParams)
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

            if(paymentResult.status == OperationStatus.SUCCESS) {
                val builder = AlertDialog.Builder(this@PaymentActivity)
                builder.setTitle("Transação Concluida")
                builder.setMessage("ID: ${paymentResult.nsuHost} | Timestamp: ${paymentResult.transactionTimestamp} \n Comprovante: ${paymentResult.customerReceipt}")
                builder.setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }
                val dialog = builder.create()
                dialog.show()

                val resultIntent = intent
                resultIntent.putExtra("PAYMENT_ACQUIRER", paymentAcquirer)
                resultIntent.putExtra("TRANSACTION_RESULT", paymentResult)
                setResult(RESULT_OK, resultIntent)
            }else{
                finish()
            }

        }
    }
}
