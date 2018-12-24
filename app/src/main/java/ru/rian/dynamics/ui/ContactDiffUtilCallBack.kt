package ru.rian.dynamics.ui

import android.os.Bundle
import android.support.v7.util.DiffUtil
import ru.rian.dynamics.retrofit.model.Article
import java.util.logging.Logger

/**
 * Created by Amanjeet Singh on 17/1/18.
 */
class ContactDiffUtilCallBack(
    private val newList: List<Article>,
    private val oldList: List<Article>
) : DiffUtil.Callback() {


    companion object {
        val log = Logger.getLogger("ContactDiffUtilCallBack")
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return true
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val result = newList[newItemPosition].compareTo(oldList[oldItemPosition])
        if (result == 0) {
            return true
        }
        return false
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val newArticle = newList[newItemPosition]
        val oldArticle = oldList[oldItemPosition]
        val bundle = Bundle()
        if (newArticle.id != oldArticle.id) {
            bundle.putParcelable("article", newArticle)
        }
        return bundle
    }
}