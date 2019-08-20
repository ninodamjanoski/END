package com.endumedia.productlisting.di

import android.app.Application
import androidx.room.Room
import com.endumedia.productlisting.api.ProductsApi
import com.endumedia.productlisting.db.ProductsDao
import com.endumedia.productlisting.db.ProductsDb
import dagger.Module
import dagger.Provides
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Singleton


/**
 * Created by Nino on 19.08.19
 */
@Module(includes = [ViewModelModule::class])
class AppModule {


    @Singleton
    @Provides
    fun provideProductsApi(): ProductsApi {
        return ProductsApi.create()
    }

    @Singleton
    @Provides
    fun provideDb(app: Application): ProductsDb {
        return Room
            .databaseBuilder(app, ProductsDb::class.java, "productsDao.db")
            .fallbackToDestructiveMigration()
            .build()
    }


    @Singleton
    @Provides
    fun provideAccountsDao(db: ProductsDb): ProductsDao {
        return db.productsDao()
    }


    @Singleton
    @Provides
    fun provideIOAppExecutor(): Executor {
        return Executors.newFixedThreadPool(5)
    }
}