package ru.rian.dynamics.di.component

import android.content.Context
import dagger.Component
import ru.rian.dynamics.di.ApplicationContext
import ru.rian.dynamics.di.model.AppModule
import ru.rian.dynamics.di.model.NetModule
import javax.inject.Singleton


@Singleton
@Component(modules = [AppModule::class, NetModule::class])
interface AppComponent {
    @ApplicationContext
    fun context(): Context
}