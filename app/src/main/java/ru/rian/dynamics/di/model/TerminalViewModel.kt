package ru.rian.dynamics.di.model

import android.text.TextUtils
import io.reactivex.Observable
import ru.rian.dynamics.HttpReqManager
import ru.rian.dynamics.SchedulerProvider
import ru.rian.dynamics.retrofit.model.LoginDataModel
import ru.rian.dynamics.ui.helpers.LoadingObserver.loading
import ru.rian.dynamics.utils.LOGIN_PATH
import ru.rian.dynamics.utils.LOGIN_STRING_KEY
import ru.rian.dynamics.utils.PreferenceHelper.get
import ru.rian.dynamics.utils.PreferenceHelper.prefs
import ru.rian.dynamics.utils.PreferenceHelper.set
import ru.rian.dynamics.utils.TOKEN_STRING_KEY
import javax.inject.Inject


class TerminalViewModel @Inject constructor(
    private var httpReqManager: HttpReqManager,
    private var schedulerProvider: SchedulerProvider
) {

    private val prefs = prefs()

    fun isTokenPresented(): Boolean {
        val isToken: String? = prefs[TOKEN_STRING_KEY]
        return !TextUtils.isEmpty(isToken)
    }

    fun provideTerminalLogin(username: String, password: String): Observable<LoginDataModel?>? {
        loading = true
        return httpReqManager.requestLogin<LoginDataModel?>(LOGIN_PATH, username, password)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .doFinally { loading = false } }
    }
