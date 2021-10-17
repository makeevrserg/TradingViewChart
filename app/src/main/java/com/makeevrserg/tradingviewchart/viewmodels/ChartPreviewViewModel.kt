package com.makeevrserg.tradingview.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.makeevrserg.tradingview.adapters.WatchListItem

class ChartPreviewViewModel(application: Application) : AndroidViewModel(application) {

    private val _watchListItem = MutableLiveData<WatchListItem>()
    public val watchListItem: LiveData<WatchListItem>
        get() = _watchListItem
}