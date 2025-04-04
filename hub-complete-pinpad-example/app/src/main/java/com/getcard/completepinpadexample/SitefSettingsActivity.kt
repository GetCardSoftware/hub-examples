package com.getcard.completepinpadexample

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.getcard.completepinpadexample.database.HubDatabase
import com.getcard.completepinpadexample.database.daos.HubSettingsDao
import com.getcard.completepinpadexample.database.daos.SitefSettingsDao
import com.getcard.completepinpadexample.database.models.HubSettingsModel
import com.getcard.completepinpadexample.database.models.SitefSettingsModel
import com.getcard.completepinpadexample.databinding.ActivitySitefSettingsBinding
import kotlinx.coroutines.launch

class SitefSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySitefSettingsBinding

    private lateinit var database: HubDatabase
    private lateinit var sitefSettingsDao: SitefSettingsDao
    private lateinit var hubSettingsDao: HubSettingsDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySitefSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = HubDatabase.getInstance(this)
        sitefSettingsDao = database.sitefSettingsDao()
        hubSettingsDao = database.settingsDao()

        binding.saveButton.setOnClickListener {
            submitSettings()
        }

        lifecycleScope.launch {
            val settings = sitefSettingsDao.findFirst()
            if (settings != null) {
                binding.ipTextField.setText(settings.serverIp)
                binding.tokenTextField.setText(settings.token)
                binding.companyCodeTextField.setText(settings.company)
                binding.terminalTextField.setText(settings.terminal)
            }
        }
    }


    private fun submitSettings() {

        val ip = binding.ipTextField.text.toString()
        val token = binding.tokenTextField.text.toString()
        val company = binding.companyCodeTextField.text.toString()
        val terminal = binding.terminalTextField.text.toString()
        if (
            ip.isEmpty() ||
            token.isEmpty() ||
            company.isEmpty() ||
            terminal.isEmpty()
        ) {
            Toast.makeText(
                this@SitefSettingsActivity,
                "Por favor, preencha todos os campos",
                Toast.LENGTH_SHORT
            ).show()
            return
        }


        val sitefSettings = SitefSettingsModel(
            id = 1,
            serverIp = ip,
            token = token,
            company = company,
            terminal = terminal,
            tls = false
        )

        lifecycleScope.launch {
            sitefSettingsDao.insert(sitefSettings)
            hubSettingsDao.insert(HubSettingsModel(paymentProviderType = PaymentProviderType.SITEF))
            Toast.makeText(
                this@SitefSettingsActivity,
                "Configurações salvas com sucesso!",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }


    }
}