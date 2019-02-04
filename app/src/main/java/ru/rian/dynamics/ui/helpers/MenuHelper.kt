package ru.rian.dynamics.ui.helpers

import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.drawer_menu_item_layout.view.*
import ru.rian.dynamics.InitApp
import ru.rian.dynamics.R
import ru.rian.dynamics.utils.LocaleHelper.getString

fun addDrawerMenuItem(menu: Menu, title: Int, iconResId: Int, itemId: Int) {
    addDrawerMenuItem(menu, iconResId, itemId, getString(title))
}

fun addDrawerMenuItem(menu: Menu, iconResId: Int, itemId: Int, title: String?) {
    menu.add(0, itemId, 0, null).apply {
        View.inflate(InitApp.appContext(), R.layout.drawer_menu_item_layout, null).apply {
            drawerMenuItemIcon.setImageResource(iconResId)
            drawerMenuItemTitle.text = title
            actionView = this
        }
    }
}

fun addMainMenuItem(menu: Menu, iconResId: Int, itemId: Int, actionViewResId: View? = null, title: String? = "") {
     menu.add(0, itemId, 0, title).apply {
         if (actionViewResId != null) {
             actionView = actionViewResId
             setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_ALWAYS)
         } else {
             setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
         }
         icon = InitApp.appContext().resources.getDrawable(iconResId)
     }
}

