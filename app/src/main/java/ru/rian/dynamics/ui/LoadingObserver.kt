package ru.rian.dynamics.ui

import kotlin.properties.Delegates

object LoadingObserver {

    private var loadingStateObservers: MutableList<((Boolean) -> Unit)?> = ArrayList<((Boolean) -> Unit)?>()

    var loading: Boolean by Delegates.observable(false) { _, oldValue, newValue ->
        for (observer in loadingStateObservers) {
            observer?.invoke(newValue)
        }
    }

    fun removeLoadingObserver(observer: ((Boolean) -> Unit)?) {
        loadingStateObservers.remove(observer)
    }

    fun addLoadingObserver(observer: ((Boolean) -> Unit)?) {
        loadingStateObservers.add(observer)
    }
}