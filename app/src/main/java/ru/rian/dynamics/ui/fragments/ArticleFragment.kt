package ru.rian.dynamics.ui.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.text.TextUtils
import android.view.*
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_content_common.*
import kotlinx.android.synthetic.main.fragment_article_list.view.*
import ru.rian.dynamics.InitApp
import ru.rian.dynamics.R
import ru.rian.dynamics.SchedulerProvider
import ru.rian.dynamics.di.component.DaggerFragmentComponent
import ru.rian.dynamics.di.model.ActivityModule
import ru.rian.dynamics.di.model.MainViewModel
import ru.rian.dynamics.retrofit.model.Article
import ru.rian.dynamics.retrofit.model.Feed
import ru.rian.dynamics.retrofit.model.Source
import ru.rian.dynamics.ui.MainActivity
import ru.rian.dynamics.ui.fragments.adapters.ArticlesAdapter
import ru.rian.dynamics.ui.fragments.listeners.OnArticlesListInteractionListener
import ru.rian.dynamics.ui.helpers.LoadingObserver
import ru.rian.dynamics.ui.helpers.SnackContainerProvider
import ru.rian.dynamics.utils.TYPE_FEED_SUBSCRIPTION_ALL
import ru.rian.dynamics.utils.TYPE_FEED_SUBSCRIPTION_BREAKING
import java.util.*
import javax.inject.Inject

/**
 * A fragment representing a list of article items.
 * Activities containing this fragment MUST implement the
 */


class ArticleFragment : BaseFragment(), OnArticlesListInteractionListener {
    private var searchView: SearchView? = null

    private lateinit var swipeRefreshArticleList: SwipeRefreshLayout

    private lateinit var recyclerView: RecyclerView

    private lateinit var compositeDisposable: CompositeDisposable

    var query: String? = null

    @Inject
    lateinit var mainViewModel: MainViewModel

    fun feed() = arguments!!.getSerializable(ARG_FEED) as Feed
    fun feedSource() = arguments!!.getSerializable(ARG_FEED_SOURCE) as Source

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
        compositeDisposable = CompositeDisposable()
        val fragmentComponent = DaggerFragmentComponent
            .builder()
            .appComponent(InitApp.get(context!!).component())
            .activityModule(ActivityModule(context as FragmentActivity, SchedulerProvider()))
            .build()
        fragmentComponent.inject(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        searchView?.let { outState.putString("query", it.query.toString()) }
        outState.putParcelableArrayList("dataList", articlesAdapter.dataList)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        var menuItem = menu.findItem(R.id.search_news)
        if (menuItem != null) {
            searchView = menuItem.actionView as SearchView
            searchView?.let {
                if (!TextUtils.isEmpty(query)) {
                    it.setQuery(query, false)
                    it.isIconified = false
                }
                it.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
                it.setOnCloseListener {
                    onSearchClose()
                }
            }
        }
        setupToolBarTitle(feed().title!!)
    }

    private fun onSearchClose(): Boolean {
        articlesAdapter.removeAll()
        requestArticles()
        return false
    }

    private fun requestMoreArticles(query: String? = null) {
        var disposable =
            mainViewModel.provideArticles(feedSource(), feed().sid, query = query, offset = articlesAdapter.itemCount)
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
            mainViewModel.provideArticles(feedSource(), feed().sid, query = query, showProgress = !isRefreshing())
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
                    onScrollNearEnd(searchView?.query.toString())
                }
            }
        })


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_article_list, container, false)

        recyclerView = view.recyclerView

        if (view.recyclerView is RecyclerView) {

            articlesAdapter = ArticlesAdapter(view.context, this)

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
            articleListSaved = savedInstanceState.getParcelableArrayList<Article>("dataList")
        }
        if (articleListSaved != null && articleListSaved.size > 0) {
            articlesAdapter.dataList = articleListSaved
        } else {
            requestArticles()
        }
        setFloatFlashFloatButton()
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        LoadingObserver.addLoadingObserver(::onLoadingStateChanged)
    }

    override fun onDetach() {
        super.onDetach()
        LoadingObserver.removeLoadingObserver(::onLoadingStateChanged)
    }


    override fun onArticlesListInteraction(item: Article?) {

    }

    private fun getNotificationRes(): Int {
        var res = R.drawable.ic_notifications_none_gray
        var feed = feed()
        if (feed.notification != null) {
            if (TYPE_FEED_SUBSCRIPTION_BREAKING == feed.notification) {
                res = R.drawable.ic_notifications_flash_gray
            } else if (TYPE_FEED_SUBSCRIPTION_ALL == feed.notification) {
                res = R.drawable.ic_notifications_all_gray
            }
        }
        return res
    }

    fun setFloatFlashFloatButton() {
        if (isAdded) {
            var res = getNotificationRes()
            (context as AppCompatActivity).buttonFloatDynamic.setImageDrawable(
                ContextCompat.getDrawable(
                    context!!,
                    res
                )
            )
        }
        /*buttonFloatDynamic.setOnClickListener { v ->
            //    BottomSheetChangePushFeedsDialogFragment.onClickProcess(getFragmentFeedSelected(), null);
            val bottomSheetDialogFragment = BottomSheetChangePushFeedsDialogFragment()
            val bundle = Bundle()
            bundle.putParcelable("feed", getFragmentFeedSelected())
            bottomSheetDialogFragment.setArguments(bundle)
            bottomSheetDialogFragment.show(
                (v.context as AppCompatActivity).supportFragmentManager,
                bottomSheetDialogFragment.getTag()
            )
        }*/
    }

    companion object {
        const val ARG_FEED = "feed_id"
        const val ARG_FEED_SOURCE = "feed_url"
        @JvmStatic
        fun newInstance(feed: Feed, feedSource: Source) =
            ArticleFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_FEED, feed)
                    putSerializable(ARG_FEED_SOURCE, feedSource)
                }
            }

        fun newInstance(bundle: Bundle) =
            ArticleFragment().apply {
                arguments = bundle
            }
    }

    private fun onLoadingStateChanged(isLoading: Boolean) {
        MainActivity.kDebug("onLoadingStateChanged $isLoading")
        if (isAdded)
            (context as Activity).progressBarMain?.let { it.visibility = if (isLoading) View.VISIBLE else View.GONE }
    }
}
