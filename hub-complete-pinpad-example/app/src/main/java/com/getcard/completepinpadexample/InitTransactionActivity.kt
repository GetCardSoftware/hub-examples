package com.getcard.completepinpadexample

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.getcard.completepinpadexample.databinding.ActivityInitTransactionBinding
import com.getcard.completepinpadexample.extensions.formatValue
import com.getcard.completepinpadexample.extensions.setCurrency
import com.getcard.completepinpadexample.extensions.string
import com.getcard.hubinterface.transaction.InstallmentType
import com.getcard.hubinterface.transaction.PaymentType
import com.getcard.hubinterface.transaction.TransactionParams

class InitTransactionActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "InitTransactionActivity"
    }

    private lateinit var binding: ActivityInitTransactionBinding
    private var chosenInstallmentType: InstallmentType = InstallmentType.ONE_TIME
    private var chosenPaymentType: PaymentType = PaymentType.DEBIT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityInitTransactionBinding.inflate(layoutInflater)
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
        val installmentTypes = mapOf(
            "A Vista" to InstallmentType.ONE_TIME,
            "Parcelado" to InstallmentType.INSTALLMENTS
        )

        val paymentTypeDropdown = binding.paymentTypeDropdownMenu
        Utils.setupDropdownMenu(
            this@InitTransactionActivity,
            paymentTypeDropdown,
            paymentTypes,
            value = chosenPaymentType
        ) { chosenPaymentType = it }

        val installmentTypeDropdown = binding.installmentDropdownMenu
        Utils.setupDropdownMenu(
            this@InitTransactionActivity,
            installmentTypeDropdown,
            installmentTypes,
            value = chosenInstallmentType
        ) {
            chosenInstallmentType = it
            if (it == InstallmentType.INSTALLMENTS) {
                binding.installmentNumberField.isEnabled = true
            } else {
                binding.installmentNumberField.isEnabled = false
                binding.installmentNumberField.setText("1")
            }
        }

        binding.amountTextField.setCurrency()

        binding.startTransactionButton.setOnClickListener {
            val value = binding.amountTextField.string().formatValue()
            if (value.isEmpty() || value.toDouble() == 0.0) {
                binding.amountTextField.error = "Digite o valor da transação"
                Toast.makeText(this, "Digite o valor da transação", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val installmentNumber = binding.installmentNumberField.string()
            if (installmentNumber.isEmpty() || installmentNumber.toInt() == 0) {
                binding.installmentNumberField.error = "Digite o número de parcelas"
                Toast.makeText(this, "Digite o número de parcelas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (chosenInstallmentType == InstallmentType.INSTALLMENTS && installmentNumber.toInt() < 2) {
                binding.installmentNumberField.error = "Parcelamento exige ao menos 2 parcelas"
                Toast.makeText(this, "Parcelamento exige ao menos 2 parcelas", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra(
                "TRANSACTION_PARAMS",
                TransactionParams(
                    value.toBigDecimal(),
                    chosenPaymentType,
                    chosenInstallmentType,
                    installmentNumber.toInt()
                )
            )
            startActivity(intent)
        }

    }
}