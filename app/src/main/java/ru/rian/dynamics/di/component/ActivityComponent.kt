package ru.rian.dynamics.di.component

import dagger.Component
import ru.rian.dynamics.MainActivity
import ru.rian.dynamics.di.PerActivity
import ru.rian.dynamics.di.model.ActivityModule

@PerActivity
@Component(modules = [ActivityModule::class],
        dependencies = [AppComponent::class]
)

interface ActivityComponent {
    fun inject(mainActivity: MainActivity)
}