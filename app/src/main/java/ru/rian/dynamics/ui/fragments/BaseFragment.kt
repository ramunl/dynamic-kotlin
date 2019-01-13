package ru.rian.dynamics.ui.fragments

import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity


open class BaseFragment : Fragment() {
    fun setupToolBarTitle(title: String) {
        (context as AppCompatActivity).supportActionBar?.title = title
    }
}
