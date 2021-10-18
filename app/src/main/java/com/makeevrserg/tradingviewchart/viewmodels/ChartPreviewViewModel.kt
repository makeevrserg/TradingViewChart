package com.makeevrserg.tradingview.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.makeevrserg.tradingview.adapters.WatchListItem
import com.makeevrserg.tradingviewchart.network.RestClient
import com.makeevrserg.tradingviewchart.network.response.stocks.Data
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.await

class ChartPreviewViewModel(application: Application) : AndroidViewModel(application) {

    private val _watchListItem = MutableLiveData<WatchListItem>()
    public val watchListItem: LiveData<WatchListItem>
        get() = _watchListItem

    private val _watchListData = MutableLiveData<List<Data>>()
    val watchListData: LiveData<List<Data>>
        get() = _watchListData
    private val _toolbarTitle = MutableLiveData<String>("")
    val toolbarTitle:LiveData<String>
        get()=_toolbarTitle
    val TAG = "ChartPreviewViewModel"

    fun loadChart(symbolName: String) {
        Log.d(TAG, "loadChart: ${symbolName}")
        viewModelScope.launch(Dispatchers.IO) {
            val stonks = RestClient.retrofitService.fetchIntraDayStock(symbolName).await()
            _watchListData.postValue(stonks.data)
            _toolbarTitle.postValue(symbolName)
        }

    }

}