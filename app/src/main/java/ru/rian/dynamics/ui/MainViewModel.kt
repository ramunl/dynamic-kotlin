package ru.rian.dynamics.ui

import android.text.TextUtils
import com.onesignal.OneSignal
import io.reactivex.Observable
import ru.rian.dynamics.DataManager
import ru.rian.dynamics.FlavorConstants
import ru.rian.dynamics.SchedulerProvider
import ru.rian.dynamics.retrofit.model.ApiRequest
import ru.rian.dynamics.utils.Consts.SharedPrefs.PLAYER_ID
import ru.rian.dynamics.utils.LocaleHelper
import ru.rian.dynamics.utils.PreferenceHelper
import ru.rian.dynamics.utils.PreferenceHelper.defaultPrefs
import javax.inject.Inject
import ru.rian.dynamics.utils.PreferenceHelper.get
import ru.rian.dynamics.utils.PreferenceHelper.set

class MainViewModel @Inject constructor(
    private var dataManager: DataManager,
    private var schedulerProvider: SchedulerProvider
) {

    var loading: Boolean? = null

    fun setIsLoading(loading: Boolean) {
        this.loading = loading
    }


    fun provideHS(): Observable<List<ApiRequest>>? {
        setIsLoading(true)
        var retVal: Observable<List<ApiRequest>>? = null
        val prefs = defaultPrefs()
        val playerId: String? = prefs[PLAYER_ID]
        if (TextUtils.isEmpty(playerId)) {
            OneSignal.idsAvailable { userId, _ ->
                prefs[PLAYER_ID] = userId
                reqHs(playerId)
            }
        } else {
            reqHs(playerId)
        }
        return retVal;
    }

    fun reqHs(userId: String?): Observable<List<ApiRequest>>? {
        return dataManager.requestHSQuery(FlavorConstants.QUERY_HS_APP_ID_DYNAMICS, userId, LocaleHelper.getLanguage())
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .map { result -> result.apiRequestArray }
    }
}