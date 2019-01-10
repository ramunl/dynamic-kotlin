package ru.rian.dynamics.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item_user_feeds.view.*
import ru.rian.dynamics.R
import ru.rian.dynamics.retrofit.model.Feed
import ru.rian.dynamics.ui.fragments.UserFeedsFragment
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

    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int, payloads: MutableList<Any>) {
        holder.bindItems(position, dataList)
        with(holder.itemView) {
            tag = dataList[position]
            setOnClickListener(mOnClickListener)
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


    fun updateData(newList: List<Feed>?) {
        newList?.let {
            this.dataList.clear()
            this.dataList.addAll(newList)
            notifyDataSetChanged()
        }
    }

    fun removeAll() {
        this.dataList.clear()
        notifyDataSetChanged()
    }
}