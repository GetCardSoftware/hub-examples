package com.getcard.completepinpadexample.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.getcard.completepinpadexample.database.TablesName
import com.getcard.corepinpad.DeviceType

@Entity(
    tableName = TablesName.Settings.MAIN,
)
data class HubSettingsModel(
    @PrimaryKey @ColumnInfo(name = "device_type") val deviceType: DeviceType,
)
