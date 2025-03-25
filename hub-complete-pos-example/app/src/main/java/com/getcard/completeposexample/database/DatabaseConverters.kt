package com.getcard.completeposexample.database

import androidx.room.TypeConverter
import com.getcard.completeposexample.PaymentProviderType
import com.getcard.hubinterface.OperationStatus
import com.getcard.hubinterface.config.PinpadType
import com.getcard.hubinterface.transaction.InstallmentType
import com.getcard.hubinterface.transaction.PaymentType

class DatabaseConverters {
    @TypeConverter
    fun fromPaymentProviderType(paymentProviderType: PaymentProviderType): String {
        return paymentProviderType.name
    }

    @TypeConverter
    fun toPaymentProviderType(paymentProviderTypeString: String): PaymentProviderType {
        return PaymentProviderType.valueOf(paymentProviderTypeString)
    }

    @TypeConverter
    fun fromPinpadType(pinpadType: PinpadType): String {
        return pinpadType.name
    }

    @TypeConverter
    fun toPinpadType(pinpadTypeString: String): PinpadType {
        return PinpadType.valueOf(pinpadTypeString)
    }

    @TypeConverter
    fun fromPaymentType(paymentType: PaymentType): String {
        return paymentType.name
    }

    @TypeConverter
    fun toPaymentType(paymentTypeString: String): PaymentType {
        return PaymentType.valueOf(paymentTypeString)
    }

    @TypeConverter
    fun fromInstallmentType(installmentType: InstallmentType): String {
        return installmentType.name
    }

    @TypeConverter
    fun toInstallmentType(installmentTypeString: String): InstallmentType {
        return InstallmentType.valueOf(installmentTypeString)
    }

    @TypeConverter
    fun fromOperationStatus(operationStatus: OperationStatus): String {
        return operationStatus.name
    }

    @TypeConverter
    fun toOperationStatus(operationStatusString: String): OperationStatus {
        return OperationStatus.valueOf(operationStatusString)
    }
}