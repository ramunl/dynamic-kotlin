package ru.rian.dynamics.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.view.MenuItem
import kotlinx.android.synthetic.main.dynamic_app_bar.*
import ru.rian.dynamics.R
import ru.rian.dynamics.retrofit.model.Feed
import ru.rian.dynamics.retrofit.model.Source
import ru.rian.dynamics.ui.fragments.FeedFragment
import ru.rian.dynamics.ui.fragments.FeedFragment.Companion.ARG_FEED
import ru.rian.dynamics.ui.fragments.FeedFragment.Companion.ARG_FEED_SOURCE
import ru.rian.dynamics.utils.FragmentId

class FeedActivity : BaseActivity() {
    companion object {
        @JvmStatic
        fun start(context: Context, feed: Feed, feedSource: Source) {
            val intent = Intent(context, FeedActivity::class.java).apply {
                putExtra(ARG_FEED, feed)
                putExtra(ARG_FEED_SOURCE, feedSource)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        setSupportActionBar(toolbarDynamic)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        if(savedInstanceState == null) {
            showArticlesFragment()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                finish()
                //NavUtils.navigateUpFromSameTask(this);
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showArticlesFragment() {
        val fragmentId = FragmentId.USER_FEED_FRAGMENT_ID
        replaceFragment(FeedFragment.newInstance(intent.extras!!), fragmentId)
    }
}
