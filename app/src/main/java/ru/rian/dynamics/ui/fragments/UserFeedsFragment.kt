package ru.rian.dynamics.ui.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_article_list.view.*
import ru.rian.dynamics.InitApp
import ru.rian.dynamics.R
import ru.rian.dynamics.SchedulerProvider
import ru.rian.dynamics.di.component.DaggerUserFeedsFragmentComponent
import ru.rian.dynamics.di.model.ActivityModule
import ru.rian.dynamics.di.model.FeedViewModel
import ru.rian.dynamics.retrofit.model.Feed
import ru.rian.dynamics.ui.FeedsAdapter
import ru.rian.dynamics.ui.SnackContainerProvider
import ru.rian.dynamics.utils.FEED_TYPE_USER
import ru.rian.dynamics.utils.KLoggerWrap
import javax.inject.Inject

class UserFeedsFragment: Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = UserFeedsFragment()
        var logger = KLoggerWrap(UserFeedsFragment::class)
    }

    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: Feed?)
    }

    @Inject
    lateinit var viewModelFeed: FeedViewModel

    private lateinit var listAdapter: FeedsAdapter

    private lateinit var recyclerView: RecyclerView

    private lateinit var compositeDisposable: CompositeDisposable

    private var listener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        compositeDisposable = CompositeDisposable()
        val fragmentComponent = DaggerUserFeedsFragmentComponent
            .builder()
            .appComponent(InitApp.get(context!!).component())
            .activityModule(ActivityModule(context as FragmentActivity, SchedulerProvider()))
            .build()
        fragmentComponent.inject(this)
    }
    

    private fun snackContainerProvider(): SnackContainerProvider {
        return context as SnackContainerProvider
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_user_feeds, container, false)

        recyclerView = view.recyclerView

        if (view.recyclerView is RecyclerView) {

            listAdapter = FeedsAdapter(view.context, listener)

            view.recyclerView.adapter = listAdapter

            (view.recyclerView.adapter as FeedsAdapter)

            with(view.recyclerView) {
                layoutManager = LinearLayoutManager(context)
                val dividerItemDecoration =
                    DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation)

                dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.divider))

                recyclerView.addItemDecoration(dividerItemDecoration)
            }
        }
        setupFeedsLoaderListener()
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun setupFeedsLoaderListener() {
        compositeDisposable.add(
            viewModelFeed.getFeedsByType(FEED_TYPE_USER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        listAdapter.updateData(it)
                    },
                    { e -> logger.kError(e) })
        )
    }


}