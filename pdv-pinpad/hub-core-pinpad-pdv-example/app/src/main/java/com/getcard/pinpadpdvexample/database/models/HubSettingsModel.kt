package com.getcard.pinpadpdvexample.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.getcard.corepinpad.DeviceType
import com.getcard.pinpadpdvexample.database.TablesName

@Entity(
    tableName = TablesName.Settings.MAIN,
)
data class HubSettingsModel(
    @PrimaryKey @ColumnInfo(name = "device_type") val deviceType: DeviceType,
    @ColumnInfo(name = "token") val token: String
)
