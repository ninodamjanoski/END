package com.endumedia.productlisting.ui

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import com.endumedia.core.ui.ProductListingViewModel
import com.endumedia.core.ui.ProductViewHolder
import com.endumedia.productlisting.DefaultServiceLocator
import com.endumedia.productlisting.R
import com.endumedia.productlisting.ServiceLocator
import com.endumedia.productlisting.api.ProductsApi
import com.endumedia.productlisting.db.ProductsDb
import com.endumedia.productlisting.repository.FakeProductsApi
import com.endumedia.productlisting.repository.ProductFactory
import com.endumedia.productlisting.repository.ProductsRepositoryImpl
import com.endumedia.productlisting.utils.MyViewAction
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
import androidx.test.orchestrator.junit.BundleJUnitUtils.getDescription
import androidx.test.espresso.ViewAction
import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast
import org.hamcrest.Matcher
import org.junit.After


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
        injectViewModel()
    }

    @After
    fun clear() {
        fakeApi.clear()
    }

    /**
     * Show items loaded on the screen
     */
    @Test
    fun showSomeResults() {
        addProductsToApi()

        val recyclerView = startActivity()
        MatcherAssert.assertThat(recyclerView.adapter?.itemCount, CoreMatchers.`is`(3))
    }

    /**
     * Show results then swipe to reload from backend service
     */
    @Test
    fun swipeRefresh() {

        addProductsToApi()
        val recyclerView = startActivity()
        MatcherAssert.assertThat(recyclerView.adapter?.itemCount, CoreMatchers.`is`(3))


        addProductsToApi()
        onView(withId(R.id.swipe_refresh))
            .perform(withCustomConstraints(swipeDown(), isDisplayingAtLeast(85)))

        waitForAdapterChange(recyclerView)
        MatcherAssert.assertThat(recyclerView.adapter?.itemCount, CoreMatchers.`is`(6))
    }

    private fun withCustomConstraints(action: ViewAction, constraints: Matcher<View>): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return constraints
            }

            override fun getDescription(): String {
                return action.description
            }

            override fun perform(uiController: UiController, view: View) {
                action.perform(uiController, view)
            }
        }
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

    /**
     * Initial load with empty db failed, press retry button and load 3 items
     */
    @Test
    fun initialLoadFailedRetryWithSuccess() {
        fakeApi.failureMsg = "xxx"
        val recyclerView = startActivity()
        MatcherAssert.assertThat(recyclerView.adapter?.itemCount, CoreMatchers.`is`(1))
        MatcherAssert.assertThat(recyclerView.adapter?.getItemViewType(0),
            CoreMatchers.`is`(R.layout.network_state_item))

        fakeApi.failureMsg = null
        addProductsToApi()

        onView(withId(R.id.list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ProductViewHolder>
                (0, MyViewAction.clickChildViewWithId(R.id.retry_button))
        )

        waitForAdapterChange(recyclerView)
        MatcherAssert.assertThat(recyclerView.adapter?.itemCount, CoreMatchers.`is`(1))


        waitForAdapterChange(recyclerView)
        MatcherAssert.assertThat(recyclerView.adapter?.itemCount, CoreMatchers.`is`(3))
    }

    private fun addProductsToApi() {
        fakeApi.addProduct(productFactory.createProduct())
        fakeApi.addProduct(productFactory.createProduct())
        fakeApi.addProduct(productFactory.createProduct())
    }

    private fun waitForAdapterChange(recyclerView: RecyclerView) {
        val latch = CountDownLatch(1)
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            recyclerView.adapter?.registerAdapterDataObserver(
                object : RecyclerView.AdapterDataObserver() {
                    override fun onChanged() {
                        latch.countDown()
                    }

                    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                        latch.countDown()
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
        val intent = Intent(ApplicationProvider.getApplicationContext(),
            MainActivity::class.java)

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity = InstrumentationRegistry.getInstrumentation().startActivitySync(intent)
        val recyclerView = activity.findViewById<RecyclerView>(R.id.list)
        MatcherAssert.assertThat(recyclerView.adapter, CoreMatchers.notNullValue())
        waitForAdapterChange(recyclerView)
        return recyclerView
    }


    private fun injectViewModel() {
        val app = ApplicationProvider.getApplicationContext<Application>()

        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {

            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                val db = Room.inMemoryDatabaseBuilder(app, ProductsDb::class.java)
                    .allowMainThreadQueries()
                    .build()

                val repository = ProductsRepositoryImpl(db.productsDao(),
                    fakeApi, Executors.newFixedThreadPool(5))
                val model = ProductListingViewModel(repository)
                (activity as MainActivity).viewModelFactory = ViewModelUtil.createFor(model)
            }

            override fun onActivityPaused(activity: Activity?) {
            }

            override fun onActivityResumed(activity: Activity?) {
            }

            override fun onActivityStarted(activity: Activity?) {
            }

            override fun onActivityDestroyed(activity: Activity?) {
            }

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
            }

            override fun onActivityStopped(activity: Activity?) {
            }

        })
    }
}