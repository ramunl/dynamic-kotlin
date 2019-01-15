package ru.rian.dynamics

import android.net.Uri
import common.FlavorConstants
import io.reactivex.Observable
import okhttp3.MultipartBody
import ru.rian.dynamics.retrofit.ApiInterface
import ru.rian.dynamics.retrofit.model.ArticleResponse
import ru.rian.dynamics.retrofit.model.FeedResponse
import ru.rian.dynamics.retrofit.model.HSResult
import ru.rian.dynamics.utils.BASE_URL
import ru.rian.dynamics.utils.LocaleHelper
import ru.rian.dynamics.utils.PLAYER_ID
import ru.rian.dynamics.utils.PreferenceHelper.get
import ru.rian.dynamics.utils.PreferenceHelper.prefs
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class HttpReqManager @Inject constructor(var apiInterface: ApiInterface) {
    inline fun <reified T> requestGet(
        path: String,
        feed: String? = null,
        limit: String? = null,
        offset: String? = null,
        query: String? = null
    ): Observable<T> {
        val pathRes = if (path.contains(BASE_URL)) path.substring(BASE_URL.length) else path
        return when (T::class) {
            ArticleResponse::class ->
                apiInterface.requestArticles(
                    Uri.encode(pathRes),
                    FlavorConstants.QUERY_HS_APP_ID_DYNAMICS,
                    prefs()[PLAYER_ID]!!,
                    feed,
                    limit,
                    offset,
                    query
                ) as Observable<T>
            HSResult::class ->
                apiInterface.requestHS(
                    Uri.encode(pathRes),
                    FlavorConstants.QUERY_HS_APP_ID_DYNAMICS,
                    prefs()[PLAYER_ID]!!
                ) as Observable<T>
            FeedResponse::class ->
                apiInterface.requestFeed(
                    pathRes,
                    FlavorConstants.QUERY_HS_APP_ID_DYNAMICS,
                    prefs()[PLAYER_ID]!!,
                    LocaleHelper.getLanguage()
                ) as Observable<T>
            else -> {
                throw UnsupportedOperationException("Not yet implemented")
            }
        }
    }

    inline fun <reified T> requestLogin(path: String, username: String, password: String): Observable<T> {
        val bld = MultipartBody.Builder()
        bld.setType(MultipartBody.FORM)
        bld.addFormDataPart("username", username)
        bld.addFormDataPart("password", password)
        bld.addFormDataPart("deviceId", prefs()[PLAYER_ID]!!)
        val requestBody = bld.build()
        return apiInterface.terminalLogin(
            Uri.encode(path),
            requestBody
        ) as Observable<T>
    }


}