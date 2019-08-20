package com.endumedia.productlisting.di

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.endumedia.core.di.Injectable
import com.endumedia.productlisting.ProductsApp
import dagger.android.AndroidInjection


/**
 * Created by Nino on 19.08.19
 */
object AppInjector {
    fun init(productsApp: ProductsApp) {
        DaggerAppComponent.builder().application(productsApp)
            .build().inject(productsApp)

        productsApp
            .registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
                override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                    if (activity is Injectable) {
                        AndroidInjection.inject(activity)
                    }
                }

                override fun onActivityStarted(activity: Activity) {

                }

                override fun onActivityResumed(activity: Activity) {

                }

                override fun onActivityPaused(activity: Activity) {

                }

                override fun onActivityStopped(activity: Activity) {

                }

                override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {

                }

                override fun onActivityDestroyed(activity: Activity) {

                }
            })
    }
}