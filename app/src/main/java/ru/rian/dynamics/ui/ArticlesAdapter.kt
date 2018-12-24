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
import java.util.logging.Logger

/**
 * Created by Amanjeet Singh on 17/1/18.
 */
class ArticlesAdapter(val context: Context, private val articleList: MutableList<Article>) :
    RecyclerView.Adapter<ArticlesAdapter.MyViewHolder>() {

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder?.bindItems(articleList[position])
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val bundle = payloads[0] as Bundle
            for (key in bundle.keySet()) {
                if (key == "article") {
                    holder?.bindItems(articleList[position])
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
        fun bindItems(article: Article) {
            itemView.flash_icon.visibility = if (article.priority!! < 3) VISIBLE else GONE
            itemView.descr_icon.visibility = if (!TextUtils.isEmpty(article.body)) VISIBLE else GONE
            itemView.article_title.text = article.title
            itemView.article_pub_date.text = RiaDateUtils.formatTime(article.createdAt)
            if (article.feeds != null) {
                itemView.story_container.visibility = VISIBLE
                var feed = article.feeds!![0]
                when (feed.type) {
                    FEED_TYPE_STORY -> itemView.story_title.text = feed.title
                }
            } else {
                itemView.story_container.visibility = GONE
            }
        }
    }

    fun updateData(newList: List<Article>?) {
        val diffResult = DiffUtil.calculateDiff(
            ContactDiffUtilCallBack(newList!!, articleList)
        )
        diffResult.dispatchUpdatesTo(this)
        this.articleList.clear()
        this.articleList.addAll(newList!!)
    }
}