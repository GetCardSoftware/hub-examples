package com.getcard.completepinpadexample.database

import com.getcard.completepinpadexample.PaymentProviderType

object TablesName {
    const val TRANSACTIONS = "transactions"
    
    object Settings {
        const val MAIN = "settings"
        const val SITEF = "sitef_settings"
        const val SCOPE = "scope_settings"

        fun getTableName(paymentProviderType: PaymentProviderType): String {
            return when (paymentProviderType) {
                PaymentProviderType.SITEF -> SITEF
                PaymentProviderType.SCOPE -> SCOPE
                else -> MAIN
            }
        }
    }
}