package com.endumedia.core.repository


import com.endumedia.core.vo.Product

interface ProductsRepository {
    fun listProducts(pageSize: Int): Listing<Product>
}
