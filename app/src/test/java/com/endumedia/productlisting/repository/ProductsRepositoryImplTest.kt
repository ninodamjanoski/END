package com.endumedia.productlisting.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.paging.PositionalDataSource
import com.endumedia.core.repository.Listing
import com.endumedia.core.repository.NetworkState
import com.endumedia.core.repository.ProductsRepository
import com.endumedia.core.vo.Product
import com.endumedia.productlisting.db.ProductsDao
import com.endumedia.productlisting.db.ProductsDb
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import java.util.concurrent.Executor

/**
 * Created by Nino on 18.08.19
 */
@RunWith(JUnit4::class)
class ProductsRepositoryImplTest {

    @get:Rule // used to make all live data calls sync
    val instantExecutor = InstantTaskExecutorRule()

    val db = Mockito.mock(ProductsDb::class.java)
    private val dao = Mockito.mock(ProductsDao::class.java)

    private var isDbEmpty: Boolean = false
    private lateinit var repository: ProductsRepository

    private val fakeApi = FakeProductsApi()
    private val networkExecutor = Executor { command -> command.run() }

    private val productFactory = ProductFactory()

    private fun <T> PagedList<T>.loadAllData() {
        do {
            val oldSize = this.loadedCount
            this.loadAround(this.size - 1)
        } while (this.size != oldSize)
    }

    @Before
    fun init() {
        Mockito.`when`(db.productsDao()).thenReturn(dao)
        Mockito.doReturn(getProductsDataSourceFromDb()).`when`(dao).getProducts()
        repository = ProductsRepositoryImpl(db.productsDao(), fakeApi, networkExecutor)
    }

    @After
    fun clear() {
        productFactory.products.clear()
    }

    /**
     * asserts that empty list works fine
     */
    @Test
    fun emptyList() {
        val listing = repository.listProducts(10)
        val pagedList = getPagedList(listing)
        MatcherAssert.assertThat(pagedList.size, CoreMatchers.`is`(0))
    }

    /**
     * asserts that a list w/ single item is loaded properly
     */
    @Test
    fun oneItem() {
        val product = productFactory.createProduct()
        fakeApi.addProduct(product)
        val listing = repository.listProducts(pageSize = 10)
        assertThat(getPagedList(listing), CoreMatchers.`is`(listOf(product)))
    }

    /**
     * asserts loading a full list in multiple pages
     */
    @Test
    fun verifyCompleteList() {
        (0..10).map { productFactory.createProduct() }
        productFactory.products.forEach(fakeApi::addProduct)
        val listing = repository.listProducts(pageSize = 3)
        // trigger loading of the whole list
        val pagedList = getPagedList(listing)
        pagedList.loadAllData()
        assertThat(pagedList, CoreMatchers.`is`(productFactory.products))
    }

    /**
     * asserts the failure message when the initial load cannot complete
     */
    @Test
    fun failToLoadInitial() {
        fakeApi.failureMsg = "xxx"
        val listing = repository.listProducts(pageSize = 3)
        // trigger load
        getPagedList(listing)
        assertThat(getNetworkState(listing), CoreMatchers.`is`(NetworkState.error("xxx")))
    }

    /**
     * asserts the retry logic when initial load request fails
     */
    @Test
    fun retryInInitialLoad() {
        fakeApi.failureMsg = "xxx"
        val listing = repository.listProducts(pageSize = 3)
        // trigger load
        val pagedList = getPagedList(listing)
        assertThat(pagedList.size, CoreMatchers.`is`(0))

        @Suppress("UNCHECKED_CAST")
        val networkObserver = Mockito.mock(Observer::class.java) as Observer<NetworkState>
        listing.networkState.observeForever(networkObserver)

        fakeApi.failureMsg = null
        fakeApi.addProduct(productFactory.createProduct())
        listing.retry()

        assertThat(getNetworkState(listing), CoreMatchers.`is`(NetworkState.LOADED))
        val inOrder = Mockito.inOrder(networkObserver)
        inOrder.verify(networkObserver).onChanged(NetworkState.error("xxx"))
        inOrder.verify(networkObserver).onChanged(NetworkState.LOADING)
        inOrder.verify(networkObserver).onChanged(NetworkState.LOADED)
        inOrder.verifyNoMoreInteractions()
    }


