package com.endumedia.productlisting.db

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.endumedia.core.vo.Product


/**
 * Created by Nino on 18.08.19
 */
@Dao
interface ProductsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProducts(posts : List<Product>)

    @Query("SELECT * FROM Product")
    fun getProducts() : DataSource.Factory<Int, Product>

    @Query("DELETE FROM Product")
    fun deleteProducts()
}