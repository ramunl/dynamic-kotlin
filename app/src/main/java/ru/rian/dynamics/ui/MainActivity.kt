package ru.rian.dynamics.ui

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import com.onesignal.OneSignal
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_content_common.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dynamic_app_bar.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import ru.rian.dynamics.BuildConfig
import ru.rian.dynamics.InitApp
import ru.rian.dynamics.R
import ru.rian.dynamics.R.drawable.ic_terminal_feeds
import ru.rian.dynamics.R.drawable.ic_terminal_feeds_badge
import ru.rian.dynamics.R.id.menu_action_toolbar_select_feed
import ru.rian.dynamics.R.layout.nav_header_main
import ru.rian.dynamics.R.string.*
import ru.rian.dynamics.SchedulerProvider
import ru.rian.dynamics.di.component.DaggerActivityComponent
import ru.rian.dynamics.di.model.FeedViewModel
import ru.rian.dynamics.di.model.MainViewModel
import ru.rian.dynamics.di.module.ActivityModule
import ru.rian.dynamics.retrofit.model.Feed
import ru.rian.dynamics.retrofit.model.HSResult
import ru.rian.dynamics.retrofit.model.Source
import ru.rian.dynamics.ui.fragments.FeedFragment
import ru.rian.dynamics.ui.fragments.TerminalLoginFragment
import ru.rian.dynamics.ui.fragments.UserFeedsFragment
import ru.rian.dynamics.ui.helpers.LoadingObserver.addLoadingObserver
import ru.rian.dynamics.ui.helpers.LoadingObserver.removeLoadingObserver
import ru.rian.dynamics.ui.helpers.SnackContainerProvider
import ru.rian.dynamics.ui.helpers.addDrawerMenuItem
import ru.rian.dynamics.ui.helpers.addMainMenuItem
import ru.rian.dynamics.utils.*
import ru.rian.dynamics.utils.PreferenceHelper.get
import ru.rian.dynamics.utils.PreferenceHelper.getToken
import ru.rian.dynamics.utils.PreferenceHelper.getUserName
import ru.rian.dynamics.utils.PreferenceHelper.prefs
import ru.rian.dynamics.utils.PreferenceHelper.set
import javax.inject.Inject


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener,
    SnackContainerProvider {

    @Inject
    lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var viewModelFeed: FeedViewModel


    private var showBadgeFeedBtnFlag = false

    private var hsResult: HSResult? = null

    companion object : KLoggerWrap(MainActivity::class)

    override fun showMessage(message: String) {
        val ctx = InitApp.appContext()
        kDebug(message)
        Snackbar.make(
            activityRootLayout,
            message,
            Snackbar.LENGTH_SHORT
        )
            .setActionTextColor(ctx.resources.getColor(R.color.action_color))
            .show()
    }

    override fun showError(err: Throwable, methodToInvoke: SnackContainerProvider.ActionToInvoke) {
        val ctx = InitApp.appContext()
        kError(err)
        Snackbar.make(
            activityRootLayout,
            if (BuildConfig.DEBUG) err.toString() else ctx.getString(R.string.connection_error_title),
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction(R.string.try_again) { methodToInvoke.invokeMethod() }
            .setActionTextColor(ctx.resources.getColor(R.color.action_color))
            .show()
    }


    override fun onDestroy() {
        super.onDestroy()
        removeLoadingObserver(::onLoadingStateChanged)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("hsResult", hsResult)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbarDynamic)

        val activityComponent = DaggerActivityComponent
            .builder()
            .appComponent(InitApp.get(this).component())
            .activityModule(ActivityModule(this, SchedulerProvider()))
            .build()
        activityComponent.inject(this)

        addLoadingObserver(::onLoadingStateChanged)

        window.setBackgroundDrawableResource(R.color.transparent)

        hsResult = savedInstanceState?.getSerializable("hsResult") as HSResult?

        hsResult?.let { addDrawerMenuItems(hsResult) }


        setupFeedsLoaderListener()

    }

    private fun requestHS() {
        kDebug("requestHS")
        val playerId: String? = prefs()[PLAYER_ID]
        if (TextUtils.isEmpty(playerId)) {
            OneSignal.idsAvailable { userId, _ ->
                prefs()[PLAYER_ID] = userId
                doRequestHS()
            }
        } else {
            doRequestHS()
        }
    }

    private fun requestFeeds() {
        kDebug("requestFeeds")
        val apiRequestArray = apiRequestArray()
        var disposable = mainViewModel.provideFeeds(apiRequestArray?.getFeeds!!).subscribe(
            { result ->
                result?.feeds?.let {
                    kDebug("result $result")
                    insertFeeds(it)
                    feedSelected = it[0]
                    showArticlesFragment(feedSelected!!, apiRequestArray.getArticles!!)
                }
            }, { e -> showError(e, SnackContainerProvider.ActionToInvoke(::requestFeeds)) })
        disposable?.let { compositeDisposable.add(it) }
    }


    private fun doRequestHS() {
        val disposable = mainViewModel.provideHS()
            ?.subscribe({ result ->
                kDebug("result ${result.toString()}")
                hsResult = result
                addDrawerMenuItems(result)
                requestFeeds()
            }, { e ->
                showError(e, SnackContainerProvider.ActionToInvoke(::doRequestHS))
            })
        disposable?.let { compositeDisposable.add(it) }
    }

    private fun addDrawerMenuItems(result: HSResult?) {
        navView.inflateMenu(R.menu.nav_drawer_stub)
        var isToken = mainViewModel.isTokenPresented()

        var menu = navView.menu
        menu.clear()
        addDrawerMenuItem(
            menu,
            R.drawable.ic_menu_ddn,
            R.id.nav_news,
            if (isToken) getString(terminal_title) else result?.application
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

        navView.inflateHeaderView(nav_header_main)?.apply {
            getToken()?.let { username_text_view.text = getUserName() }
        }

    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        addMainMenuItem(
            menu,
            if (showBadgeFeedBtnFlag) ic_terminal_feeds_badge else ic_terminal_feeds,
            menu_action_toolbar_select_feed
        )
        menuInflater.inflate(R.menu.main, menu)

        return super.onCreateOptionsMenu(menu)
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
            R.id.nav_terminal -> showTerminalLoginFragment()
            R.id.nav_tapes -> showUserFeedsFragment()
            R.id.nav_news -> {
                if (feedSelected == null)
                    requestHS()
                else
                    showArticlesFragment(feedSelected!!, apiRequestArray()!!.getArticles!!)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun showTerminalLoginFragment() {
        replaceFragment(
            TerminalLoginFragment.newInstance(apiRequestArray()!!.login!!),
            FragmentId.TERMINAL_LOGIN_FRAGMENT_ID
        )
    }


    private fun apiRequestArray() = hsResult!!.apiRequestArray

    private fun showUserFeedsFragment() {
        replaceFragment(
            UserFeedsFragment.newInstance(apiRequestArray()!!.getArticles!!),
            FragmentId.USER_FEEDS_FRAGMENT_ID
        )
    }


    var feedSelected: Feed? = null

    private fun showArticlesFragment(feed: Feed, source: Source) {
        val fragmentId = FragmentId.MAIN_FEED_FRAGMENT_ID
        replaceFragment(FeedFragment.newInstance(feed, source), fragmentId)
    }

    private fun insertFeeds(feeds: List<Feed>) {
        kDebug("insertFeeds size = ${feeds.size}")
        compositeDisposable.add(
            viewModelFeed.insert(feeds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ },
                    { e -> showError(e, SnackContainerProvider.ActionToInvoke(::doRequestHS)) })
        )
    }

    private fun setupFeedsLoaderListener() {
        compositeDisposable.add(
            viewModelFeed.getFeedsByType(FEED_TYPE_COMMON)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        if (it.isNotEmpty()) {
                            showBadgeFeedBtnFlag = it.size > 1
                            invalidateOptionsMenu()
                        }
                    },
                    { e -> showError(e, SnackContainerProvider.ActionToInvoke(::requestFeeds)) })
        )
    }


    private fun onLoadingStateChanged(isLoading: Boolean) {
        kDebug("onLoadingStateChanged $isLoading")
        progressBarMain.visibility = if (isLoading) VISIBLE else GONE
    }

    override fun onStart() {
        super.onStart()
        kDebug("onStart")
    }

    override fun onStop() {
        super.onStop()
        kDebug("onStop")
    }

    override fun onResume() {
        super.onResume()
        kDebug("onResume")
        if (hsResult == null) {
            requestHS()
        }
    }

}
