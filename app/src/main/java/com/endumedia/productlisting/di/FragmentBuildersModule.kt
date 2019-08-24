package com.endumedia.productlisting.di

import com.endumedia.productlisting.ui.MainFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector


/**
 * Created by Nino on 21.07.19
 */
@Suppress("unused")
@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeMainFragment(): MainFragment
}