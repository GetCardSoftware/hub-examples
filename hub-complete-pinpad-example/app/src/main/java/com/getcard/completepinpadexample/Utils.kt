package com.getcard.completepinpadexample

import android.content.Context
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.getcard.completepinpadexample.database.HubDatabase
import com.getcard.hub.scopeprovider.pinpad.ScopeProvider
import com.getcard.hub.sitefprovider.pinpad.SitefProvider
import com.getcard.hubinterface.PaymentProvider
import com.getcard.hubinterface.config.PaymentProviderConfig
import kotlinx.coroutines.runBlocking
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
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
                PaymentProviderType.SCOPE -> {
                    val scopeSettings = runBlocking {
                        database.scopeSettingsDao().findFirst()
                    }
                    scopeSettings?.let {
                        PaymentProviderConfig.builder()
                            .setIp(it.serverIp)
                            .setPort(it.serverPort.toUInt())
                            .setCompany(it.company)
                            .setCompanyBranch(it.companyBranch)
                            .setTerminal(it.terminal)
                            .build()
                    }?.let { ScopeProvider(it) }
                }

            }

            return paymentProvider
        }

        fun getPaymentProviderType(clazz: KClass<*>): PaymentProviderType {
            return when (clazz) {
                ScopeProvider::class -> PaymentProviderType.SCOPE
                SitefProvider::class -> PaymentProviderType.SITEF
                else -> throw IllegalArgumentException("Classe não suportada: $clazz")
            }
        }

        fun formatTimestamp(timestamp: Long): String {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
            return dateTime.format(formatter)
        }

        fun applyMoneyMask(amount: String): String {
//            val paddedText = amount.padStart(3, '0')

            val number = amount.toLongOrNull() ?: 0L
            val formatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
            formatter.minimumFractionDigits = 2
            formatter.maximumFractionDigits = 2
            return formatter.format(number / 100.0).trim()
        }
    }

}