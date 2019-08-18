package com.endumedia.benchmark

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.endumedia.core.GlideApp
import com.endumedia.core.repository.NetworkState
import com.endumedia.core.ui.ProductsAdapter
import com.endumedia.core.vo.Product
import kotlinx.android.synthetic.main.activity_benchmark.*

/**
 * Created by Nino on 18.08.19
 */
class BenchmarkActivity : AppCompatActivity() {
    val testExecutor = TestExecutor()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_benchmark)

        initList()
        val glide = GlideApp.with(this)
        val adapter = ProductsAdapter(glide) {}
        list.adapter = adapter

        val config = PagedList.Config.Builder()
            .setInitialLoadSizeHint(5)
            .setPageSize(5)
            .build()

        val pagedStrings: PagedList<Product> = PagedList.Builder<Int, Product>(MockDataSource(), config)
            .setInitialKey(0)
            .setFetchExecutor(testExecutor)
            .setNotifyExecutor(testExecutor)
            .build()

        adapter.submitList(pagedStrings)
        adapter.setNetworkState(NetworkState.LOADED)

    }

    private fun initList() {
        val layoutManager = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                when (list.adapter?.getItemViewType(position)) {
                    R.layout.network_state_item -> return 2
                    R.layout.cms_product_carousel_row_row -> return 1
                    else -> return 1
                }
            }
        }
        list.layoutManager = layoutManager
    }
}

class MockDataSource : PageKeyedDataSource<Int, Product>() {
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Product>) {
        callback.onResult(List(200) { generateProduct() }.toList(), -1, 1)
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Product>) {
        callback.onResult(List(200) { generateProduct() }.toList(), params.key + 1)
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Product>) {
        callback.onResult(List(200) { generateProduct() }.toList(), params.key - 1)
    }

    private fun generateProduct(): Product {
        val name = List(10) { (0..100).random() }.joinToString("")
        val price = List(10) { (10..200).random() }.joinToString("$")
        return Product(2L, 2L, name, price, "")
    }
}