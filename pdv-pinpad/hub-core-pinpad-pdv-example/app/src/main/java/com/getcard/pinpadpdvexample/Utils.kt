package com.getcard.pinpadpdvexample

import android.content.Context
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.getcard.corepinpad.DeviceType
import com.getcard.corepinpad.PaymentCentral
import com.getcard.hubinterface.config.PaymentProviderConfig
import com.getcard.pinpadpdvexample.database.HubDatabase
import kotlinx.coroutines.runBlocking

class Utils {
    companion object {
        fun <T> setupDropdownMenu(
            context: Context,
            textView: AutoCompleteTextView,
            items: Map<String, T>,
            value: T? = null,
            onChange: (T) -> Unit,
        ) {
            val keyArray = items.keys.toTypedArray()
            val adapter = ArrayAdapter(
                context,
                android.R.layout.simple_dropdown_item_1line,
                keyArray
            )
            textView.setAdapter(adapter)
            textView.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, position, _ ->
                    onChange(items[keyArray[position]]!!)
                }

            if (value != null) {
                val key = keyArray[items.values.indexOf(value)]
                textView.setText(key, false)
            }
        }

        fun getPaymentCentral(context: Context): PaymentCentral? {

            val database = HubDatabase.getInstance(context)
            val hubSettingsDAO = database.settingsDao()

            val deviceType = runBlocking {
                hubSettingsDAO.findFirst()?.deviceType
            }
            if (deviceType == null) {
                return null
            }

            val providerConfig = when (deviceType) {
                DeviceType.SITEF -> {
                    val sitefSettings = runBlocking {
                        database.sitefSettingsDao().findFirst()
                    }
                    sitefSettings?.let {
                        PaymentProviderConfig.builder()
                            .setIp(it.serverIp)
                            .setToken(it.token)
                            .setCompany(it.company)
                            .setTerminal(it.terminal)
                            .build()
                    }
                }

                else -> null
            }

            if (providerConfig == null) {
                return null
            }

            return PaymentCentral(deviceType, providerConfig)
        }

    }

}