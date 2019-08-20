package com.endumedia.productlisting

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner


/**
 * Created by Nino on 20.08.19
 */
class CustomTestRunner : AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(cl, Application::class.java.name, context)
    }
}