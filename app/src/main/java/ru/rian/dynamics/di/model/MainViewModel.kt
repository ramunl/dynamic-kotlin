package ru.rian.dynamics.di.model

import android.text.TextUtils
import io.reactivex.Observable
import ru.rian.dynamics.DataManager
import ru.rian.dynamics.SchedulerProvider
import ru.rian.dynamics.retrofit.model.FeedResponse
import ru.rian.dynamics.retrofit.model.HSResult
import ru.rian.dynamics.utils.PreferenceHelper.get
import ru.rian.dynamics.utils.PreferenceHelper.prefs
import ru.rian.dynamics.utils.TOKEN_STRING_KEY
import javax.inject.Inject

class MainViewModel @Inject constructor( private var dataManager: DataManager, private var schedulerProvider: SchedulerProvider) {

    var loading: Boolean? = null
    private val prefs = prefs()


    fun isTokenPresented(): Boolean {
        val isToken: String? = prefs[TOKEN_STRING_KEY]
        return !TextUtils.isEmpty(isToken)
    }

    fun provideHS(): Observable<HSResult?>? {
        return reqHs()
    }

    fun provideFeeds(): Observable<FeedResponse?>? {
        return reqFeeds()
    }

    private fun reqHs(): Observable<HSResult?>? {
        return dataManager.requestHSQuery()
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .map { result -> result }
    }

    private fun reqFeeds(): Observable<FeedResponse?>? {
        return dataManager.requestFeeds()
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .map { result -> result }
    }
}