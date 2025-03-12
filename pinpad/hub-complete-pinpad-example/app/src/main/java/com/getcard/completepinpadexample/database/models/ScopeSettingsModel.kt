package com.getcard.completepinpadexample.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.getcard.completepinpadexample.database.TablesName

@Entity(
    tableName = TablesName.Settings.SCOPE,
)

data class ScopeSettingsModel(
    //Scope settings
    @PrimaryKey @ColumnInfo(name = "id") val id: Int = 1,
    @ColumnInfo(name = "server_ip") val serverIp: String,
    @ColumnInfo(name = "server_port") val serverPort: Int,
    @ColumnInfo(name = "company") val company: String,
    @ColumnInfo(name = "company_branch") val companyBranch: String,
    @ColumnInfo(name = "terminal") val terminal: String,
)
