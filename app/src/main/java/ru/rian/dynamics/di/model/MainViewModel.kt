package ru.rian.dynamics.di.model

import android.text.TextUtils
import io.reactivex.Observable
import ru.rian.dynamics.HttpReqManager
import ru.rian.dynamics.SchedulerProvider
import ru.rian.dynamics.retrofit.model.ArticleResponse
import ru.rian.dynamics.retrofit.model.FeedResponse
import ru.rian.dynamics.retrofit.model.HSResult
import ru.rian.dynamics.retrofit.model.Source
import ru.rian.dynamics.ui.helpers.LoadingObserver.loading
import ru.rian.dynamics.utils.ARTICLE_LIST_LIMIT
import ru.rian.dynamics.utils.HS_PATH
import ru.rian.dynamics.utils.PreferenceHelper.get
import ru.rian.dynamics.utils.PreferenceHelper.prefs
import ru.rian.dynamics.utils.TOKEN_STRING_KEY
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private var httpReqManager: HttpReqManager,
    private var schedulerProvider: SchedulerProvider
) {


    private val prefs = prefs()

    fun isTokenPresented(): Boolean {
        val isToken: String? = prefs[TOKEN_STRING_KEY]
        return !TextUtils.isEmpty(isToken)
    }

    fun provideHS(): Observable<HSResult?>? {
        loading = true
        return httpReqManager.requestGet<HSResult?>(HS_PATH)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .doFinally { loading = false }
    }

    fun provideFeeds(source: Source): Observable<FeedResponse?> {
        loading = true
        return source.url.let {
            httpReqManager.requestGet<FeedResponse?>(it!!)
                .subscribeOn(schedulerProvider.io())
                .doFinally { loading = false }
                .observeOn(schedulerProvider.ui())
        }
    }

    fun provideArticles(
        source: Source,
        feed: String,
        offset: Int = 0,
        query: String? = null,
        showProgress: Boolean = true
    ): Observable<ArticleResponse?>? {
        if (showProgress) {
            loading = true
        }
        return source.url.let {
            httpReqManager.requestGet<ArticleResponse?>(
                it!!,
                feed,
                ARTICLE_LIST_LIMIT.toString(),
                offset.toString(),
                query
            )
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .doFinally { loading = false }
        }
    }
}