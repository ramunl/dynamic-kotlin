package ru.rian.dynamics

import io.reactivex.Scheduler


interface ISchedulerProvider {
    fun ui(): Scheduler

    fun computation(): Scheduler

    fun io(): Scheduler
}