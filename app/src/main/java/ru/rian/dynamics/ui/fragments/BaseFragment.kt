package ru.rian.dynamics.ui.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import ru.rian.dynamics.ui.helpers.SnackContainerProvider


open class BaseFragment : Fragment() {

    protected fun showError(e: Throwable, methodToInvoke: SnackContainerProvider.ActionToInvoke) {
        (context as SnackContainerProvider).showError(e, methodToInvoke)
    }

    protected var compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    fun setupToolBarTitle(title: String) {
        (context as AppCompatActivity).supportActionBar?.title = title
    }

    override fun onPause() {
        super.onPause()
        if (compositeDisposable.size() > 0) {
            compositeDisposable.clear()
        }
    }
}
