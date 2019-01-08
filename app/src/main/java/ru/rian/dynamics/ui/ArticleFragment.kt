package ru.rian.dynamics.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.text.TextUtils
import android.view.*
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_article_list.view.*
import ru.rian.dynamics.InitApp
import ru.rian.dynamics.R
import ru.rian.dynamics.SchedulerProvider
import ru.rian.dynamics.di.component.DaggerFragmentComponent
import ru.rian.dynamics.di.model.ActivityModule
import ru.rian.dynamics.di.model.MainViewModel
import ru.rian.dynamics.retrofit.model.Article
import ru.rian.dynamics.retrofit.model.Source
import java.util.*
import javax.inject.Inject

/**
 * A fragment representing a list of article items.
 * Activities containing this fragment MUST implement the
 * [ArticleFragment.OnListFragmentInteractionListener] interface.
 */
class ArticleFragment : Fragment() {
    private lateinit var searchView: SearchView

    private lateinit var swipeRefreshArticleList: SwipeRefreshLayout

    private lateinit var recyclerView: RecyclerView

    private lateinit var compositeDisposable: CompositeDisposable

    var query: String? = null

    private lateinit var feedSource: Source
    private lateinit var feedId: String
    @Inject
    lateinit var mainViewModel: MainViewModel

    private var listener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
        arguments?.let {
            feedId = it.getString(ARG_FEED_ID)!!
            feedSource = it.getSerializable(ARG_FEED_SOURCE) as Source
        }
        compositeDisposable = CompositeDisposable()
        val fragmentComponent = DaggerFragmentComponent
            .builder()
            .appComponent(InitApp.get(context!!).component())
            .activityModule(ActivityModule(SchedulerProvider()))
            .build()
        fragmentComponent.inject(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("query", searchView.query.toString())
        outState.putParcelableArrayList("articleList", articlesAdapter.articleList)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        searchView = menu.findItem(R.id.search_news).actionView as SearchView
        if (!TextUtils.isEmpty(query)) {
            searchView.setQuery(query, false)
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                articlesAdapter.removeAll()
                requestArticles(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                this@ArticleFragment.query = newText
                return true
            }
        })

        searchView.setOnCloseListener {
            onSearchClose()
        }
    }

    private fun onSearchClose(): Boolean {
        articlesAdapter.removeAll()
        requestArticles()
        return false
    }

    private fun requestMoreArticles(query: String? = null) {
        var disposable = mainViewModel.provideArticles(feedSource, feedId!!, query = query, offset = articlesAdapter.itemCount)
            ?.subscribe(
                { result ->
                    updateList(result!!.articles, false)
                },
                { e ->
                    snackContainerProvider().showError(
                        e,
                        SnackContainerProvider.ActionToInvoke(::requestMoreArticles, query)
                    )
                })
        disposable?.let { compositeDisposable.add(it) }
    }

    private fun snackContainerProvider(): SnackContainerProvider {
        return context as SnackContainerProvider
    }

    private fun requestArticles(query: String? = null) {
        var disposable =
            mainViewModel.provideArticles(feedSource, feedId!!, query = query, showProgress = !isRefreshing())
                ?.subscribe(
                    { result ->
                        updateList(result?.articles)
                        setRefreshing(false)
                    },
                    { e ->
                        setRefreshing(false)
                        snackContainerProvider().showError(
                            e,
                            SnackContainerProvider.ActionToInvoke(::requestArticles, query)
                        )
                    })
        disposable?.let { compositeDisposable.add(it) }
    }

    private fun setRefreshing(isRefreshing: Boolean) {
        swipeRefreshArticleList.isRefreshing = isRefreshing
    }

    private fun isRefreshing() = swipeRefreshArticleList.isRefreshing

    private fun updateList(articles: List<Article>?, toClean: Boolean = true) {
        if (toClean)
            articlesAdapter.updateData(articles)
        else
            articlesAdapter.addData(articles)
    }

    private lateinit var articlesAdapter: ArticlesAdapter


    private fun RecyclerView.onScrollToEnd(
        linearLayoutManager: LinearLayoutManager,
        onScrollNearEnd: (String?) -> Unit
    ) =
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (linearLayoutManager.childCount + linearLayoutManager.findFirstVisibleItemPosition()
                    >= linearLayoutManager.itemCount - 1
                ) {  //if near fifth item from end
                    onScrollNearEnd(searchView.query.toString())
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

                swipeRefreshArticleList = view.swipeRefreshLayout

                view.swipeRefreshLayout.setOnRefreshListener {
                    requestArticles()
                }
                view.recyclerView.onScrollToEnd(layoutManager as LinearLayoutManager, ::requestMoreArticles)
            }
        }
        var articleListSaved: ArrayList<Article>? = null
        if (savedInstanceState != null) {
            query = savedInstanceState.getString("query")
            articleListSaved = savedInstanceState.getParcelableArrayList<Article>("articleList")
        }
        if (articleListSaved != null && articleListSaved.size > 0) {
            articlesAdapter.articleList = articleListSaved
        } else {
            requestArticles()
        }
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
        const val ARG_FEED_ID = "feed_id"
        const val ARG_FEED_SOURCE = "feed_url"
        @JvmStatic
        fun newInstance(feedId: String, feedSource: Source) =
            ArticleFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_FEED_ID, feedId)
                    putSerializable(ARG_FEED_SOURCE, feedSource)
                }
            }
    }
}
