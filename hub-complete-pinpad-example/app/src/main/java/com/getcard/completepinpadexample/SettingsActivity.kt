package com.getcard.completepinpadexample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.getcard.completepinpadexample.database.HubDatabase
import com.getcard.completepinpadexample.database.daos.HubSettingsDao
import com.getcard.completepinpadexample.databinding.ActivitySettingsBinding
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var database: HubDatabase
    private lateinit var hubSettingsDAO: HubSettingsDao
    private var choosedProvider = PaymentProviderType.SITEF

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = HubDatabase.getInstance(this)
        hubSettingsDAO = database.settingsDao()

        val items = mapOf(
            "Sitef" to PaymentProviderType.SITEF,
            "Scope" to PaymentProviderType.SCOPE
        )
        val dropdown = binding.paymentProviderDropdownMenu

        Utils.setupDropdownMenu(this, dropdown, items, value = choosedProvider) {
            choosedProvider = it
        }

        lifecycleScope.launch {
            val settings = hubSettingsDAO.findFirst()
            if (settings != null) {
                binding.paymentProviderDropdownMenu.setSelection(
                    items.values.indexOf(settings.paymentProviderType)
                )
            }
        }

        binding.nextButton.setOnClickListener {
            val intent = when (choosedProvider) {
                PaymentProviderType.SITEF -> Intent(
                    this@SettingsActivity,
                    SitefSettingsActivity::class.java
                )
                PaymentProviderType.SCOPE -> Intent(
                    this@SettingsActivity,
                    ScopeSettingsActivity::class.java
                )

            }
            startActivity(intent)
            finish()
        }
    }
}