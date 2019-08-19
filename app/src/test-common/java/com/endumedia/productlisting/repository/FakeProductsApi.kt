package com.endumedia.productlisting.repository

import com.endumedia.core.vo.Catalog
import com.endumedia.core.vo.Product
import com.endumedia.productlisting.api.ProductsApi
import retrofit2.Call
import retrofit2.mock.Calls
import java.io.IOException


/**
 * Created by Nino on 18.08.19
 */
class FakeProductsApi : ProductsApi {

    // subreddits keyed by name
    private val model = mutableListOf<Product>()
    var failureMsg: String? = null

    fun addProduct(product: Product) {
        model.add(product)
    }

    fun clear() {
        model.clear()
    }

    override fun getProducts(): Call<Catalog> {
        failureMsg?.let {
            return Calls.failure(IOException(it))
        }

        return Calls.response(Catalog(model, "Title", model.count()))
    }
}