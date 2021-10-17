package com.makeevrserg.tradingviewchart.network.response.watchlists

data class SingleWatchList(
    val id: Int,
    val name: String,
    val symbols: List<String>
)