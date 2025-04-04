package com.getcard.completeposexample.database

import com.getcard.completeposexample.PaymentProviderType

object TablesName {
    const val TRANSACTIONS = "transactions"

    object Settings {
        const val MAIN = "settings"
        const val SITEF = "sitef_settings"
        //        const val SCOPE = "scope_settings"

        fun getTableName(paymentProviderType: PaymentProviderType): String {
            return when (paymentProviderType) {
                PaymentProviderType.SITEF -> SITEF
//                PaymentProviderType.SCOPE -> SCOPE
                else -> MAIN
            }
        }
    }
}