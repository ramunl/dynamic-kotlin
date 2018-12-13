package ru.rian.dynamics.ui

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import ru.rian.dynamics.InitApp
import ru.rian.dynamics.R
import ru.rian.dynamics.SchedulerProvider
import ru.rian.dynamics.di.component.DaggerActivityComponent
import ru.rian.dynamics.di.model.ActivityModule
import java.util.logging.Logger
import javax.inject.Inject
import android.view.View
import kotlinx.android.synthetic.main.drawer_menu_item_layout.view.*
import ru.rian.dynamics.BuildConfig
import ru.rian.dynamics.R.string.*
import ru.rian.dynamics.retrofit.model.HSResult
import ru.rian.dynamics.utils.TRENDING


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        compositeDisposable = CompositeDisposable()
        val activityComponent = DaggerActivityComponent
            .builder()
            .appComponent(InitApp.get(this).component())
            .activityModule(ActivityModule(SchedulerProvider()))
            .build()
        activityComponent.inject(this)

        requestHS()

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        window.setBackgroundDrawableResource(R.color.transparent)
    }

    private fun addMenuItems(result: HSResult?) {
        navView.inflateMenu(R.menu.nav_drawer_stub)
        var isToken = mainViewModel.isTokenPresented()
        addMenuItem(
            if (isToken) getString(terminal_title) else result?.application,
            R.drawable.ic_menu_ddn,
            R.id.nav_news
        )
        if (isToken) {
            addMenuItem(stories_tab_title, R.drawable.ic_menu_story, R.id.nav_story)
        } else {
            addMenuItem(terminal_title, R.drawable.ic_menu_terminal, R.id.nav_terminal)
        }
        addMenuItem(tapes_tab_title, R.drawable.ic_menu_my_feeds, R.id.nav_tapes)


        addMenuItem(settings_notification_title, R.drawable.ic_menu_notifications, R.id.nav_notifications)
        addMenuItem(settings_events_title, R.drawable.ic_menu_events, R.id.nav_events)
        addMenuItem(settings_about_title, R.drawable.ic_menu_about, R.id.nav_about)
        if (isToken) {
            addMenuItem(logout, R.drawable.ic_menu_exit, R.id.nav_terminal_logout)
        }
        if (BuildConfig.FLAVOR.equals(TRENDING)) {
            addMenuItem(choose_lang_title, R.drawable.ic_menu_language, R.id.nav_lang)
        }
    }

    /*
    <item name="nav_news" type="id"/>
    <item name="nav_tapes" type="id"/>
    <item name="nav_terminal_login" type="id"/>
    <item name="nav_terminal_logout" type="id"/>
    <item name="nav_notifications" type="id"/>
    <item name="nav_events" type="id"/>
    <item name="nav_about" type="id"/>
    <item name="nav_terminal" type="id"/>
    <item name="nav_story" type="id"/>
    <item name="nav_lang" type="id"/>
    *     <!--<item
                android:id="@+id/nav_terminal"
                android:icon="@drawable/nav_terminal"
                android:title="@string/terminal_title"/>
        <item
                android:id="@+id/nav_story"
                android:icon="@drawable/ic_menu_story"
                android:title="@string/stories_tab_title"/>
        <item
                android:id="@+id/nav_tapes"
                android:icon="@drawable/ic_menu_my_feeds"
                android:title="@string/tapes_tab_title"/>
        <item
                android:id="@+id/nav_notifications"
                android:icon="@drawable/ic_menu_notifications"
                android:title="@string/settings_notification_title"/>
        <item
                android:id="@+id/nav_events"
                android:title=""
                app:actionLayout="@layout/nav_events_layout"/>
        <item
                android:id="@+id/nav_about"
                android:icon="@drawable/ic_menu_about"
                android:title="@string/settings_about_title"/> -->
    * */

    private fun addMenuItem(title: Int, iconResId: Int, itemId: Int) {
        addMenuItem(getString(title), iconResId, itemId)
    }

    private fun addMenuItem(title: String?, iconResId: Int, itemId: Int) {
        val menuItem = navView.menu.add(0, itemId, 0, null)
        var menuItemActionView = View.inflate(this, R.layout.drawer_menu_item_layout, null)
        menuItemActionView.drawerMenuItemIcon.setImageResource(iconResId)
        menuItemActionView.drawerMenuItemTitle.text = title
        menuItem.actionView = menuItemActionView
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            /* R.id.nav_share -> {

             }
             R.id.nav_send -> {

             }*/
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


    @Inject
    lateinit var mainViewModel: MainViewModel
    private lateinit var compositeDisposable: CompositeDisposable
    private var disposable: Disposable? = null

    companion object {
        val Log = Logger.getLogger(MainActivity::class.java.name)
    }


    private fun requestHS() {
        disposable = mainViewModel.provideHS()
            ?.subscribe({ result ->
                addMenuItems(result)
            }, { e ->
                e.printStackTrace()
                Snackbar.make(rootLayout, getString(R.string.connection_error_title), Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.try_again) { requestHS() }
                    .setActionTextColor(resources.getColor(R.color.action_color))
                    .show()
            })
    }

    override fun onResume() {
        super.onResume()
        if (disposable != null) {
            bind()
        }
    }

    private fun bind() {
        compositeDisposable.add(disposable!!)
    }

    private fun unbind() {
        compositeDisposable.clear()
    }

    override fun onPause() {
        super.onPause()
        if (compositeDisposable.size() > 0) {
            unbind()
        }
    }
}