    /**
     * asserts the retry logic when initial load succeeds but subsequent loads fails
     */
    @Test
    fun retryAfterInitialFails() {
        fakeApi.addProduct(productFactory.createProduct())
        val listing = repository.listProducts(pageSize = 2)
        val list = getPagedList(listing)
        assertThat(
            "test sanity, we should not load everything",
            list.size == productFactory.products.size, CoreMatchers.`is`(true)
        )
        assertThat(getNetworkState(listing), CoreMatchers.`is`(NetworkState.LOADED))
        fakeApi.failureMsg = "fail"
        list.loadAllData()
        assertThat(getNetworkState(listing), CoreMatchers.`is`(NetworkState.error("fail")))
        fakeApi.failureMsg = null
        listing.retry()
        list.loadAllData()
        assertThat(getNetworkState(listing), CoreMatchers.`is`(NetworkState.LOADED))
        assertThat(list, CoreMatchers.`is`(productFactory.products))
    }


    /**
     * asserts refresh loads the new data
     */
    @Test
    fun refresh() {
        val postsV1 = (0..5).map { productFactory.createProduct() }
        postsV1.forEach(fakeApi::addProduct)
        val listing = repository.listProducts(pageSize = 5)
        val list = getPagedList(listing)
        list.loadAround(5)
        val postsV2 = (0..10).map { productFactory.createProduct() }
        fakeApi.clear()
        postsV2.forEach(fakeApi::addProduct)

        @Suppress("UNCHECKED_CAST")
        val refreshObserver = Mockito.mock(Observer::class.java) as Observer<NetworkState>
        listing.refreshState.observeForever(refreshObserver)
        listing.refresh()

//        val list2 = getPagedList(listing)
//        list2.loadAround(5)
//        assertThat(list2, CoreMatchers.`is`(postsV2))
        val inOrder = Mockito.inOrder(refreshObserver)
        inOrder.verify(refreshObserver).onChanged(NetworkState.LOADED) // initial state
        inOrder.verify(refreshObserver).onChanged(NetworkState.LOADING)
        inOrder.verify(refreshObserver).onChanged(NetworkState.LOADED)
    }

    /**
     * extract the latest paged list from the listing
     */
    private fun getPagedList(listing: Listing<Product>): PagedList<Product> {
        val observer = LoggingObserver<PagedList<Product>>()
        listing.pagedList.observeForever(observer)
        MatcherAssert.assertThat(observer.value, CoreMatchers.`is`(CoreMatchers.notNullValue()))
        return observer.value!!
    }

    /**
     * extract the latest network state from the listing
     */
    private fun getNetworkState(listing: Listing<Product>) : NetworkState? {
        val networkObserver = LoggingObserver<NetworkState>()
        listing.networkState.observeForever(networkObserver)
        return networkObserver.value
    }

    /**
     * simple observer that logs the latest value it receives
     */
    private class LoggingObserver<T> : Observer<T> {
        var value : T? = null
        override fun onChanged(t: T?) {
            this.value = t
        }
    }

    private fun getProductsDataSourceFromDb(): DataSource.Factory<Int, Product> {
        return object : DataSource.Factory<Int, Product>() {
            override fun create(): DataSource<Int, Product> {
                return object : PositionalDataSource<Product>() {
                    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Product>) {
                        if (isDbEmpty) {
                            callback.onResult(mutableListOf())
                        } else {
                            callback.onResult(productFactory.products)
                        }
                    }

                    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Product>) {
                        if (isDbEmpty) {
                            callback.onResult(mutableListOf(), 0, 0)
                        } else {
                            callback.onResult(productFactory.products, 0, productFactory.products.size)
                        }
                    }
                }
            }
        }
    }
}