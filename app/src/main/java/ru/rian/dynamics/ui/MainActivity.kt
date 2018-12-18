package ru.rian.dynamics.ui

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import com.onesignal.OneSignal
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import ru.rian.dynamics.BuildConfig
import ru.rian.dynamics.InitApp
import ru.rian.dynamics.R
import ru.rian.dynamics.R.drawable.ic_terminal_feeds
import ru.rian.dynamics.R.drawable.ic_terminal_feeds_badge
import ru.rian.dynamics.R.id.menu_action_toolbar_select_feed
import ru.rian.dynamics.R.string.*
import ru.rian.dynamics.SchedulerProvider
import ru.rian.dynamics.db.ViewModelFactory
import ru.rian.dynamics.di.component.DaggerActivityComponent
import ru.rian.dynamics.di.model.ActivityModule
import ru.rian.dynamics.di.model.FeedViewModel
import ru.rian.dynamics.di.model.Injection
import ru.rian.dynamics.di.model.MainViewModel
import ru.rian.dynamics.retrofit.model.HSResult
import ru.rian.dynamics.utils.PLAYER_ID
import ru.rian.dynamics.utils.PreferenceHelper.get
import ru.rian.dynamics.utils.PreferenceHelper.prefs
import ru.rian.dynamics.utils.PreferenceHelper.putHStoPrefs
import ru.rian.dynamics.utils.PreferenceHelper.set
import ru.rian.dynamics.utils.TRENDING
import java.util.logging.Logger
import javax.inject.Inject

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    @Inject
    lateinit var mainViewModel: MainViewModel
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var viewModelFeed: FeedViewModel
    private lateinit var viewModelFactory: ViewModelFactory
    var showBadge = false

    companion object {
        val Log = Logger.getLogger(MainActivity::class.java.name)
    }

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

        val playerId: String? = prefs()[PLAYER_ID]
        if (TextUtils.isEmpty(playerId)) {
            OneSignal.idsAvailable { userId, _ ->
                prefs()[PLAYER_ID] = userId
                requestHS()
            }
        } else {
            requestHS()
        }


        viewModelFactory = Injection.provideViewModelFactory(this)

        viewModelFeed = ViewModelProviders.of(this, viewModelFactory).get(FeedViewModel::class.java)

        compositeDisposable.add(
            viewModelFeed.getFeedsAsync()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                    {
                        var feeds = viewModelFeed.getFeeds()
                        var len = feeds.size
                        showBadge = it.size > 1
                        invalidateOptionsMenu()
                    },
                    { e -> showError(e) })
        )

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()



        window.setBackgroundDrawableResource(R.color.transparent)
    }

    private fun addDrawerMenuItems(result: HSResult?) {
        navView.inflateMenu(R.menu.nav_drawer_stub)
        var isToken = mainViewModel.isTokenPresented()

        var menu = navView.menu
        addDrawerMenuItem(
            menu,
            if (isToken) getString(terminal_title) else result?.application,
            R.drawable.ic_menu_ddn,
            R.id.nav_news
        )
        if (isToken) {
            addDrawerMenuItem(menu, stories_tab_title, R.drawable.ic_menu_story, R.id.nav_story)
        } else {
            addDrawerMenuItem(menu, terminal_title, R.drawable.ic_menu_terminal, R.id.nav_terminal)
        }
        addDrawerMenuItem(menu, tapes_tab_title, R.drawable.ic_menu_my_feeds, R.id.nav_tapes)


        addDrawerMenuItem(menu, settings_notification_title, R.drawable.ic_menu_notifications, R.id.nav_notifications)
        addDrawerMenuItem(menu, settings_events_title, R.drawable.ic_menu_events, R.id.nav_events)
        addDrawerMenuItem(menu, settings_about_title, R.drawable.ic_menu_about, R.id.nav_about)
        if (isToken) {
            addDrawerMenuItem(menu, logout, R.drawable.ic_menu_exit, R.id.nav_terminal_logout)
        }
        if (BuildConfig.FLAVOR.equals(TRENDING)) {
            addDrawerMenuItem(menu, choose_lang_title, R.drawable.ic_menu_language, R.id.nav_lang)
        }
        navView.setNavigationItemSelectedListener(this)
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

        addMainMenuItem(
            menu,
            ic_terminal_feeds_badge,
            if (showBadge) menu_action_toolbar_select_feed else ic_terminal_feeds
        )
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

    private fun requestFeeds() {
        var disposable = mainViewModel.provideFeeds()
            ?.subscribe(
                { result ->
                    viewModelFeed.insert(result?.feeds!!)
                    var feeds = viewModelFeed.getFeeds()
                    var len = feeds.size
                    invalidateOptionsMenu()
                },
                { e ->
                    showError(e)
                })
        compositeDisposable.add(disposable!!)
    }

    private fun showError(e: Throwable) {
        e.printStackTrace()
        Snackbar.make(
            rootLayout,
            if (BuildConfig.DEBUG) e.toString() else getString(R.string.connection_error_title),
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction(R.string.try_again) { requestHS() }
            .setActionTextColor(resources.getColor(R.color.action_color))
            .show()
    }

    private fun requestHS() {
        var disposable = mainViewModel.provideHS()
            ?.subscribe({ result ->
                putHStoPrefs(result)
                addDrawerMenuItems(result)
                requestFeeds()
            }, { e ->
                showError(e)
            })
        compositeDisposable.add(disposable!!)
    }


    override fun onResume() {
        super.onResume()
    }


    override fun onPause() {
        super.onPause()
        if (compositeDisposable.size() > 0) {
            compositeDisposable.clear()
        }
    }
}
