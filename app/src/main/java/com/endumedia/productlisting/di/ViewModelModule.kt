package com.endumedia.productlisting.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.endumedia.core.ui.ProductListingViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


/**
 * Created by Nino on 19.08.19
 */
@Suppress("unused")
@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(ProductListingViewModel::class)
    abstract fun bindFeedViewModel(feedViewModel: ProductListingViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ProductsViewModelFactory): ViewModelProvider.Factory
}
