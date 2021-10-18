package com.makeevrserg.tradingviewchart.network.response.stocks

data class Stock(
    val `data`: List<Data>,
    val page: Page
)