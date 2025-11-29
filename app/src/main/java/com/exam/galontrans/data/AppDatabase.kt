package com.exam.galontrans.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.exam.galontrans.data.dao.ProductDao
import com.exam.galontrans.data.dao.TransactionDao
import com.exam.galontrans.data.entity.Product
import com.exam.galontrans.data.entity.Transaction

@Database(
    entities = [Product::class, Transaction::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "galon_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}