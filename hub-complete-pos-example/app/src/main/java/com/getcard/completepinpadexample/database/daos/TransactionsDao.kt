package com.getcard.completepinpadexample.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.getcard.completepinpadexample.PaymentProviderType
import com.getcard.completepinpadexample.database.TablesName
import com.getcard.completepinpadexample.database.models.TransactionsModel

@Dao
interface TransactionsDao {

    @Insert
    suspend fun insert(transactionsModel: TransactionsModel)

    @Query("SELECT * FROM ${TablesName.TRANSACTIONS} WHERE payment_provider_type = :paymentProviderType ORDER BY id DESC LIMIT 1")
    suspend fun findLast(paymentProviderType: PaymentProviderType): TransactionsModel?

    @Query("SELECT * FROM ${TablesName.TRANSACTIONS}")
    suspend fun findAll(): List<TransactionsModel?>

    @Query("UPDATE ${TablesName.TRANSACTIONS} SET is_refunded = 1 WHERE id = :id")
    suspend fun setRefunded(id: Int)

    @Update
    suspend fun update(transactionsModel: TransactionsModel)

    @Delete
    suspend fun delete(transactionsModel: TransactionsModel)

    @Query("DELETE FROM ${TablesName.TRANSACTIONS}")
    suspend fun clearAll()
}