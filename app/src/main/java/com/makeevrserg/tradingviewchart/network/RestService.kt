package com.makeevrserg.tradingviewchart.network

import com.makeevrserg.tradingviewchart.network.response.stocks.Stock
import com.makeevrserg.tradingviewchart.network.response.watchlists.Watchlists
import com.makeevrserg.tradingviewchart.network.response.watchlists.WatchlistsItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface RestService {

    @GET("watchlists")
    fun fetchWatchListsList(): Call<Watchlists>

    @GET("watchlists/{id}")
    fun fetchWatchList(
        @Path("id") id:Int
    ): Call<WatchlistsItem>

    @GET("/stocks/intraday/{symbolName}")
    fun fetchIntraDayStock(

        @Path("symbolName") symbolName: String,
        @Query("offset") offset: Int = 1
    ):Call<Stock>

}

