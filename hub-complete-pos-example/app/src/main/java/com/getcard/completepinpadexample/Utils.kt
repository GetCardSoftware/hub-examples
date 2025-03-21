package com.getcard.completepinpadexample

import android.content.Context
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.getcard.completepinpadexample.database.HubDatabase
import com.getcard.hub.sitefprovider.pos.SitefProvider
import com.getcard.hubinterface.PaymentProvider
import com.getcard.hubinterface.config.PaymentProviderConfig
import kotlinx.coroutines.runBlocking
import kotlin.reflect.KClass

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

        fun getPaymentProvider(context: Context): PaymentProvider? {

            val database = HubDatabase.getInstance(context)
            val hubSettingsDAO = database.settingsDao()

            val paymentProviderType = runBlocking {
                hubSettingsDAO.findFirst()?.paymentProviderType
            }
            if (paymentProviderType == null) {
                return null
            }

            val paymentProvider = when (paymentProviderType) {
                PaymentProviderType.SITEF -> {
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
                    }?.let { SitefProvider(it) }
                }

                else -> null
            }

            return paymentProvider
        }

        fun getPaymentProviderType(clazz: KClass<*>): PaymentProviderType {
            return when (clazz) {
                SitefProvider::class -> PaymentProviderType.SITEF
                else -> throw IllegalArgumentException("Classe não suportada: $clazz")
            }
        }

    }

}