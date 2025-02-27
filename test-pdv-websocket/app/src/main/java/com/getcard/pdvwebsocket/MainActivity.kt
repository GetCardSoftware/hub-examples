package com.getcard.pdvwebsocket

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.getcard.pdvwebsocket.databinding.ActivityMainBinding
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var stomp = Stomp(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonPdv.setOnClickListener {
            lifecycleScope.launch {
                stomp.startListenTransaction()
            }
        }


        binding.buttonsendPdv.setOnClickListener {
            lifecycleScope.launch {
                stomp.sendTransaction()
            }
        }


    }
}