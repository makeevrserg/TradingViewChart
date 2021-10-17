package com.makeevrserg.tradingviewchart.network.response.stocks

data class Data(
    val close: Double,
    val date: String,
    val high: Double,
    val last: Double,
    val low: Double,
    val `open`: Double,
    val symbol: String,
    val volume: Int
)