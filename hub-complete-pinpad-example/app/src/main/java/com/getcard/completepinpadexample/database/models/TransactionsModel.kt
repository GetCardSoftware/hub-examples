package com.getcard.completepinpadexample.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.getcard.completepinpadexample.PaymentProviderType
import com.getcard.completepinpadexample.database.TablesName
import com.getcard.hubinterface.OperationStatus
import com.getcard.hubinterface.transaction.InstallmentType
import com.getcard.hubinterface.transaction.PaymentType

@Entity(
    tableName = TablesName.TRANSACTIONS,
)
data class TransactionsModel(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "status") val status: OperationStatus = OperationStatus.UNKNOWN,
    @ColumnInfo(name = "amount") val amount: String,
    @ColumnInfo(name = "payment_type") val paymentType: PaymentType,
    @ColumnInfo(name = "installment_type") val installmentType: InstallmentType,
    @ColumnInfo(name = "installment_number") val installmentNumber: Int,
    @ColumnInfo(name = "nsu_host") val nsuHost: String? = null,
    @ColumnInfo(name = "timestamp") val timestamp: Long? = null,
    @ColumnInfo(name = "is_refunded") val isRefunded: Boolean = false,
    @ColumnInfo(name = "payment_provider_type") val paymentProviderType: PaymentProviderType,
)
