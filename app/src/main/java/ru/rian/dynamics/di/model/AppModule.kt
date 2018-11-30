package ru.rian.dynamics.di.model

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import ru.rian.dynamics.di.ApplicationContext

@Module
class AppModule(private val app: Application) {

    @Provides
    fun providesApp(): Application {
        return app
    }

    @Provides
    @ApplicationContext
    fun provideContext(): Context {
        return app
    }
}