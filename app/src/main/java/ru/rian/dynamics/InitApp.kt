package ru.rian.dynamics

import android.app.Application
import android.content.Context
import ru.rian.dynamics.di.component.AppComponent
import ru.rian.dynamics.di.component.DaggerAppComponent
import ru.rian.dynamics.di.model.AppModule


class InitApp : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: InitApp? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }

        fun get(context: Context): InitApp {
            return context.applicationContext as InitApp
        }
    }

    fun component(): AppComponent {
        return DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }
}