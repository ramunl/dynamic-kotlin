package ru.rian.dynamics.di.model

import android.text.TextUtils
import io.reactivex.Observable
import ru.rian.dynamics.DataManager
import ru.rian.dynamics.SchedulerProvider
import ru.rian.dynamics.retrofit.model.ArticleResponse
import ru.rian.dynamics.retrofit.model.FeedResponse
import ru.rian.dynamics.retrofit.model.HSResult
import ru.rian.dynamics.retrofit.model.Source
import ru.rian.dynamics.utils.*
import ru.rian.dynamics.utils.PreferenceHelper.get
import ru.rian.dynamics.utils.PreferenceHelper.prefs
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private var dataManager: DataManager,
    private var schedulerProvider: SchedulerProvider
) {

    var loading: Boolean? = null
    private val prefs = prefs()


    fun isTokenPresented(): Boolean {
        val isToken: String? = prefs[TOKEN_STRING_KEY]
        return !TextUtils.isEmpty(isToken)
    }

    fun provideHS(): Observable<HSResult?>? {
        return dataManager.requestGet<HSResult?>(HS_PATH)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .map { result -> result }
    }

    fun provideFeeds(): Observable<FeedResponse?>? {
        val source: Source? = prefs()[getFeeds]!!
        return dataManager.requestGet<FeedResponse?>(source!!.url!!)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.io())
            .map { result -> result }
    }

    fun provideArticles(feed: String, offset: Int = 0): Observable<ArticleResponse?>? {
        val source: Source? = prefs()[getArticles]!!
        return dataManager.requestGet<ArticleResponse?>(source!!.url!!, feed, offset.toString(), ARTICLE_LIST_LIMIT.toString())
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .map { result -> result }
    }
}