package com.endumedia.core.repository


import com.endumedia.core.vo.Product

interface ProductsRepository {
    fun lisProducts(pageSize: Int): Listing<Product>

}
