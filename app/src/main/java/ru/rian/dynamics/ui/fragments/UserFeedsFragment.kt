package ru.rian.dynamics.ui.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_content_common.*
import kotlinx.android.synthetic.main.fragment_user_feeds.view.*
import ru.rian.dynamics.InitApp
import ru.rian.dynamics.R
import ru.rian.dynamics.SchedulerProvider
import ru.rian.dynamics.di.component.DaggerUserFeedsFragmentComponent
import ru.rian.dynamics.di.module.ActivityModule
import ru.rian.dynamics.di.model.FeedViewModel
import ru.rian.dynamics.retrofit.model.Feed
import ru.rian.dynamics.retrofit.model.Source
import ru.rian.dynamics.ui.FeedActivity
import ru.rian.dynamics.ui.fragments.adapters.UserFeedsAdapter
import ru.rian.dynamics.ui.fragments.listeners.OnUserFeedsListInteractionListener
import ru.rian.dynamics.ui.helpers.SnackContainerProvider
import ru.rian.dynamics.utils.FEED_TYPE_USER
import ru.rian.dynamics.utils.KLoggerWrap
import javax.inject.Inject


open class UserFeedsFragment : BaseFragment(), OnUserFeedsListInteractionListener {

    fun feedSource() = arguments!!.getSerializable(FeedFragment.ARG_FEED_SOURCE) as Source

    override fun onUserFeedsListInteraction(item: Feed?) {
        FeedActivity.start(context!!,item!!, feedSource())
    }

    companion object {
        const val ARG_FEED_SOURCE = "feed_url"
        @JvmStatic
        fun newInstance(feedSource: Source) = UserFeedsFragment().apply {
            arguments = Bundle().apply {
                putSerializable(UserFeedsFragment.ARG_FEED_SOURCE, feedSource)
            }
        }

        var logger = KLoggerWrap(UserFeedsFragment::class)
    }

    @Inject
    lateinit var viewModelFeed: FeedViewModel

    private lateinit var listAdapter: UserFeedsAdapter

    private lateinit var recyclerView: RecyclerView

    private lateinit var placeHolder: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fragmentComponent = DaggerUserFeedsFragmentComponent
            .builder()
            .appComponent(InitApp.get(context!!).component())
            .activityModule(ActivityModule(context as FragmentActivity, SchedulerProvider()))
            .build()
        fragmentComponent.inject(this)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_user_feeds, container, false)

        recyclerView = view.recyclerView

        if (view.recyclerView is RecyclerView) {

            listAdapter = UserFeedsAdapter(view.context, this)

            view.recyclerView.adapter = listAdapter

            (view.recyclerView.adapter as UserFeedsAdapter)

            with(view.recyclerView) {
                layoutManager = LinearLayoutManager(context)
                val dividerItemDecoration =
                    DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation)

                dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.divider))

                recyclerView.addItemDecoration(dividerItemDecoration)
            }
        }
        placeHolder = view.placeHolder
        setupFeedsLoaderListener()
        setFloatCreateUserFeedButton()
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    private fun setupFeedsLoaderListener() {
        compositeDisposable.add(
            viewModelFeed.getFeedsByType(FEED_TYPE_USER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        if (it.isNotEmpty()) {
                            placeHolder.visibility = GONE
                            listAdapter.updateData(it)
                        } else {
                            placeHolder.visibility = VISIBLE
                        }
                    },
                    { e -> logger.kError(e) })
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        setupToolBarTitle(getString(R.string.tapes_tab_title))
    }

    private fun setFloatCreateUserFeedButton() {
        if (isAdded) {
            val res = R.drawable.plus_fab
            (context as AppCompatActivity).buttonFloatDynamic.setImageDrawable(
                ContextCompat.getDrawable(context!!, res)
            )
        }
    }

}