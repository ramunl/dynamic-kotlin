package ru.rian.dynamics.di.component

import android.content.Context
import dagger.Component
import ru.rian.dynamics.HttpReqManager
import ru.rian.dynamics.di.ApplicationContext
import ru.rian.dynamics.di.module.AppModule
import ru.rian.dynamics.di.model.HttpModule
import javax.inject.Singleton


@Singleton
@Component(modules = [AppModule::class, HttpModule::class])
interface AppComponent {
    @ApplicationContext
    fun context(): Context

    fun httpReqManager(): HttpReqManager
}