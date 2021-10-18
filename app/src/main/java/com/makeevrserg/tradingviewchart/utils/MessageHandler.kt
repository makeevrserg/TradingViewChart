package com.makeevrserg.tradingview.utils


/**
 * После сворачивания/разворачивания приложений observer моежет заново обсервнуть LiveData.
 * Чтобы этого не происходило - для одноразовых сообщений используется этот класс.
 */
class MessageHandler(private val content: String?) {

    /**
     * При создании класса эвент считаетсся включенным
     */
    private var isEnabled = true

    /**
     * Получаем контент во фрагменте, созданный в ViewModel
     * После вызова функии
     */
    fun getContent(): String? {
        if (!isEnabled)
            return null
        isEnabled = false
        return content
    }

}