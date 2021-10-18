package com.makeevrserg.tradingview.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.makeevrserg.tradingview.adapters.WatchListItem
import com.makeevrserg.tradingview.utils.MessageHandler
import com.makeevrserg.tradingviewchart.network.RestClient
import com.makeevrserg.tradingviewchart.network.response.watchlists.Watchlists
import com.makeevrserg.tradingviewchart.network.response.watchlists.WatchlistsItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.await
import java.lang.Exception
import kotlin.random.Random

class WatchListViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * Индикатор загрузки
     */
    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    /**
     * Список котировок из текущего выбранного вотчлиста
     */
    private val _watchListItems = MutableLiveData<MutableList<WatchListItem>>()
    public val watchListItem: LiveData<MutableList<WatchListItem>>
        get() = _watchListItems

    /**
     * Список всех вотчлистов
     */
    private var watchLists: Watchlists? = null

    /**
     * Текущий выбранный вотчлист
     */
    private var currentWatchList: WatchlistsItem? = null

    /**
     * После изменение пользователь единожды получит уведомление
     */
    private val _notifyMessage = MutableLiveData(MessageHandler(null))
    val notifyMessage: LiveData<MessageHandler>
        get() = _notifyMessage

    /**
     * Тайтл тулбара
     */
    private val _toolbarTitle = MutableLiveData<String>()
    val toolbarTitle: LiveData<String>
        get() = _toolbarTitle

    private val TAG = "WatchListViewModel"

    /**
     * Только для тестинга
     */
    fun Random.nextFloat(from: Float, until: Float) =
        Random.nextDouble(from.toDouble(), until.toDouble()).toFloat().round(2)

    /**
     * Только для тестинга
     */
    fun Float.round(decimals: Int): Float {
        var multiplier = 1f
        repeat(decimals) { multiplier *= 10 }
        return kotlin.math.round(this * multiplier) / multiplier
    }

    /**
     * Получаем список тайтлов загруженных вотчлистов чтобы пользователь мог выбрать конкретный
     */
    fun getWatchListTitles(): List<String>? {
        if (watchLists == null || watchLists!!.isEmpty()) {
            _notifyMessage.value = MessageHandler("Нет доступных списков")
            return null
        }
        val list = watchLists?.map { it.name }
        return list
    }

    /**
     * Загружаем доступные вотчлисты
     *
     * Чтобы предотвратить ошибки связанные с подключением к серверу - просто добавляем общий Exception
     */
    fun loadWatchLists() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                watchLists = RestClient.retrofitService.fetchWatchListsList().await()
                onWatchListSelected(0)
                _isLoading.postValue(false)
            } catch (e: Exception) {
                println(e.stackTraceToString())
                _notifyMessage.postValue(MessageHandler("Возникла непредвиденная ошибка"))
            }
        }
    }

    /**
     * Наш View сообщает нам о том, что пользователь выбрал конкретную позицию в списке вотчлистов
     */
    fun onWatchListSelected(position: Int) {
        currentWatchList = watchLists?.getOrNull(position)
        currentWatchList?.let {
            _toolbarTitle.postValue(it.name)
            val list = mutableListOf<WatchListItem>()
            it.symbols.forEach {
                val item = WatchListItem(
                    "",
                    it,
                    "Desc ${Random.nextFloat(100f, 200f)}",
                    Random.nextFloat(100f, 200f),
                    Random.nextFloat(-1f, -0.11f),
                    Random.nextFloat(-10f, -3f),
                )
                list.add(item)
            }
            _watchListItems.postValue(list)
        }
    }


    init {
        loadWatchLists()
    }
}