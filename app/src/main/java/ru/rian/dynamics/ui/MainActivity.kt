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

        navView.inflateMenu( if (mainViewModel.isTokenPresented()) R.menu.nav_drawer_terminal else R.menu.nav_drawer_dynamic )
        navView.setNavigationItemSelectedListener(this)

        window.setBackgroundDrawableResource(R.color.transparent)
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
                //val res = result.also {  }
                //val intent = Intent(this, MainActivity::class.java)
                // intent.putExtra("sources", res.)
                ///startActivity(intent)
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
