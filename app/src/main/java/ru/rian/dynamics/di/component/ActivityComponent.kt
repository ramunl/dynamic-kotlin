package ru.rian.dynamics.di.component

import dagger.Component
import ru.rian.dynamics.di.PerActivity
import ru.rian.dynamics.di.module.ActivityModule
import ru.rian.dynamics.di.module.DaoModule
import ru.rian.dynamics.di.model.HttpModule
import ru.rian.dynamics.ui.MainActivity

@PerActivity
@Component(modules = [HttpModule::class, DaoModule::class, ActivityModule::class], dependencies = [AppComponent::class])

interface ActivityComponent {
    fun inject(activity: MainActivity)
}