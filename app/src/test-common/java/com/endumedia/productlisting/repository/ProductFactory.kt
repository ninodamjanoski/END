package com.endumedia.productlisting.repository

import com.endumedia.core.vo.Product
import java.util.concurrent.atomic.AtomicLong


/**
 * Created by Nino on 18.08.19
 */
class ProductFactory {
    private val localCounter = AtomicLong(0)
    private val counter = AtomicLong(0)
    val products = mutableListOf<Product>()
    fun createProduct() : Product {
        val localId = localCounter.incrementAndGet()
        val id = counter.incrementAndGet()
        val product = Product(localId, id, "name $id", "price $id", "")
        products.add(product)
        return product
    }
}