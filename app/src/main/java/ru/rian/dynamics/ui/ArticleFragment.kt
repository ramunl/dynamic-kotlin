package ru.rian.dynamics.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_article_list.view.*
import ru.rian.dynamics.InitApp
import ru.rian.dynamics.R
import ru.rian.dynamics.SchedulerProvider
import ru.rian.dynamics.di.component.DaggerFragmentComponent
import ru.rian.dynamics.di.model.ActivityModule
import ru.rian.dynamics.di.model.MainViewModel
import ru.rian.dynamics.ui.ContactDiffUtilCallBack.Companion.log
import ru.rian.dynamics.ui.dummy.DummyContent.DummyItem
import javax.inject.Inject

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [ArticleFragment.OnListFragmentInteractionListener] interface.
 */
class ArticleFragment : Fragment() {

    // TODO: Customize parameters
    private var feedId: String? = null
    private val columnCount = 1
    @Inject
    lateinit var mainViewModel: MainViewModel

    private var listener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            feedId = it.getString(ARG_FEED_ID)
        }

        val fragmentComponent = DaggerFragmentComponent
            .builder()
            .appComponent(InitApp.get(context!!).component())
            .activityModule(ActivityModule(SchedulerProvider()))
            .build()
        fragmentComponent.inject(this)
    }

    private fun requestFeeds() {
        var disposable = mainViewModel.provideArticles()
            ?.subscribe(
                { result ->

                },
                { e ->
                    showError(e)
                })
        compositeDisposable.add(disposable!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_article_list, container, false)
        if (view.recycler_view_main is RecyclerView) {
            with(view.recycler_view_main) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                val dataSource = DataSource()
                adapter = ArticlesAdapter(context, dataSource.getData())
                val layoutManager = LinearLayoutManager(context)
                val dividerItemDecoration = DividerItemDecoration(context, layoutManager.orientation)
                recycler_view_main.addItemDecoration(dividerItemDecoration)
                view.swipeRefreshLayout.setOnRefreshListener {
                    (adapter as ArticlesAdapter).updateData(dataSource.getUpdatedData())
                    log.info(dataSource.getUpdatedData()[0].status)
                    view.swipeRefreshLayout.isRefreshing = false
                }
            }
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: DummyItem?)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_FEED_ID = "feed_id"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(feedId: String) =
            ArticleFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_FEED_ID, feedId)
                }
            }
    }
}
