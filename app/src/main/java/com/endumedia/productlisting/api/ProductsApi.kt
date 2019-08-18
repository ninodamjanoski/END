package com.endumedia.productlisting.api

import android.util.Log
import com.endumedia.core.vo.Catalog
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


/**
 * Created by Nino on 18.08.19
 */
interface ProductsApi {

    @GET("media/catalog/example.json")
    fun getProducts(): Call<Catalog>


    companion object {
        private const val BASE_URL = "https://www.endclothing.com/"
        fun create(): ProductsApi = create(HttpUrl.parse(BASE_URL)!!)
        fun create(httpUrl: HttpUrl): ProductsApi {
            val logger = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
                Log.d("API", it)
            })
            logger.level = HttpLoggingInterceptor.Level.BASIC

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()
            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ProductsApi::class.java)
        }
    }

}