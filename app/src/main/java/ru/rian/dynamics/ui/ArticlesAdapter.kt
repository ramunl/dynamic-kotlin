package ru.rian.dynamics.ui

import android.content.Context
import android.os.Bundle
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item_article.view.*
import ru.rian.dynamics.R
import ru.rian.dynamics.retrofit.model.Article
import ru.rian.dynamics.utils.FEED_TYPE_STORY
import ru.rian.dynamics.utils.RiaDateUtils
import ru.rian.dynamics.utils.RiaDateUtils.areTheDatesAtTheSameDay
import java.util.logging.Logger

/**
 * Created by Amanjeet Singh on 17/1/18.
 */
class ArticlesAdapter(
    val context: Context,
    private val mListener: ArticleFragment.OnListFragmentInteractionListener?,
    private val articleList: MutableList<Article> = ArrayList()
) :
    RecyclerView.Adapter<ArticlesAdapter.MyViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Article
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(position, articleList)
        with(holder.itemView) {
            tag = articleList[position]
            setOnClickListener(mOnClickListener)
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val bundle = payloads[0] as Bundle
            for (key in bundle.keySet()) {
                if (key == "article") {
                    holder.bindItems(position, articleList)
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.list_item_article,
            parent, false
        )
        return MyViewHolder(view)
    }


    companion object {
        val log = Logger.getLogger("ArticlesAdapter")
    }

    override fun getItemCount(): Int {
        return articleList.size
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(position: Int, articleList: MutableList<Article>) {
            val article = articleList[position]
            val articlePrev = if (position > 0) articleList[position - 1] else null
            fun setStoryContainerVisibility(isVisible: Boolean) {
                itemView.story_container.visibility = if (isVisible) VISIBLE else GONE
            }
            itemView.flash_icon.visibility = if (article.priority!! < 3) VISIBLE else GONE
            itemView.descr_icon.visibility = if (!TextUtils.isEmpty(article.body)) VISIBLE else GONE
            itemView.article_title.text = article.title
            itemView.article_pub_date.text = RiaDateUtils.formatTime(article.createdAt)
            if (article.feeds != null) {
                itemView.story_container.visibility = VISIBLE
                var feed = article.feeds!![0]
                when (feed.type) {
                    FEED_TYPE_STORY -> {
                        itemView.story_title.text = feed.title
                        setStoryContainerVisibility(true)
                    }
                    else -> setStoryContainerVisibility(false)
                }
            } else {
                setStoryContainerVisibility(false)
            }

            if (!areTheDatesAtTheSameDay(articlePrev?.createdAt, article.createdAt)) {
                itemView.day_header_pub_date.text = RiaDateUtils.formatArticleListHeaderTime(article.createdAt)
                itemView.day_header_pub_date.visibility = VISIBLE
                itemView.day_header_pub_date_bottom_img.visibility = VISIBLE

            } else {
                itemView.day_header_pub_date.visibility = GONE
                itemView.day_header_pub_date_bottom_img.visibility = GONE
            }

        }
    }

    fun addData(newList: List<Article>?) {
        newList?.let {
            this.articleList.addAll(newList)
            notifyDataSetChanged()
        }
    }

    fun updateData(newList: List<Article>?) {
        newList?.let {
            var toUpdate = true
            if (articleList.size > 0) {
                var newListFirstArticle = newList[0]
                var firstArticle = articleList[0]
                if (newListFirstArticle == firstArticle) {
                    toUpdate = false
                }
            }
            if (toUpdate) {
                val joined = articleList.union(newList)
                val diffResult = DiffUtil.calculateDiff(
                    ArticleListDiffUtilCallBack(newList, articleList)
                )
                diffResult.dispatchUpdatesTo(this)
                this.articleList.clear()
                this.articleList.addAll(joined)
            }
        }
    }
}