package com.getcard.completeposexample.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.getcard.completeposexample.database.TablesName
import com.getcard.completeposexample.database.models.SitefSettingsModel

@Dao
interface SitefSettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sitefSettingsModel: SitefSettingsModel)

    @Query("SELECT * FROM ${TablesName.Settings.SITEF} LIMIT 1")
    suspend fun findFirst(): SitefSettingsModel?

    @Query("SELECT * FROM ${TablesName.Settings.SITEF}")
    suspend fun findAll(): List<SitefSettingsModel?>

    @Update
    suspend fun update(hubSettingsModel: SitefSettingsModel)

    @Delete
    suspend fun delete(hubSettingsModel: SitefSettingsModel)

    @Query("DELETE FROM ${TablesName.Settings.SITEF}")
    suspend fun clearAll()
}