package com.makeevrserg.tradingview.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.makeevrserg.tradingview.adapters.WatchListItem
import kotlin.random.Random

class WatchListViewModel(application: Application) : AndroidViewModel(application) {

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading
    private val _watchListItems = MutableLiveData<List<WatchListItem>>()
    public val watchListItem: LiveData<List<WatchListItem>>
        get() = _watchListItems

    fun Random.nextFloat(from: Float, until: Float) =
        Random.nextDouble(from.toDouble(), until.toDouble()).toFloat().round(2)
    fun Float.round(decimals: Int): Float {
        var multiplier = 1f
        repeat(decimals) { multiplier *= 10 }
        return kotlin.math.round(this * multiplier) / multiplier
    }

    init {
        val item1 = WatchListItem(
            "",
            "GOOG",
            "Non-Alphabet Inc (Non-Google) Class D",
            Random.nextFloat(100f,200f),
            Random.nextFloat(-1f,-0.11f),
            Random.nextFloat(-10f,-3f),
        )
        val item2 = WatchListItem(
            "",
            "GOOG",
            "Non-Alphabet Inc (Non-Google) Class D",
            Random.nextFloat(100f,200f),
            Random.nextFloat(-1f,-0.11f),
            Random.nextFloat(-10f,-3f),
        )
        val item3 = WatchListItem(
            "",
            "GOOG",
            "Non-Alphabet Inc (Non-Google) Class D",
            Random.nextFloat(100f,500f),
            Random.nextFloat(2f,3f),
            Random.nextFloat(5f,12f),
        )
        val item4 = WatchListItem(
            "",
            "GOOG",
            "Non-Alphabet Inc (Non-Google) Class D",
            Random.nextFloat(100f,500f),
            Random.nextFloat(2f,3f),
            Random.nextFloat(5f,12f),
        )
        _watchListItems.value = listOf(item1, item2, item3, item4)
    }
}