package com.getcard.completeposexample

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.getcard.completeposexample.database.HubDatabase
import com.getcard.completeposexample.database.daos.HubSettingsDao
import com.getcard.completeposexample.databinding.ActivityLoginBinding
import com.sgsistemas.hub_authentication.Authentication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var authentication: Authentication = Authentication()
    private lateinit var database: HubDatabase
    private lateinit var hubSettingsDAO: HubSettingsDao

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityLoginBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        database = HubDatabase.getInstance(this)
        hubSettingsDAO = database.settingsDao()

        binding.loginTextField.setText("80345267000150")
        binding.passwordCodeTextField.setText("SG515t3m45hub")

        CoroutineScope(Dispatchers.Main).launch {
            if (hubSettingsDAO.findFirst()?.id == null){
                Toast.makeText(this@LoginActivity, "Realizar a configuração antes do login", Toast.LENGTH_SHORT).show()
                finish()
                return@launch
            }
        }

        binding.saveButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val login = binding.loginTextField.text.toString()
                val password = binding.passwordCodeTextField.text.toString()
                println("login $login")
                println("password $password")

                if(login.isEmpty() || password.isEmpty()){
                    Toast.makeText(this@LoginActivity, "Deve enviar login e senha", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val result = authentication.login(login, password)

                result.onSuccess {  token ->
                    println("Token - " + token)
                    Toast.makeText(this@LoginActivity, "Login realizado com sucesso", Toast.LENGTH_SHORT).show()
                    hubSettingsDAO.updateToken(token.toString())
                    finish()
                    return@launch
                }.onFailure { error ->
                    Toast.makeText(this@LoginActivity, error.message , Toast.LENGTH_SHORT).show()
                    return@launch
                }

            }
        }
    }
}