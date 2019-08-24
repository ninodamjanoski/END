package com.endumedia.productlisting.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.endumedia.core.GlideApp
import com.endumedia.core.di.Injectable
import com.endumedia.core.repository.NetworkState
import com.endumedia.core.ui.ProductListingViewModel
import com.endumedia.core.ui.ProductsAdapter
import com.endumedia.core.ui.widgets.HintAdapter
import com.endumedia.core.ui.widgets.HintSpinner
import com.endumedia.core.ui.widgets.ViewMode
import com.endumedia.core.vo.Product
import com.endumedia.productlisting.BuildConfig
import com.endumedia.productlisting.R
import kotlinx.android.synthetic.main.activity_main_old.*
import javax.inject.Inject
import kotlin.jvm.internal.Intrinsics


class MainFragment : Fragment(), Injectable {

    private val list by lazy { view?.findViewById<RecyclerView>(R.id.list) }
    private val spSort by lazy { view?.findViewById<Spinner>(R.id.spinner_sort) }
    private val spView by lazy { view?.findViewById<Spinner>(R.id.spinner_view) }
    private val llFilters by lazy { view?.findViewById<View>(R.id.filter) }
    private val dlFilters by lazy { view?.findViewById<DrawerLayout>(R.id.drawer_layout) }
    private lateinit var adapter: ProductsAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val model: ProductListingViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory)
            .get(ProductListingViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initAdapter()
        initSpinners()
        initSwipeToRefresh()
        model.showCatalog()
    }

    private fun initList() {
        initGridLayout()
        val itemDecoration =
            CardPaddingDecorator(0).top(0).bottom(0)
                .left(resources.getDimensionPixelSize(R.dimen.screen_padding))
                .right(resources.getDimensionPixelSize(R.dimen.screen_padding))
                .betweenHorizontal(resources.getDimensionPixelSize(R.dimen.screen_padding))
        list?.addItemDecoration(itemDecoration)
    }

    private fun initGridLayout() {
        val layoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                when (list?.adapter?.getItemViewType(position)) {
                    R.layout.network_state_item -> return 2
                    R.layout.product_list_row -> return 1
                    else -> return -1
                }
            }
        }
        list?.layoutManager = layoutManager
    }

    private fun initLinearLayout() {
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        list?.layoutManager = layoutManager
    }

    private fun initAdapter() {
        val glide = GlideApp.with(this)
        adapter = ProductsAdapter(glide) {
            model.retry()
        }
        list?.adapter = adapter

        model.products.observe(this, Observer<PagedList<Product>> {
            adapter.submitList(it)
        })

        model.networkState.observe(this, Observer {
            adapter.setNetworkState(it)
        })
    }

    private fun initSwipeToRefresh() {
        model.refreshState.observe(this, Observer {
            swipe_refresh.isRefreshing = it == NetworkState.LOADING
        })
        swipe_refresh.setOnRefreshListener {
            model.refresh()
        }
    }

    private fun initSpinners() {
        spSort?.run { initSortSpinner(this, R.string.product_listing_sort, R.array.sort_spinner_values) }
        spView?.run { initViewSpinner(this, R.string.product_listing_view, R.array.view_spinner_values) }
        llFilters?.setOnClickListener {
            toggleNavDrawer()
        }
    }


    private fun toggleNavDrawer() {
        try {
            if (dlFilters?.isDrawerVisible(GravityCompat.END) == true) {
                dlFilters?.closeDrawer(GravityCompat.END)
            } else {
                dlFilters?.openDrawer(GravityCompat.END)
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) e.printStackTrace()
        }

    }
    private fun initSortSpinner(spinner: Spinner, i: Int, i2: Int) {
        val stringArray = resources.getStringArray(i2)
        val hintAdapter = HintAdapter(context, R.layout.product_list_view_row, i, stringArray.asList(), false)
        HintSpinner(spinner, hintAdapter, object : HintSpinner.Callback<String> {
            override fun onItemSelected(i: Int, t: String?) {
                hintAdapter.setSelectedTopPosition(i)
            }
        }).init()
    }

    private fun initViewSpinner(spinner: Spinner, i: Int, i2: Int) {
        val stringArray = resources.getStringArray(i2)
        Intrinsics.checkExpressionValueIsNotNull(stringArray, "array")
        val hintAdapter = HintAdapter(context, R.layout.product_list_view_row, i, stringArray.asList(), true)
        HintSpinner(spinner, hintAdapter, object : HintSpinner.Callback<String> {
            override fun onItemSelected(i: Int, t: String?) {
                when (ViewMode.values()[i]) {
                    ViewMode.VIEW_MODE_PRODUCT, ViewMode.VIEW_MODE_OUTFIT -> hintAdapter.setSelectedTopPosition(i)
                    ViewMode.VIEW_MODE_ONE_PRODUCT_PER_ROW -> {
                        hintAdapter.setSelectedBottomPosition(i)
                        initLinearLayout()
                        adapter.notifyDataSetChanged()
                    }
                    ViewMode.VIEW_MODE_TWO_PRODUCTS_PER_ROW -> {
                        hintAdapter.setSelectedBottomPosition(i)
                        initGridLayout()
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }).init()
    }
}
