package ru.rian.dynamics.di.component

import dagger.Component
import ru.rian.dynamics.di.PerActivity
import ru.rian.dynamics.di.model.ActivityModule
import ru.rian.dynamics.di.model.NetModule
import ru.rian.dynamics.ui.ArticleFragment

@PerActivity
@Component(modules = [NetModule::class, ActivityModule::class], dependencies = [AppComponent::class])

interface FragmentComponent {
    fun inject(fragment: ArticleFragment)
}