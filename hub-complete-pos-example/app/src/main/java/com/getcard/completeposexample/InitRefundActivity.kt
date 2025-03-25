package com.getcard.completeposexample

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.getcard.completeposexample.database.HubDatabase
import com.getcard.hubinterface.OperationStatus
import com.getcard.hubinterface.transaction.TransactionParams
import kotlinx.coroutines.launch
import java.math.BigDecimal

class InitRefundActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "InitRefundActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            finish()
        }

        lifecycleScope.launch {
            val transactionModel =
                HubDatabase.getInstance(this@InitRefundActivity).transactionsDao()
                    .findLast(PaymentProviderType.SITEF)

            if (transactionModel == null) {
                Toast.makeText(
                    this@InitRefundActivity,
                    "Não há transações para serem estornadas.",
                    Toast.LENGTH_LONG
                ).show()
                finish()
                return@launch
            }

            if (transactionModel.isRefunded) {
                Toast.makeText(
                    this@InitRefundActivity,
                    "A última transação já está estornada.",
                    Toast.LENGTH_LONG
                ).show()
                finish()
                return@launch
            }

            if (transactionModel.status != OperationStatus.SUCCESS) {
                Toast.makeText(
                    this@InitRefundActivity,
                    "Não é possível estornar a última transação.",
                    Toast.LENGTH_LONG
                ).show()
                finish()
                return@launch
            }

            val refundIntent = Intent(this@InitRefundActivity, RefundActivity::class.java)
            refundIntent.putExtra("TRANSACTION_ID", transactionModel.id)
            refundIntent.putExtra(
                "REFUND_PARAMS",
                TransactionParams(
                    amount = BigDecimal(transactionModel.amount),
                    paymentType = transactionModel.paymentType,
                    nsuHost = transactionModel.nsuHost,
                    transactionTimestamp = transactionModel.timestamp,
                    refund = true
                )
            )
            launcher.launch(refundIntent)
        }
    }
}
