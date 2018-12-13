package ru.rian.dynamics

import io.reactivex.Observable
import ru.rian.dynamics.retrofit.ApiInterface
import ru.rian.dynamics.retrofit.model.FeedResponse
import ru.rian.dynamics.retrofit.model.HSResult
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DataManager @Inject constructor(private var apiInterface: ApiInterface) {

    fun requestHSQuery(appId: String, deviceId: String, lang: String?): Observable<HSResult> {
        return apiInterface.requestHS(appId, deviceId, lang)
    }

    fun requestFeeds(appId: String, deviceId: String, feedPath: String?): Observable<FeedResponse> {
        return apiInterface.requestFeeds(appId, deviceId, feedPath)
    }

}