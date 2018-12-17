package ru.rian.dynamics

import io.reactivex.Observable
import ru.rian.dynamics.retrofit.ApiInterface
import ru.rian.dynamics.retrofit.model.FeedResponse
import ru.rian.dynamics.retrofit.model.HSResult
import ru.rian.dynamics.utils.LocaleHelper
import ru.rian.dynamics.utils.PLAYER_ID
import ru.rian.dynamics.utils.PreferenceHelper.get
import ru.rian.dynamics.utils.PreferenceHelper.prefs
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DataManager @Inject constructor(private var apiInterface: ApiInterface) {

    fun requestHSQuery(): Observable<HSResult> {
        return apiInterface.requestHS(FlavorConstants.QUERY_HS_APP_ID_DYNAMICS,
            prefs()[PLAYER_ID]!!, LocaleHelper.getLanguage())
    }

    fun requestFeeds(): Observable<FeedResponse> {
        return apiInterface.requestFeeds(FlavorConstants.QUERY_HS_APP_ID_DYNAMICS,
            prefs()[PLAYER_ID]!!,
            prefs()["getFeeds"])
    }

}