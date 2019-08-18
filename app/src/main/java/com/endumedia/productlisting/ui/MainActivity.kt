package com.endumedia.productlisting.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.endumedia.core.GlideApp
import com.endumedia.core.repository.NetworkState
import com.endumedia.core.ui.ProductListingViewModel
import com.endumedia.core.ui.ProductsAdapter
import com.endumedia.core.vo.Product
import com.endumedia.productlisting.R
import com.endumedia.productlisting.ServiceLocator
import kotlinx.android.synthetic.main.activity_main.*



class MainActivity : AppCompatActivity() {

    private val list by lazy { findViewById<RecyclerView>(R.id.list) }


    private val model: ProductListingViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ProductListingViewModel(ServiceLocator.instance(baseContext).getRepository()) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initList()
        initAdapter()
        initSwipeToRefresh()
        model.showCatalog()
    }

    private fun initList() {
        val layoutManager = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                when (list.adapter?.getItemViewType(position)) {
                    R.layout.network_state_item -> return 2
                    R.layout.cms_product_carousel_row_row -> return 1
                    else -> return -1
                }
            }
        }
        list.layoutManager = layoutManager
        val itemDecoration =
            CardPaddingDecorator(0).top(0).bottom(0)
                .left(resources.getDimensionPixelSize(R.dimen.screen_padding))
                .right(resources.getDimensionPixelSize(R.dimen.screen_padding))
                .betweenHorizontal(resources.getDimensionPixelSize(R.dimen.screen_padding))
        list.addItemDecoration(itemDecoration)
    }

    private fun initAdapter() {
        val glide = GlideApp.with(this)
        val adapter = ProductsAdapter(glide) {
            model.retry()
        }
        list.adapter = adapter

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
}
