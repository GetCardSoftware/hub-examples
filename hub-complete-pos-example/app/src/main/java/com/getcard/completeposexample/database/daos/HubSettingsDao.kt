package com.getcard.completeposexample.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.getcard.completeposexample.database.TablesName
import com.getcard.completeposexample.database.models.HubSettingsModel

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

    @Query("UPDATE ${TablesName.Settings.MAIN} SET token = :token")
    suspend fun updateToken(token: String)

    @Delete
    suspend fun delete(hubSettingsModel: HubSettingsModel)

    @Query("DELETE FROM ${TablesName.Settings.MAIN}")
    suspend fun clearAll()
}