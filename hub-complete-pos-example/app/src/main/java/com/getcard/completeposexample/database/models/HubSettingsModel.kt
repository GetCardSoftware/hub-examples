package com.getcard.completeposexample.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.getcard.completeposexample.PaymentProviderType
import com.getcard.completeposexample.database.TablesName

@Entity(
    tableName = TablesName.Settings.MAIN,
)
data class HubSettingsModel(
    @PrimaryKey @ColumnInfo(name = "id") val id: Int = 1,
    @ColumnInfo(name = "payment_provider_type") val paymentProviderType: PaymentProviderType,
    @ColumnInfo(name = "token") val token: String,
    )
