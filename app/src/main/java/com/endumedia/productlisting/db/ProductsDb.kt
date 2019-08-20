package com.endumedia.productlisting.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.endumedia.core.vo.Product


/**
 * Created by Nino on 18.08.19
 */

@Database(
    entities = arrayOf(Product::class),
    version = 1,
    exportSchema = false)
abstract class ProductsDb : RoomDatabase() {

    companion object {
        fun create(context: Context, useInMemory : Boolean): ProductsDb {
            val databaseBuilder = if(useInMemory) {
                Room.inMemoryDatabaseBuilder(context, ProductsDb::class.java)
            } else {
                Room.databaseBuilder(context, ProductsDb::class.java, "productsDao.db")
            }
            return databaseBuilder
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    abstract fun productsDao(): ProductsDao
}