package com.endumedia.core.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.endumedia.core.repository.ProductsRepository


/**
 * Created by Nino on 18.08.19
 */
class ProductListingViewModel(repository: ProductsRepository) : ViewModel() {

    private val pageSize = MutableLiveData<Int>()
    val catalogResult = Transformations.map(pageSize) {
        repository.lisProducts(it)
    }

    val products = Transformations.switchMap(catalogResult) { it.pagedList }!!
    val networkState = Transformations.switchMap(catalogResult) { it.networkState }!!
    val refreshState = Transformations.switchMap(catalogResult) { it.refreshState }!!


    fun refresh() {
        catalogResult.value?.refresh?.invoke()
    }

    fun showCatalog(size: Int = 10) {
        pageSize.value = size
    }

    fun retry() {
        val listing = catalogResult?.value
        listing?.retry?.invoke()
    }
}