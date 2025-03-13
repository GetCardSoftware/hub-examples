package com.getcard.pinpadpdvexample


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.getcard.pinpadpdvexample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

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

        binding.transactionButton.setOnClickListener {
            val paymentIntent = Intent(this, InitTransactionActivity::class.java)
            startActivity(paymentIntent)
        }
        binding.startWebsocket.setOnClickListener {
            Log.d("MainActivity", "Conexão Estabelecida (Callback)")
            Log.d("MainActivity", "O serviço de WS não está rodando, iniciando...")
            try {
                val service = Intent(this, WebSocketService::class.java)
                startForegroundService(service)
            } catch (e: Exception) {
                Log.e("MainActivity", "Erro ao iniciar o serviço de WS", e)
            }
        }

        binding.stopWebsocket.setOnClickListener {
            Log.d("MainActivity", "Conexão Estabelecida (Callback)")
            Log.d("MainActivity", "O serviço de WS não está rodando, iniciando...")
            try {
                val service = Intent(this, WebSocketService::class.java)
                stopService(service)
            } catch (e: Exception) {
                Log.e("MainActivity", "Erro ao iniciar o serviço de WS", e)
            }
        }



        binding.refundButton.setOnClickListener {
            val refundIntent = Intent(this, InitRefundActivity::class.java)
            startActivity(refundIntent)
        }

        binding.settingsButton.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }

        binding.startTransactionPosButton.setOnClickListener {
            val paymentIntent = Intent(this, InitTransactionActivity::class.java)
            paymentIntent.putExtra("POS", true)
            startActivity(paymentIntent)
        }


    }


}