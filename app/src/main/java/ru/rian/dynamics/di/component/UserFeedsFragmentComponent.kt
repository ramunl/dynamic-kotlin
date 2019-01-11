package ru.rian.dynamics.di.component

import dagger.Component
import ru.rian.dynamics.di.PerActivity
import ru.rian.dynamics.di.model.ActivityModule
import ru.rian.dynamics.di.model.DaoModule
import ru.rian.dynamics.di.model.HttpModule
import ru.rian.dynamics.ui.fragments.UserFeedsFragment

@PerActivity
@Component(modules = [HttpModule::class, ActivityModule::class, DaoModule::class], dependencies = [AppComponent::class])

interface UserFeedsFragmentComponent {
    fun inject(fragment: UserFeedsFragment)
}