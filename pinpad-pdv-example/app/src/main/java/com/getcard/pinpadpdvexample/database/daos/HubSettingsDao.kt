package com.getcard.pinpadpdvexample.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.getcard.pinpadpdvexample.database.TablesName
import com.getcard.pinpadpdvexample.database.models.HubSettingsModel

@Dao
interface HubSettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(hubSettingsModel: HubSettingsModel)

    @Query("SELECT * FROM ${TablesName.Settings.MAIN} LIMIT 1")
    suspend fun findFirst(): HubSettingsModel?

    @Query("SELECT * FROM ${TablesName.Settings.MAIN}")
    suspend fun findAll(): List<HubSettingsModel?>

    @Update
    suspend fun update(hubSettingsModel: HubSettingsModel)

    @Delete
    suspend fun delete(hubSettingsModel: HubSettingsModel)

    @Query("DELETE FROM ${TablesName.Settings.MAIN}")
    suspend fun clearAll()
}