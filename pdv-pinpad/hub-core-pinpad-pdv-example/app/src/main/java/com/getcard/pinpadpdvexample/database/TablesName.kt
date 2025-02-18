package com.getcard.pinpadpdvexample.database

import com.getcard.corepinpad.DeviceType

object TablesName {
    object Settings {
        const val MAIN = "settings"
        const val SITEF = "sitef_settings"

        fun getTableName(deviceType: DeviceType): String {
            return when (deviceType) {
                DeviceType.SITEF -> SITEF
                else -> MAIN
            }
        }
    }
}