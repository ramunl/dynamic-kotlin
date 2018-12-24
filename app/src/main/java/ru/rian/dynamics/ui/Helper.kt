package ru.rian.dynamics.ui;

import android.support.design.widget.Snackbar
import ru.rian.dynamics.BuildConfig
import ru.rian.dynamics.InitApp
import ru.rian.dynamics.R

fun showError(container: SnackContainerProvider, e: Throwable, actionToCallOnError: () -> (Unit)) {
    val ctx = InitApp.appContext()
    e.printStackTrace()
    Snackbar.make(
        container.getSnackContainer(),
        if (BuildConfig.DEBUG) e.toString() else ctx.getString(R.string.connection_error_title),
        Snackbar.LENGTH_INDEFINITE
    )
        .setAction(R.string.try_again) { actionToCallOnError.invoke() }
        .setActionTextColor(ctx.resources.getColor(R.color.action_color))
        .show()
}

