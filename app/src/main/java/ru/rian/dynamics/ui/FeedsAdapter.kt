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
import kotlinx.android.synthetic.main.list_item_user_feeds.view.*
import ru.rian.dynamics.R
import ru.rian.dynamics.retrofit.model.Feed
import ru.rian.dynamics.utils.FEED_TYPE_STORY
import ru.rian.dynamics.utils.RiaDateUtils
import ru.rian.dynamics.utils.RiaDateUtils.areTheDatesAtTheSameDay
import java.util.*
import java.util.logging.Logger

/**
 * Created by Amanjeet Singh on 17/1/18.
 */
class FeedsAdapter(
    val context: Context,
    private val mListener: UserFeedsFragment.OnListFragmentInteractionListener?,
    var dataList: ArrayList<Feed> = ArrayList()
) :
    RecyclerView.Adapter<FeedsAdapter.FeedViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Feed
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.bindItems(position, dataList)
        with(holder.itemView) {
            tag = dataList[position]
            setOnClickListener(mOnClickListener)
        }
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val bundle = payloads[0] as Bundle
            for (key in bundle.keySet()) {
                if (key == "Feed") {
                    holder.bindItems(position, dataList)
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): FeedViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.list_item_user_feeds,
            parent, false
        )
        return FeedViewHolder(view)
    }


    companion object {
        val log = Logger.getLogger("FeedsAdapter")
    }

    override fun getItemCount(): Int {
        return dataList.size
    }


    class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(position: Int, feedList: MutableList<Feed>) {
            val feed = feedList[position]
            itemView.item_user_feeds_text_view.text = feed.title
        }
        
    }

    fun addData(newList: List<Feed>?) {
        newList?.let {
            this.dataList.addAll(newList)
            notifyDataSetChanged()
        }
    }

    fun updateData(newList: List<Feed>?) {
        newList?.let {
            var toUpdate = true
            if (dataList.size > 0) {
                var newListFirstFeed = newList[0]
                var firstFeed = dataList[0]
                if (newListFirstFeed == firstFeed) {
                    toUpdate = false
                }
            }
            if (toUpdate) {
                if (!dataList.isEmpty()) {
                    val joined = dataList.union(newList)
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