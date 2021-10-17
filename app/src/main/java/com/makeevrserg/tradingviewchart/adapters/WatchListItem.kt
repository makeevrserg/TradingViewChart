package com.makeevrserg.tradingview.adapters

data class WatchListItem(
    val imgUrl: String,
    val title: String,
    val desc: String,
    val value: Float,
    val change: Float,
    val percentChange: Float
) {
    fun isPositive() = change >= 0
    fun changeToString(): String {
        return if (!isPositive()) change.toString()
        else "+$change"
    }
    fun percentChangeToString(): String {
        return if (!isPositive()) "$percentChange%"
        else "+$percentChange%"
    }
}
