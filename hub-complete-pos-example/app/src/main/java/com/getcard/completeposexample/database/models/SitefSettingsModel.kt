package com.getcard.completeposexample.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.getcard.completeposexample.database.TablesName

@Entity(
    tableName = TablesName.Settings.SITEF,

    )

data class SitefSettingsModel(
    //Sitef settings
    @PrimaryKey @ColumnInfo(name = "id") val id: Int = 1,
    @ColumnInfo(name = "server_ip") val serverIp: String,
    @ColumnInfo(name = "company") val company: String,
    @ColumnInfo(name = "terminal") val terminal: String,
    @ColumnInfo(name = "token") val token: String,
    @ColumnInfo(name = "tls") val tls: Boolean,
)
