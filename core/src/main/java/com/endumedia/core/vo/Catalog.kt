package com.endumedia.core.vo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


/**
 * Created by Nino on 16.08.19
 */
data class Catalog(val products: List<Product>, val title: String,
                   @SerializedName("product_count") val count: Int)

@Entity
data class Product(@PrimaryKey(autoGenerate = true) val storeId: Long,
                   val id: Long, val name: String, val price: String, val image: String)