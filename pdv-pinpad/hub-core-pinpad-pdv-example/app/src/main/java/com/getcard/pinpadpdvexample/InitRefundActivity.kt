package com.getcard.pinpadpdvexample

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.getcard.hubinterface.transaction.PaymentType
import com.getcard.hubinterface.transaction.TransactionParams
import com.getcard.pinpadpdvexample.databinding.ActivityInitRefundBinding
import com.getcard.pinpadpdvexample.extensions.formatValue
import com.getcard.pinpadpdvexample.extensions.setCurrency
import com.getcard.pinpadpdvexample.extensions.string

class InitRefundActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInitRefundBinding
    private var choosedPaymentType: PaymentType = PaymentType.DEBIT

    companion object {
        const val TAG = "RefundActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityInitRefundBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val paymentTypes = mapOf(
            "Crédito" to PaymentType.CREDIT,
            "Débito" to PaymentType.DEBIT
        )

        val paymentTypeDropdown = binding.paymentTypeDropdownMenu
        Utils.setupDropdownMenu(
            this@InitRefundActivity,
            paymentTypeDropdown,
            paymentTypes,
            value = choosedPaymentType
        ) { choosedPaymentType = it }

        binding.amountTextField.setCurrency()


        binding.startRefundButton.setOnClickListener {
            val transactionId = binding.transactionIdText.string()
            val transactionTimestamp = binding.transactionTimestampText.string()
            val amount = binding.amountTextField.string().formatValue()


            if (transactionId.isEmpty() || transactionTimestamp.isEmpty() || amount.isEmpty() || amount.toDouble() == 0.0) {
                Toast.makeText(
                    this,
                    "Por favor, preencha todos os campos",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val refundIntent = Intent(this, RefundActivity::class.java)
            refundIntent.putExtra(
                "REFUND_PARAMS", TransactionParams(
                    refund = true,
                    transactionId = transactionId,
                    transactionTimestamp = transactionTimestamp.toLong(),
                    amount = amount.toBigDecimal(),
                    paymentType = choosedPaymentType,
                )
            )
            startActivity(refundIntent)
        }

    }
}