package ru.rian.dynamics.ui

import android.text.TextUtils
import com.onesignal.OneSignal
import io.reactivex.Observable
import ru.rian.dynamics.DataManager
import ru.rian.dynamics.FlavorConstants
import ru.rian.dynamics.SchedulerProvider
import ru.rian.dynamics.retrofit.model.HSResult
import ru.rian.dynamics.utils.LocaleHelper
import ru.rian.dynamics.utils.PLAYER_ID
import ru.rian.dynamics.utils.PreferenceHelper.defaultPrefs
import ru.rian.dynamics.utils.PreferenceHelper.get
import ru.rian.dynamics.utils.PreferenceHelper.set
import ru.rian.dynamics.utils.TOKEN_STRING_KEY
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private var dataManager: DataManager,
    private var schedulerProvider: SchedulerProvider
) {

    var loading: Boolean? = null
    val prefs = defaultPrefs()

    fun isTokenPresented(): Boolean {
        val isToken: String? = prefs[TOKEN_STRING_KEY]
        return !TextUtils.isEmpty(isToken)
    }

    fun provideHS(): Observable<HSResult?>? {
        var retVal: Observable<HSResult?>? = null

        val playerId: String? = prefs[PLAYER_ID]
        if (TextUtils.isEmpty(playerId)) {
            OneSignal.idsAvailable { userId, _ ->
                prefs[PLAYER_ID] = userId
                retVal = reqHs(playerId)
            }
        } else {
            retVal = reqHs(playerId)
        }
        return retVal;
    }

    fun reqHs(userId: String?): Observable<HSResult?>? {
        return dataManager.requestHSQuery(FlavorConstants.QUERY_HS_APP_ID_DYNAMICS, userId, LocaleHelper.getLanguage())
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .map { result -> result }
    }
}