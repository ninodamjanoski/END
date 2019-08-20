package com.endumedia.productlisting.ui

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.endumedia.core.ui.ProductListingViewModel
import com.endumedia.productlisting.DefaultServiceLocator
import com.endumedia.productlisting.R
import com.endumedia.productlisting.ServiceLocator
import com.endumedia.productlisting.api.ProductsApi
import com.endumedia.productlisting.db.ProductsDb
import com.endumedia.productlisting.repository.FakeProductsApi
import com.endumedia.productlisting.repository.ProductFactory
import com.endumedia.productlisting.repository.ProductsRepositoryImpl
import com.endumedia.productlisting.utils.ViewModelUtil
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * Created by Nino on 19.08.19
 */
class MainActivityTest {


    private lateinit var activity: Activity
    @get:Rule
    var testRule = CountingTaskExecutorRule()

    private val productFactory = ProductFactory()

    private val fakeApi = FakeProductsApi()

    @Before
    fun init() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val db = Room.inMemoryDatabaseBuilder(app, ProductsDb::class.java)
            .allowMainThreadQueries()
            .build()
        val repository = ProductsRepositoryImpl(db.productsDao(),
            fakeApi, Executors.newFixedThreadPool(2))
        val model = ProductListingViewModel(repository)

        val intent = Intent(ApplicationProvider.getApplicationContext(),
            MainActivity::class.java)

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity = InstrumentationRegistry.getInstrumentation().startActivitySync(intent)

        (activity as MainActivity).viewModelFactory = ViewModelUtil.createFor(model)

        // use a controlled service locator w/ fake API
        ServiceLocator.swap(
            object : DefaultServiceLocator(app = app,
                useInMemoryDb = true) {
                override fun getRedditApi(): ProductsApi = fakeApi
            }
        )
//        EspressoTestUtil.disableProgressBarAnimations(activityRule)
    }

    /**
     * Show items loaded on the screen
     */
    @Test
    @Throws(InterruptedException::class, TimeoutException::class)
    fun showSomeResults() {
        fakeApi.addProduct(productFactory.createProduct())
        fakeApi.addProduct(productFactory.createProduct())
        fakeApi.addProduct(productFactory.createProduct())

        val recyclerView = startActivity()
        MatcherAssert.assertThat(recyclerView.adapter?.itemCount, CoreMatchers.`is`(3))
    }

    /**
     * Initial load with empty db failed, show retry button
     */
    @Test
    fun initialLoadFailedShowRetry() {
        fakeApi.failureMsg = "xxx"
        val recyclerView = startActivity()
        MatcherAssert.assertThat(recyclerView.adapter?.itemCount, CoreMatchers.`is`(1))
        MatcherAssert.assertThat(recyclerView.adapter?.getItemViewType(0), CoreMatchers.`is`(R.layout.network_state_item))
    }

//    /**
//     * Initial load with empty db failed, show retry button
//     */
//    @Test
//    fun initialLoadFailedRetryWithSuccess() {
//        fakeApi.failureMsg = "xxx"
//        val recyclerView = startActivity()
//        MatcherAssert.assertThat(recyclerView.adapter?.itemCount, CoreMatchers.`is`(1))
//        MatcherAssert.assertThat(recyclerView.adapter?.getItemViewType(0), CoreMatchers.`is`(R.layout.network_state_item))
//
//        fakeApi.failureMsg = null
//        fakeApi.addProduct(productFactory.createProduct())
//        fakeApi.addProduct(productFactory.createProduct())
//        fakeApi.addProduct(productFactory.createProduct())
//
//        onView(withId(R.id.list)).perform(
//            RecyclerViewActions.actionOnItemAtPosition<ProductViewHolder>
//                (0, MyViewAction.clickChildViewWithId(R.id.retry_button))
//        )
//
//        waitForAdapterChange(recyclerView)
//        MatcherAssert.assertThat(recyclerView.adapter?.itemCount, CoreMatchers.`is`(3))
//    }

    private fun waitForAdapterChange(recyclerView: RecyclerView) {
        val latch = CountDownLatch(1)
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            recyclerView.adapter?.registerAdapterDataObserver(
                object : RecyclerView.AdapterDataObserver() {
                    override fun onChanged() {
                        latch.countDown()
                    }

                    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
//                        latch.countDown()
                    }
                })
        }
        testRule.drainTasks(1, TimeUnit.SECONDS)
        if (recyclerView.adapter?.itemCount ?: 0 > 0) {
            return
        }
        MatcherAssert.assertThat(latch.await(10, TimeUnit.SECONDS), CoreMatchers.`is`(true))
    }

    @Throws(InterruptedException::class, TimeoutException::class)
    private fun startActivity(): RecyclerView {
        val recyclerView = activity.findViewById<RecyclerView>(R.id.list)
        MatcherAssert.assertThat(recyclerView.adapter, CoreMatchers.notNullValue())
        waitForAdapterChange(recyclerView)
        return recyclerView
    }
}