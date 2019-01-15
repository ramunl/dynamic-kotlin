package ru.rian.dynamics.di.module

import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity
import dagger.Module
import dagger.Provides
import ru.rian.dynamics.HttpReqManager
import ru.rian.dynamics.SchedulerProvider
import ru.rian.dynamics.db.ViewModelFactory
import ru.rian.dynamics.di.model.FeedViewModel
import ru.rian.dynamics.di.model.MainViewModel
import ru.rian.dynamics.di.model.TerminalViewModel


@Module
class ActivityModule(private val activity: FragmentActivity, private val schedulerProvider: SchedulerProvider) {

    @Provides
    fun provideMainViewModel(httpReqManager: HttpReqManager): MainViewModel {
        return MainViewModel(httpReqManager, schedulerProvider)
    }

    @Provides
    fun provideFeedViewModel(viewModelFactory: ViewModelFactory): FeedViewModel {
        return ViewModelProviders.of(activity, viewModelFactory).get(FeedViewModel::class.java)
    }

    @Provides
    fun provideTerminalLoginViewModel(httpReqManager: HttpReqManager): TerminalViewModel {
        return TerminalViewModel(httpReqManager, schedulerProvider)
    }
}