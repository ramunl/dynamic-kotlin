package ru.rian.dynamics.di.component

import android.content.Context
import dagger.Component
import ru.rian.dynamics.di.model.AppModule
import ru.rian.dynamics.di.ApplicationContext
import javax.inject.Singleton


@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    @ApplicationContext
    fun context(): Context

}