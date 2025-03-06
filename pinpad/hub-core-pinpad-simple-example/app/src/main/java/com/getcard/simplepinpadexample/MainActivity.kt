package com.getcard.simplepinpadexample


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.getcard.corepinpad.DeviceType
import com.getcard.hubinterface.transaction.PaymentType
import com.getcard.hubinterface.transaction.TransactionParams
import com.getcard.hubinterface.transaction.TransactionResponse
import com.getcard.simplepinpadexample.databinding.ActivityMainBinding
import java.math.BigDecimal

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var deviceType: DeviceType

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityMainBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val paymentIntent = Intent(this, PaymentActivity::class.java)
        paymentIntent.putExtra("TRANSACTION_PARAMS",TransactionParams(
            amount = BigDecimal("2000"),
            paymentType = PaymentType.CREDIT
        ))

        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val response = result.data?.getParcelableExtra<TransactionResponse>("TRANSACTION_RESULT")
            if (response != null) {
                Log.d("PaymentActivity", "Response: $response")
                val intent = Intent(this, PaymentActivity::class.java)
                intent.putExtra("DEVICE_TYPE", deviceType.name)
                intent.putExtra("TRANSACTION_PARAMS", TransactionParams(
                    amount = BigDecimal("2000"),
                    nsuHost = response.nsuHost,
                    transactionTimestamp = response.transactionTimestamp,
                    refund = true,
                    paymentType = PaymentType.CREDIT
                ))
                startActivity(intent)
            }
        }

        binding.transactionSitefButton.setOnClickListener {
            deviceType = DeviceType.SITEF
            paymentIntent.putExtra("DEVICE_TYPE", deviceType.name)
            startActivity(paymentIntent)
        }

        binding.refundSitefButton.setOnClickListener {
            deviceType = DeviceType.SITEF
            paymentIntent.putExtra("DEVICE_TYPE", deviceType.name)
            launcher.launch(paymentIntent)
        }

        binding.transactionScopeButton.setOnClickListener {
            deviceType = DeviceType.SCOPE
            paymentIntent.putExtra("DEVICE_TYPE", deviceType.name)
            startActivity(paymentIntent)
        }

        binding.refundScopeButton.setOnClickListener {
            deviceType = DeviceType.SCOPE
            paymentIntent.putExtra("DEVICE_TYPE", deviceType.name)
            launcher.launch(paymentIntent)
        }
    }
}
