package com.getcard.simpleexample


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.getcard.hubinterface.authentication.AuthParams
import com.getcard.hubinterface.transaction.PaymentType
import com.getcard.hubinterface.transaction.TransactionParams
import com.getcard.hubinterface.transaction.TransactionResponse
import com.getcard.simpleexample.databinding.ActivityMainBinding
import java.math.BigDecimal

class MainActivity : AppCompatActivity() {

    companion object {
        // TODO: Inserir o token de autenticação obtido no login na Hub API
        const val AUTH_TOKEN = "SEU TOKEN"
    }

    private lateinit var binding: ActivityMainBinding

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

        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val paymentAcquirer = result.data?.getStringExtra("PAYMENT_ACQUIRER")
            val response = result.data?.getParcelableExtra<TransactionResponse>("TRANSACTION_RESULT")
            if (response != null) {
                Log.d("PaymentActivity", "Response: $response")
                Intent(this, PaymentActivity::class.java).also {
                    it.putExtra("PAYMENT_ACQUIRER", paymentAcquirer)
                    it.putExtra("TRANSACTION_PARAMS", TransactionParams(
                        amount = BigDecimal("2000"),
                        nsuHost = response.nsuHost,
                        transactionTimestamp = response.transactionTimestamp,
                        refund = true,
                        paymentType = PaymentType.CREDIT
                    ))
                    it.putExtra("AUTH_PARAMS", AuthParams(AUTH_TOKEN))
                    startActivity(it)
                }
            }
        }

        val paymentIntent = Intent(this, PaymentActivity::class.java)
        paymentIntent.putExtra(
            "TRANSACTION_PARAMS",
            TransactionParams(
                amount = BigDecimal("2000"),
                paymentType = PaymentType.CREDIT
            )
        )
        paymentIntent.putExtra("AUTH_PARAMS", AuthParams(AUTH_TOKEN))

        binding.transactionButton.setOnClickListener {
            startActivity(paymentIntent)
        }

        // Realiza uma nova transação e estornando-a em seguida
        binding.refundButton.setOnClickListener {
            launcher.launch(paymentIntent)
        }

    }
}
