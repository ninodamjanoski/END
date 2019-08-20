package com.endumedia.productlisting.di

import com.endumedia.core.repository.ProductsRepository
import com.endumedia.productlisting.repository.ProductsRepositoryImpl
import com.endumedia.productlisting.ui.MainActivity
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector


/**
 * Created by Nino on 19.08.19
 */

@Suppress("unused")
@Module
abstract class MainActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity

    @Binds
    abstract fun boardItemsRepository(boardItemsRepository: ProductsRepositoryImpl): ProductsRepository

}