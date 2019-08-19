package com.endumedia.productlisting.repository

import androidx.annotation.MainThread
import androidx.paging.PagedList
import androidx.paging.PagingRequestHelper
import com.endumedia.core.vo.Catalog
import com.endumedia.core.vo.Product
import com.endumedia.productlisting.api.ProductsApi
import com.endumedia.productlisting.util.createStatusLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor


/**
 * Created by Nino on 16.08.19
 */
class ProductsBoundaryCallback(private val webservice: ProductsApi,
                               private val handleResponse: (Catalog?) -> Unit,
                               private val ioExecutor: Executor,
                               private val networkPageSize: Int = 10) : PagedList.BoundaryCallback<Product>() {


    val helper = PagingRequestHelper(ioExecutor)
    val networkState = helper.createStatusLiveData()

    /**
     * Database returned 0 items. We should query the backend for more items.
     */
    @MainThread
    override fun onZeroItemsLoaded() {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
            webservice.getProducts()
                    .enqueue(createWebserviceCallback(it))
        }
    }

    /**
     * User reached to the end of the list.
     * TODO to be implemented when theres paging supported by the backend service
     */
    @MainThread
    override fun onItemAtEndLoaded(itemAtEnd: Product) {
        super.onItemAtEndLoaded(itemAtEnd)
    }

    /**
     * every time it gets new items, boundary callback simply inserts them into the database and
     * paging library takes care of refreshing the list if necessary.
     */
    private fun insertItemsIntoDb(
            response: Response<Catalog>,
            it: PagingRequestHelper.Request.Callback) {
        ioExecutor.execute {
            handleResponse(response.body())
            it.recordSuccess()
        }
    }

    private fun createWebserviceCallback(it: PagingRequestHelper.Request.Callback)
            : Callback<Catalog> {
        return object : Callback<Catalog> {
            override fun onFailure(
                    call: Call<Catalog>,
                    t: Throwable) {
                it.recordFailure(t)
            }

            override fun onResponse(
                    call: Call<Catalog>,
                    response: Response<Catalog>) {
                insertItemsIntoDb(response, it)
            }
        }
    }
}