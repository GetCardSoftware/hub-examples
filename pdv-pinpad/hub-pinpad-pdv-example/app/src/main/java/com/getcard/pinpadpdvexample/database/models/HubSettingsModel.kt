package com.getcard.pinpadpdvexample.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.getcard.pinpadpdvexample.PaymentProviderType
import com.getcard.pinpadpdvexample.database.TablesName

@Entity(
    tableName = TablesName.Settings.MAIN,
)
data class HubSettingsModel(
    @PrimaryKey @ColumnInfo(name = "payment_provider_type") val paymentProviderType: PaymentProviderType,
    @ColumnInfo(name = "token") val token: String
)
