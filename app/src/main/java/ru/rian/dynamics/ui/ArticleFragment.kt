package ru.rian.dynamics.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_article_list.view.*
import ru.rian.dynamics.InitApp
import ru.rian.dynamics.R
import ru.rian.dynamics.SchedulerProvider
import ru.rian.dynamics.di.component.DaggerFragmentComponent
import ru.rian.dynamics.di.model.ActivityModule
import ru.rian.dynamics.di.model.MainViewModel
import ru.rian.dynamics.retrofit.model.Article
import ru.rian.dynamics.utils.ARTICLE_LIST_LIMIT
import javax.inject.Inject

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [ArticleFragment.OnListFragmentInteractionListener] interface.
 */
class ArticleFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var compositeDisposable: CompositeDisposable
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
        compositeDisposable = CompositeDisposable()
        val fragmentComponent = DaggerFragmentComponent
            .builder()
            .appComponent(InitApp.get(context!!).component())
            .activityModule(ActivityModule(SchedulerProvider()))
            .build()
        fragmentComponent.inject(this)
    }

    private fun requestMoreArticles() {
        var disposable = mainViewModel.provideArticles(feedId!!, articlesAdapter.itemCount)
            ?.subscribe(
                { result ->
                    updateList(result!!.articles, false)
                    recyclerView.scrollToPosition(ARTICLE_LIST_LIMIT + articlesAdapter.itemCount)
                },
                { e ->
                    showError((context as SnackContainerProvider), e, ::requestArticles)
                })
        compositeDisposable.add(disposable!!)
    }

    private fun requestArticles() {
        var disposable = mainViewModel.provideArticles(feedId!!)
            ?.subscribe(
                { result ->
                    updateList(result!!.articles)
                },
                { e ->
                    showError((context as SnackContainerProvider), e, ::requestArticles)
                })
        compositeDisposable.add(disposable!!)
    }

    private fun updateList(articles: List<Article>?, toClean: Boolean = true) {
        articlesAdapter.updateData(articles, toClean)
    }

    private lateinit var articlesAdapter: ArticlesAdapter


    private fun RecyclerView.onScrollToEnd(linearLayoutManager: LinearLayoutManager, onScrollNearEnd: () -> Unit) =
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (linearLayoutManager.childCount + linearLayoutManager.findFirstVisibleItemPosition()
                    >= linearLayoutManager.itemCount - 5
                ) {  //if near fifth item from end
                    onScrollNearEnd()
                }
            }
        })


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_article_list, container, false)
        recyclerView = view.recyclerView
        if (view.recyclerView is RecyclerView) {
            articlesAdapter = ArticlesAdapter(view.context, listener)
            view.recyclerView.adapter = articlesAdapter
            (view.recyclerView.adapter as ArticlesAdapter)
            with(view.recyclerView) {

                layoutManager = LinearLayoutManager(context)

                val dividerItemDecoration =
                    DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation)

                dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.divider))

                recyclerView.addItemDecoration(dividerItemDecoration)

                view.swipeRefreshLayout.setOnRefreshListener {
                    view.swipeRefreshLayout.isRefreshing = false
                }
                view.recyclerView.onScrollToEnd(layoutManager as LinearLayoutManager, ::requestMoreArticles)
            }
        }
        requestArticles()
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
        fun onListFragmentInteraction(item: Article?)
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
