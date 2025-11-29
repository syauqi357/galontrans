package com.exam.galontrans.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.exam.galontrans.data.entity.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: Transaction)

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()
}