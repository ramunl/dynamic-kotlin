package ru.rian.dynamics.di.component

import dagger.Component
import ru.rian.dynamics.di.PerActivity
import ru.rian.dynamics.di.module.ActivityModule
import ru.rian.dynamics.di.module.DaoModule
import ru.rian.dynamics.di.model.HttpModule
import ru.rian.dynamics.ui.fragments.FeedFragment
import ru.rian.dynamics.ui.fragments.TerminalLoginFragment

@PerActivity
@Component(modules = [HttpModule::class, ActivityModule::class], dependencies = [AppComponent::class])

interface TerminalLoginFragmentComponent {
    fun inject(fragment: TerminalLoginFragment)
}