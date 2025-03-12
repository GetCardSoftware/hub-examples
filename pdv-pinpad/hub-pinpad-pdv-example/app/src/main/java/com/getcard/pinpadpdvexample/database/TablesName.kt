package com.getcard.pinpadpdvexample.database

import com.getcard.pinpadpdvexample.PaymentProviderType

object TablesName {
    object Settings {
        const val MAIN = "settings"
        const val SITEF = "sitef_settings"

        fun getTableName(paymentProviderType: PaymentProviderType): String {
            return when (paymentProviderType) {
                PaymentProviderType.SITEF -> SITEF
                else -> MAIN
            }
        }
    }
}