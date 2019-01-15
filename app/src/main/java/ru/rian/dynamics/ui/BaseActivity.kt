package ru.rian.dynamics.ui

import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import ru.rian.dynamics.R
import ru.rian.dynamics.utils.FragmentId

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {


    protected var compositeDisposable: CompositeDisposable = CompositeDisposable()

    private fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
        beginTransaction().func().commit()
    }

    protected fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: FragmentId) {
        supportFragmentManager.inTransaction { replace(R.id.fragmentContainer, fragment, frameId.name) }
    }


    override fun onPause() {
        super.onPause()
        if (compositeDisposable.size() > 0) {
            compositeDisposable.clear()
        }
    }
}