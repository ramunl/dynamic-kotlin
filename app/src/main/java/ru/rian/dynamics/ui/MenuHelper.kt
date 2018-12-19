package ru.rian.dynamics.ui

import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.drawer_menu_item_layout.view.*
import ru.rian.dynamics.InitApp
import ru.rian.dynamics.R
import ru.rian.dynamics.utils.LocaleHelper.getString

fun addDrawerMenuItem(menu: Menu, title: Int, iconResId: Int, itemId: Int) {
    addDrawerMenuItem(menu, getString(title), iconResId, itemId)
}

fun addDrawerMenuItem(menu: Menu, title: String?, iconResId: Int, itemId: Int) {
    val menuItem = menu.add(0, itemId, 0, null)
    var menuItemActionView = View.inflate(InitApp.appContext(), R.layout.drawer_menu_item_layout, null)
    menuItemActionView.drawerMenuItemIcon.setImageResource(iconResId)
    menuItemActionView.drawerMenuItemTitle.text = title
    menuItem.actionView = menuItemActionView
}

fun addMainMenuItem(menu: Menu, iconResId: Int, itemId: Int, title: String? = "") {
    val menuItem = menu.add(0, itemId, 0, title)
    menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
    menuItem.icon = InitApp.appContext().resources.getDrawable(iconResId)
}

