package com.makeevrserg.tradingviewchart.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RestClient {



    private fun requestInterceptor() = Interceptor { chain ->
        val url = chain.request()
            .url()
            .newBuilder()
            .build()
        val request = chain.request()
            .newBuilder()
            .url(url)
            .build()

//        val originalRequest = chain.request()
//        val request = originalRequest.newBuilder().url(originalRequest.url()).build()
        return@Interceptor chain.proceed(request)
    }


    private fun okHttpClient() = OkHttpClient.Builder()
        .addInterceptor(requestInterceptor())
        .build()


    private fun retrofit() = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
//        .addConverterFactory(ScalarsConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .client(okHttpClient())
        .baseUrl("https://alex9.fvds.ru/")
        .build()

    val retrofitService: RestService by lazy { retrofit().create(RestService::class.java) }
}