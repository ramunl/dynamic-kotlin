package ru.rian.dynamics.di.model

import android.app.Activity
import android.content.Context
import dagger.Module
import dagger.Provides
import ru.rian.dynamics.di.ActivityContext


@Module
class ActivityModule(val activity: Activity) {

    @Provides
    fun providesActivity(): Activity {
        return activity
    }

    @Provides
    @ActivityContext
    fun providesContext(): Context {
        return activity
    }


}