package com.getcard.completepinpadexample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.getcard.completepinpadexample.database.HubDatabase
import com.getcard.completepinpadexample.database.daos.HubSettingsDao
import com.getcard.completepinpadexample.databinding.ActivitySettingsBinding
import com.getcard.corepinpad.DeviceType
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var database: HubDatabase
    private lateinit var hubSettingsDAO: HubSettingsDao
    private var choosedDevice = DeviceType.SITEF

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = HubDatabase.getInstance(this)
        hubSettingsDAO = database.settingsDao()

        val items = mapOf(
            "Sitef" to DeviceType.SITEF,
            "Scope" to DeviceType.SCOPE
        )
        val dropdown = binding.deviceTypeDropdownMenu

        Utils.setupDropdownMenu(this, dropdown, items, value = choosedDevice) {
            choosedDevice = it
        }

        lifecycleScope.launch {
            val settings = hubSettingsDAO.findFirst()
            if (settings != null) {
                binding.deviceTypeDropdownMenu.setSelection(
                    items.values.indexOf(settings.deviceType)
                )
            }
        }

        binding.nextButton.setOnClickListener {
            val intent = when (choosedDevice) {
                DeviceType.SITEF -> Intent(
                    this@SettingsActivity,
                    SitefSettingsActivity::class.java
                )
                DeviceType.SCOPE -> Intent(
                    this@SettingsActivity,
                    ScopeSettingsActivity::class.java
                )

                else -> Intent(this@SettingsActivity, SitefSettingsActivity::class.java)
            }
            startActivity(intent)
            finish()
        }
    }
}