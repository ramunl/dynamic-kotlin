package ru.rian.dynamics.ui

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem
import kotlinx.android.synthetic.main.app_bar.*

import ru.rian.dynamics.R
import ru.rian.dynamics.retrofit.model.Feed
import ru.rian.dynamics.retrofit.model.Source
import ru.rian.dynamics.ui.fragments.ArticleFragment
import ru.rian.dynamics.utils.FragmentId

class FeedActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        setSupportActionBar(toolbarDynamic)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeButtonEnabled(true)
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

    private fun showArticlesFragment(feed: Feed, source: Source) {
        val fragmentId = FragmentId.ARTICLE_FRAGMENT_ID
        //setupFloatButton(fragmentId)
        replaceFragment(ArticleFragment.newInstance(feed.sid, source), fragmentId)
    }
}
