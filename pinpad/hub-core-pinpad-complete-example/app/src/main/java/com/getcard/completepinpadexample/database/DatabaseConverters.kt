package com.getcard.completepinpadexample.database

import androidx.room.TypeConverter
import com.getcard.corepinpad.DeviceType
import com.getcard.hubinterface.config.PinpadType

class DatabaseConverters {
    @TypeConverter
    fun fromDeviceType(deviceType: DeviceType): String {
        return deviceType.name
    }

    @TypeConverter
    fun toDeviceType(deviceTypeString: String): DeviceType {
        return DeviceType.valueOf(deviceTypeString)
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