package ru.rian.dynamics.di.model

import dagger.Module
import dagger.Provides
import ru.rian.dynamics.DataManager
import ru.rian.dynamics.SchedulerProvider
import ru.rian.dynamics.ui.MainViewModel


@Module
class ActivityModule(private val schedulerProvider: SchedulerProvider) {
    @Provides
    fun provideMainViewModel(dataManager: DataManager)
            : MainViewModel {
        return MainViewModel(dataManager, schedulerProvider)
    }

}