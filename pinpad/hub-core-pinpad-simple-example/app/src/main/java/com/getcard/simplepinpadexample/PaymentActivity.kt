package com.getcard.simplepinpadexample

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.getcard.corepinpad.DeviceType
import com.getcard.corepinpad.PaymentCentral
import com.getcard.hubinterface.OperationStatus
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

        val paymentCentral: PaymentCentral

        val deviceType = intent.getStringExtra("DEVICE_TYPE")?.let { DeviceType.valueOf(it) }

        if (deviceType == null) {
            Toast.makeText(this, "DeviceType não encontrado", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        when (deviceType) {
            DeviceType.SITEF -> {
                val providerConfig = PaymentProviderConfig.builder()
                    .setIp("192.168.1.227")
                    .setToken("2823-0548-7763-1206")
                    .setCompany("00000000")
                    .setTerminal("50201136")
                    .build()

                paymentCentral = PaymentCentral(DeviceType.SITEF, providerConfig)
            }
            DeviceType.SCOPE -> {
                val providerConfig = PaymentProviderConfig.builder()
                    .setIp("177.72.161.156")
                    .setPort(2046u)
                    .setCompany("1283")
                    .setCompanyBranch("0001")
                    .setTerminal("003")
                    .build()

                paymentCentral = PaymentCentral(DeviceType.SCOPE, providerConfig)
            }
        }

        val paymentParams = intent.getParcelableExtra<TransactionParams>("TRANSACTION_PARAMS")

        if(paymentParams == null) {
            Toast.makeText(this, "Nenhum parâmetro transação encontrado", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        Log.d("PaymentActivity", "PaymentParams: $paymentParams")

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
                resultIntent.putExtra("TRANSACTION_RESULT", paymentResult)
                setResult(RESULT_OK, resultIntent)
            }else{
                finish()
            }

        }
    }
}
