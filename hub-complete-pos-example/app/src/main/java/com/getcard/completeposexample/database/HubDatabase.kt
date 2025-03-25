package com.getcard.completeposexample.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.getcard.completeposexample.database.daos.HubSettingsDao
import com.getcard.completeposexample.database.daos.SitefSettingsDao
import com.getcard.completeposexample.database.daos.TransactionsDao
import com.getcard.completeposexample.database.models.HubSettingsModel
//import com.getcard.completepinpadexample.database.models.ScopeSettingsModel
import com.getcard.completeposexample.database.models.SitefSettingsModel
import com.getcard.completeposexample.database.models.TransactionsModel

@Database(
    entities = [HubSettingsModel::class, /*ScopeSettingsModel::class,*/ SitefSettingsModel::class, TransactionsModel::class],
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
    abstract fun transactionsDao(): TransactionsDao
//    abstract fun scopeSettingsDao(): ScopeSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: HubDatabase? = null

        fun getInstance(context: Context): HubDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    HubDatabase::class.java, "payment_hub"
                )
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }

}
