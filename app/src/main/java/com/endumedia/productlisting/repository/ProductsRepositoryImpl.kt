package com.endumedia.productlisting.repository

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.toLiveData
import com.endumedia.core.repository.Listing
import com.endumedia.core.repository.NetworkState
import com.endumedia.core.repository.ProductsRepository
import com.endumedia.core.vo.Catalog
import com.endumedia.core.vo.Product
import com.endumedia.productlisting.api.ProductsApi
import com.endumedia.productlisting.db.ProductsDao
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor


/**
 * Created by Nino on 18.08.19
 */
class ProductsRepositoryImpl(val dao: ProductsDao,
                             private val productsApi: ProductsApi,
                             private val ioExecutor: Executor) : ProductsRepository {

    private fun insertResponseIntoDb(catalog: Catalog?) {
        catalog?.products?.let {
            dao.insertProducts(it)
        }
    }

    /**
     * When refresh is called, we simply run a fresh network request and when it arrives, clear
     * the database table and insert all new items in a transaction.
     * <p>
     * Since the PagedList already uses a database bound data source, it will automatically be
     * updated after the database transaction is finished.
     */
    @MainThread
    private fun refreshProducts(): LiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING
        productsApi.getProducts().enqueue(
            object : Callback<Catalog> {
                override fun onFailure(call: Call<Catalog>, t: Throwable) {
                    // retrofit calls this on main thread so safe to call set value
                    networkState.value = NetworkState.error(t.message)
                }

                override fun onResponse(
                    call: Call<Catalog>,
                    response: Response<Catalog>
                ) {
                    ioExecutor.execute {
                            dao.deleteProducts()
                            insertResponseIntoDb(response.body())
                        // since we are in bg thread now, post the result.
                        networkState.postValue(NetworkState.LOADED)
                    }
                }
            }
        )
        return networkState
    }

    override fun listProducts(pageSize: Int): Listing<Product> {
        // create a boundary callback which will observe when the user reaches to the edges of
        // the list and update the database with extra data.
        val boundaryCallback = ProductsBoundaryCallback(
            webservice = productsApi,
            handleResponse = this::insertResponseIntoDb,
            ioExecutor = ioExecutor)
        // we are using a mutable live data to trigger refresh requests which eventually calls
        // refresh method and gets a new live data. Each refresh request by the user becomes a newly
        // dispatched data in refreshTrigger
        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger) {
            refreshProducts()
        }

        // We use toLiveData Kotlin extension function here, you could also use LivePagedListBuilder
        val livePagedList = dao.getProducts().toLiveData(
            pageSize = pageSize,
            boundaryCallback = boundaryCallback)

        return Listing(
            pagedList = livePagedList,
            networkState = boundaryCallback.networkState,
            retry = {
                boundaryCallback.helper.retryAllFailed()
            },
            refresh = {
                refreshTrigger.value = null
            },
            refreshState = refreshState
        )
    }
}