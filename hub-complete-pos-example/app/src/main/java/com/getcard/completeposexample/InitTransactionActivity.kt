package com.getcard.completeposexample

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.getcard.completeposexample.database.HubDatabase
import com.getcard.completeposexample.database.daos.HubSettingsDao
import com.getcard.completeposexample.databinding.ActivityInitTransactionBinding
import com.getcard.completeposexample.extensions.formatValue
import com.getcard.completeposexample.extensions.setCurrency
import com.getcard.completeposexample.extensions.string
import com.getcard.hubinterface.authentication.AuthParams
import com.getcard.hubinterface.transaction.InstallmentType
import com.getcard.hubinterface.transaction.PaymentType
import com.getcard.hubinterface.transaction.TransactionParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InitTransactionActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "InitTransactionActivity"
    }

    private lateinit var binding: ActivityInitTransactionBinding
    private var chosenInstallmentType: InstallmentType = InstallmentType.ONE_TIME
    private var chosenPaymentType: PaymentType = PaymentType.CREDIT
    private lateinit var database: HubDatabase
    private lateinit var hubSettingsDAO: HubSettingsDao


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
        database = HubDatabase.getInstance(this)
        hubSettingsDAO = database.settingsDao()
        var token = ""
        lifecycleScope.launch {
            token = withContext(Dispatchers.IO) {
                hubSettingsDAO.findFirst()?.token.toString()
            }
            println("token1 " + token)
        }

        val paymentTypes = mapOf(
            "Crédito" to PaymentType.CREDIT,
//            "Débito" to PaymentType.DEBIT
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

            val transactionIntent = Intent(this, PaymentActivity::class.java)
            transactionIntent.putExtra(
                "TRANSACTION_PARAMS",
                TransactionParams(
                    value.toBigDecimal(),
                    chosenPaymentType,
                    chosenInstallmentType,
                    installmentNumber.toInt()
                ),
            )
            transactionIntent.putExtra(
                "AUTH_PARAMS",
                AuthParams(
                    "80345267000150",
                    token
                )
            )

            startActivity(transactionIntent)
        }
    }
}