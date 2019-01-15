package ru.rian.dynamics.utils

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import ru.rian.dynamics.InitApp

fun hideKeyboard(context: Context) {
    try {
        var view = (context as AppCompatActivity).currentFocus
        val imm = InitApp.appContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    } catch (e: Exception) {
        e.printStackTrace()
    }

}