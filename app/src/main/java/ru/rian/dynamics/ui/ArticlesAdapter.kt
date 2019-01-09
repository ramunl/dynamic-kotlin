package ru.rian.dynamics.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.list_item_article.view.*
import ru.rian.dynamics.R
import ru.rian.dynamics.retrofit.model.Article
import ru.rian.dynamics.utils.FEED_TYPE_STORY
import ru.rian.dynamics.utils.RiaDateUtils
import ru.rian.dynamics.utils.RiaDateUtils.areTheDatesAtTheSameDay
import java.util.*
import java.util.logging.Logger

/**
 * Created by Amanjeet Singh on 17/1/18.
 */
class ArticlesAdapter(
    val context: Context,
    private val mListener: ArticleFragment.OnListFragmentInteractionListener?,
    var dataList: ArrayList<Article> = ArrayList()
) :
    RecyclerView.Adapter<ArticlesAdapter.ArticleViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Article
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bindItems(position, dataList)
        with(holder.itemView) {
            tag = dataList[position]
            setOnClickListener(mOnClickListener)
        }
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val bundle = payloads[0] as Bundle
            for (key in bundle.keySet()) {
                if (key == "article") {
                    holder.bindItems(position, dataList)
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ArticleViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.list_item_article,
            parent, false
        )
        return ArticleViewHolder(view)
    }


    companion object {
        val log = Logger.getLogger("ArticlesAdapter")
    }

    override fun getItemCount(): Int {
        return dataList.size
    }


    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(position: Int, articleList: MutableList<Article>) {
            val article = articleList[position]
            val articlePrev = if (position > 0) articleList[position - 1] else null
            fun setStoryContainerVisibility(isVisible: Boolean) {
                itemView.story_container.visibility = if (isVisible) VISIBLE else GONE
            }
            itemView.flash_icon.visibility = if (article.priority!! < 3) VISIBLE else GONE
            itemView.descr_icon.visibility = if (!TextUtils.isEmpty(article.body)) VISIBLE else GONE
            article.title?.let {
                if (!insertTitleKeyword(itemView.context, it, itemView.article_title)) {
                    itemView.article_title.text = article.title
                }
            }
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

        private fun insertTitleKeyword(context: Context, aTitle: String, titleTextView: TextView): Boolean {
            val nonBreakingSpace = "\u00a0"
            if (aTitle.contains(nonBreakingSpace)) {
                class SpannableIndices(var mStartIndex: Int, var mEndIndex: Int, var inserted: Boolean)

                val spannableIndices = ArrayList<SpannableIndices>()
                val stringTokenizer = StringTokenizer(aTitle, nonBreakingSpace)
                val pos = 0

                while (stringTokenizer.hasMoreElements()) {
                    val str = stringTokenizer.nextElement().toString()
                    val start = aTitle.indexOf(str, pos)
                    val end = start + str.length
                    if (start >= 0 && end > 0) {
                        val substrInd = aTitle.indexOf(str)
                        var inserted = false
                        if (substrInd > 0) {
                            if (aTitle.substring(substrInd - 1, substrInd) == nonBreakingSpace) {
                                var isPrevItemInserted = false
                                if (spannableIndices.size > 0) {
                                    isPrevItemInserted = spannableIndices[spannableIndices.size - 1].inserted
                                }
                                if (!isPrevItemInserted && str.length > 1) {
                                    inserted = true
                                }
                            }
                        }
                        spannableIndices.add(SpannableIndices(start, end, inserted))
                    }
                }
                val spannableStringBuilder = SpannableStringBuilder()
                if (spannableIndices.size > 0) {
                    titleTextView.text = ""
                    for (spannableIndex in spannableIndices) {
                        if (spannableIndex.inserted) {
                            val text =
                                SpannableString(aTitle.substring(spannableIndex.mStartIndex, spannableIndex.mEndIndex))
                            val lastInd = spannableStringBuilder.length
                            spannableStringBuilder.append(text)
                            val span = ForegroundColorSpan(ContextCompat.getColor(context, R.color.inserted_text))
                            spannableStringBuilder.setSpan(
                                span,
                                lastInd,
                                lastInd + text.length,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        } else {
                            spannableStringBuilder.append(
                                aTitle.substring(
                                    spannableIndex.mStartIndex,
                                    spannableIndex.mEndIndex
                                )
                            )
                        }
                    }
                    titleTextView.text = spannableStringBuilder
                }
                return true
            }
            return false
        }
    }

    fun addData(newList: List<Article>?) {
        newList?.let {
            this.dataList.addAll(newList)
            notifyDataSetChanged()
        }
    }

    fun updateData(newList: List<Article>?) {
        newList?.let {
            var toUpdate = true
            if (dataList.size > 0) {
                var newListFirstArticle = newList[0]
                var firstArticle = dataList[0]
                if (newListFirstArticle == firstArticle) {
                    toUpdate = false
                }
            }
            if (toUpdate) {
                if (!dataList.isEmpty()) {
                    val joined = dataList.union(newList)
                    val diffResult = DiffUtil.calculateDiff(
                        ArticleListDiffUtilCallBack(newList, dataList)
                    )
                    diffResult.dispatchUpdatesTo(this)
                    this.dataList.clear()
                    this.dataList.addAll(joined)
                } else {
                    this.dataList.addAll(newList)
                    notifyDataSetChanged()
                }
            }
        }
    }

    fun removeAll() {
        this.dataList.clear()
        notifyDataSetChanged()
    }
}