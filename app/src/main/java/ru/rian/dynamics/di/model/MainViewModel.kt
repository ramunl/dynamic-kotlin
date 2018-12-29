package ru.rian.dynamics.di.model

import android.text.TextUtils
import io.reactivex.Observable
import ru.rian.dynamics.DataManager
import ru.rian.dynamics.SchedulerProvider
import ru.rian.dynamics.di.model.MainViewModel.LoadingObserver.loading
import ru.rian.dynamics.retrofit.model.ArticleResponse
import ru.rian.dynamics.retrofit.model.FeedResponse
import ru.rian.dynamics.retrofit.model.HSResult
import ru.rian.dynamics.retrofit.model.Source
import ru.rian.dynamics.utils.*
import ru.rian.dynamics.utils.PreferenceHelper.get
import ru.rian.dynamics.utils.PreferenceHelper.prefs
import javax.inject.Inject
import kotlin.properties.Delegates

class MainViewModel @Inject constructor(
    private var dataManager: DataManager,
    private var schedulerProvider: SchedulerProvider
) {
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

    private val prefs = prefs()


    fun isTokenPresented(): Boolean {
        val isToken: String? = prefs[TOKEN_STRING_KEY]
        return !TextUtils.isEmpty(isToken)
    }

    fun provideHS(): Observable<HSResult?>? {
        loading = true
        return dataManager.requestGet<HSResult?>(HS_PATH)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .doFinally { loading = false }
            .map { result -> result }
    }

    fun provideFeeds(): Observable<FeedResponse?>? {
        loading = true
        val source: Source? = prefs()[getFeeds]
        return source?.url?.let {
            dataManager.requestGet<FeedResponse?>(it)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .doFinally { loading = false }
                .map { result -> result }
        }
    }

    fun provideArticles(
        feed: String,
        offset: Int = 0,
        query: String? = null,
        showProgress: Boolean = true

    ): Observable<ArticleResponse?>? {
        if (showProgress) {
            loading = true
        }
        val source: Source? = prefs()[getArticles]
        return source?.url?.let {
            dataManager.requestGet<ArticleResponse?>(
                it,
                feed,
                ARTICLE_LIST_LIMIT.toString(),
                offset.toString(),
                query
            )
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .doFinally { loading = false }
                .map { result -> result }
        }
    }
}