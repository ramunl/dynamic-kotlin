package ru.rian.dynamics.di.component

import dagger.Component
import ru.rian.dynamics.di.PerActivity
import ru.rian.dynamics.di.model.ActivityModule
import ru.rian.dynamics.di.model.NetModule
import ru.rian.dynamics.ui.MainActivity

@PerActivity
@Component(modules = [NetModule::class, DbModule::class, ActivityModule::class], dependencies = [AppComponent::class])

interface ActivityComponent {
    fun inject(activity: MainActivity)
}