package ru.rian.dynamics.di.component

import dagger.Component
import ru.rian.dynamics.di.PerActivity
import ru.rian.dynamics.di.module.ActivityModule
import ru.rian.dynamics.di.module.DaoModule
import ru.rian.dynamics.di.model.HttpModule
import ru.rian.dynamics.ui.fragments.FeedFragment

@PerActivity
@Component(modules = [HttpModule::class, ActivityModule::class, DaoModule::class], dependencies = [AppComponent::class])

interface FeedFragmentComponent {
    fun inject(fragment: FeedFragment)
}