package ru.rian.dynamics.ui

import android.content.Context
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.developers.diffutil.Person
import kotlinx.android.synthetic.main.item_view.view.*
import ru.rian.dynamics.R
import java.util.logging.Logger

/**
 * Created by Amanjeet Singh on 17/1/18.
 */
class ArticlesAdapter(val context: Context,
                      val personList: MutableList<Person>) :
        RecyclerView.Adapter<ArticlesAdapter.MyViewHolder>() {

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder?.bindItems(personList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.item_view,
            parent, false)
        var holder =  MyViewHolder(view)

        if (personList?.isEmpty()!!) {
            super.onBindViewHolder(holder, position, personList as List<Any>)
        } else {
            val bundle = personList[0]
            holder?.itemView?.name_text_view?.text = personList[position].name
            holder?.itemView?.status_text_view?.text = personList[position].status
        }

        return holder
    }


    companion object {
        val log = Logger.getLogger("ArticlesAdapter")
    }

    override fun getItemCount(): Int {
        return personList.size
    }



    /*override fun onBindViewHolder(holder: MyViewHolder?, position: Int) {
        holder?.bindItems(personList[position])
    }

    override fun onBindViewHolder(holder: MyViewHolder?, position: Int, payloads: MutableList<Any>?) {
        if (payloads?.isEmpty()!!) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val bundle = payloads[0] as Bundle
            for (key in bundle.keySet()) {
                if (key == "name") {
                    holder?.itemView?.name_text_view?.text = personList[position].name
                }
                if (key == "status") {
                    log.info("Changes are ready to show")
                    holder?.itemView?.status_text_view?.text = personList[position].status
                }
            }

        }
    }*/

    /*override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.item_view,
                parent, false)
        return MyViewHolder(view)
    }*/

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(person: Person) {
            itemView.name_text_view.text = person.name
            itemView.status_text_view.text = person.status
        }
    }

    fun updateData(newList: List<Person>) {
        val diffResult = DiffUtil.calculateDiff(
            ContactDiffUtilCallBack(newList,
                personList)
        )
        diffResult.dispatchUpdatesTo(this)
        this.personList.clear()
        this.personList.addAll(newList)
    }
}