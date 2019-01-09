package ru.rian.dynamics.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import ru.rian.dynamics.InitApp

fun hideKeyboard(view:View) {
    try {
        val imm = InitApp.appContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    } catch (e: Exception) {
        e.printStackTrace()
    }

}