package com.getcard.pinpadpdvexample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.getcard.corepinpad.DeviceType
import com.getcard.pinpadpdvexample.database.HubDatabase
import com.getcard.pinpadpdvexample.database.daos.HubSettingsDao
import com.getcard.pinpadpdvexample.database.models.HubSettingsModel
import com.getcard.pinpadpdvexample.databinding.ActivitySettingsBinding
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
            "Sitef" to DeviceType.SITEF
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
                binding.authTokenTextField.setText(settings.token)
            }
        }

        binding.nextButton.setOnClickListener {
            val token = binding.authTokenTextField.text.toString()
            lifecycleScope.launch {
                hubSettingsDAO.insert(
                    HubSettingsModel(
                        deviceType = DeviceType.SITEF,
                        token = token
                    )
                )
            }
            val intent = when (choosedDevice) {
                DeviceType.SITEF -> Intent(
                    this@SettingsActivity,
                    SitefSettingsActivity::class.java
                )

                else -> Intent(this@SettingsActivity, SitefSettingsActivity::class.java)
            }
            startActivity(intent)
            finish()
        }
    }
}