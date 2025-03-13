package com.getcard.pinpadpdvexample.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.getcard.pinpadpdvexample.database.daos.HubSettingsDao
import com.getcard.pinpadpdvexample.database.daos.SitefSettingsDao
import com.getcard.pinpadpdvexample.database.models.HubSettingsModel
import com.getcard.pinpadpdvexample.database.models.SitefSettingsModel

@Database(
    entities = [HubSettingsModel::class, SitefSettingsModel::class],
    version = 1,
    exportSchema = true,
    autoMigrations = [
//        AutoMigration(from = 1, to = 2),
//        AutoMigration(from = 2, to = 3)
    ]
)
@TypeConverters(DatabaseConverters::class)
abstract class HubDatabase : RoomDatabase() {
    abstract fun settingsDao(): HubSettingsDao
    abstract fun sitefSettingsDao(): SitefSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: HubDatabase? = null

        fun getInstance(context: Context): HubDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    HubDatabase::class.java, "pinpad_pdv_example"
                )
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }

}