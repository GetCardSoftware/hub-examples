package com.getcard.completepinpadexample

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.getcard.completepinpadexample.database.HubDatabase
import com.getcard.completepinpadexample.database.daos.HubSettingsDao
import com.getcard.completepinpadexample.database.daos.ScopeSettingsDao
import com.getcard.completepinpadexample.database.models.HubSettingsModel
import com.getcard.completepinpadexample.database.models.ScopeSettingsModel
import com.getcard.completepinpadexample.databinding.ActivityScopeSettingsBinding
import kotlinx.coroutines.launch

class ScopeSettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScopeSettingsBinding

    private lateinit var database: HubDatabase
    private lateinit var scopeSettingsDao: ScopeSettingsDao
    private lateinit var hubSettingsDao: HubSettingsDao

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityScopeSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = HubDatabase.getInstance(this)
        scopeSettingsDao = database.scopeSettingsDao()
        hubSettingsDao = database.settingsDao()

        binding.saveButton.setOnClickListener {
            submitSettings()
        }

        lifecycleScope.launch {
            val settings = scopeSettingsDao.findFirst()
            if (settings != null) {
                binding.ipTextField.setText(settings.serverIp)
                binding.portTextField.setText(settings.serverPort.toString())
                binding.companyCodeTextField.setText(settings.company)
                binding.companyBranchTextField.setText(settings.companyBranch)
                binding.terminalTextField.setText(settings.terminal)
            }
        }
    }

    private fun submitSettings() {

        val ip = binding.ipTextField.text.toString()
        val port = binding.portTextField.text.toString()
        val token = binding.portTextField.text.toString()
        val company = binding.companyCodeTextField.text.toString()
        val companyBranch = binding.companyBranchTextField.text.toString()
        val terminal = binding.terminalTextField.text.toString()
        if (
            ip.isEmpty() ||
            token.isEmpty() ||
            company.isEmpty() ||
            terminal.isEmpty()
        ) {
            Toast.makeText(
                this@ScopeSettingsActivity,
                "Por favor, preencha todos os campos",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val scopeSettings = ScopeSettingsModel(
            id = 1,
            serverIp = ip,
            serverPort = port.toInt(),
            company = company,
            companyBranch = companyBranch,
            terminal = terminal,
        )

        lifecycleScope.launch {
            scopeSettingsDao.insert(scopeSettings)
            hubSettingsDao.insert(HubSettingsModel(paymentProviderType = PaymentProviderType.SCOPE))
            Toast.makeText(
                this@ScopeSettingsActivity,
                "Configurações salvas com sucesso!",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }
}
