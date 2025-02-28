package com.getcard.completepinpadexample.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.getcard.completepinpadexample.database.TablesName
import com.getcard.completepinpadexample.database.models.ScopeSettingsModel

@Dao
interface ScopeSettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(scopeSettingsModel: ScopeSettingsModel)

    @Query("SELECT * FROM ${TablesName.Settings.SCOPE} LIMIT 1")
    suspend fun findFirst(): ScopeSettingsModel?

    @Query("SELECT * FROM ${TablesName.Settings.SCOPE}")
    suspend fun findAll(): List<ScopeSettingsModel?>

    @Update
    suspend fun update(hubSettingsModel: ScopeSettingsModel)

    @Delete
    suspend fun delete(hubSettingsModel: ScopeSettingsModel)

    @Query("DELETE FROM ${TablesName.Settings.SCOPE}")
    suspend fun clearAll()
}
