package com.makeevrserg.tradingviewchart

import com.makeevrserg.tradingviewchart.network.RestClient
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*
import retrofit2.await

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
        RetrofitTest()


    }
}

fun Log(TAG: String, message: String) =
    println("$TAG: $message")

/**
 * Не особо похоже на номральный тестинг, но тут можно убедиться, что ретрофит правильно отправляет/полуает запросы
 */
class RetrofitTest() {
    val TAG = "RetrofitTest"
    init {
        runBlocking {
            val service = RestClient.retrofitService
            val watchLists = service.fetchWatchListsList().await()
            Log(TAG, "fetchWatchListsList $watchLists")
            val id = watchLists.first().id
            val fetchWatchList = service.fetchWatchList(id).await()
            Log(TAG, "fetchWatchList ${fetchWatchList}")
            val symbolName = fetchWatchList.symbols.first()
            val fetchIntraDayStock = service.fetchIntraDayStock(symbolName).await()
            Log(TAG, "fetchIntraDayStock ${fetchIntraDayStock}")
        }
    }
}