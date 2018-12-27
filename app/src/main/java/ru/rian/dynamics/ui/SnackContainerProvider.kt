package ru.rian.dynamics.ui;

interface SnackContainerProvider {
    fun showError(e: Throwable, actionToCallOnError: () -> (Unit))
}

