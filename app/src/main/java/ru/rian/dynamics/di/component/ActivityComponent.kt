package ru.rian.dynamics.di.component

import android.app.Activity
import dagger.Component
import ru.rian.dynamics.di.PerActivity
import ru.rian.dynamics.di.model.ActivityModule
import ru.rian.dynamics.di.model.NetModule

@PerActivity
@Component(modules = [NetModule::class, ActivityModule::class], dependencies = [AppComponent::class])

interface ActivityComponent {
    fun inject(activity: Activity)
}