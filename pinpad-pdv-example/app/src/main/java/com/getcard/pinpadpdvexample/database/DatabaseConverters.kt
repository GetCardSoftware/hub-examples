package com.getcard.pinpadpdvexample.database

import androidx.room.TypeConverter
import com.getcard.hubinterface.config.PinpadType
import com.getcard.pinpadpdvexample.PaymentProviderType

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

}