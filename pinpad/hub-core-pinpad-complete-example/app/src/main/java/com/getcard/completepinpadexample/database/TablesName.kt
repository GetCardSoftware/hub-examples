package com.getcard.completepinpadexample.database

import com.getcard.corepinpad.DeviceType

object TablesName {
    object Settings {
        const val MAIN = "settings"
        const val SITEF = "sitef_settings"
        const val SCOPE = "scope_settings"

        fun getTableName(deviceType: DeviceType): String {
            return when (deviceType) {
                DeviceType.SITEF -> SITEF
                DeviceType.SCOPE -> SCOPE
                else -> MAIN
            }
        }
    }
}