package com.cocoba.tulisajadulu

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_post.view.*

/**
 * Created by Chandra on 07/03/19.
 * Need some help?
 * Contact me at y.pristyan.chandra@gmail.com
 */
class PostAdapter(private val callback: Callback) : RecyclerView.Adapter<PostAdapter.Holder>() {

    private var items = ArrayList<PostModel>()

    fun update(item: PostModel) {
        items.filter { it.id == item.id }
            .map { items.indexOf(it) }
            .find {
                items[it] = item
                notifyItemChanged(it)
                true
            }
    }

    fun delete(item: PostModel) {
        items.filter { it.id == item.id }
            .map { items.indexOf(it) }
            .find {
                items.removeAt(it)
                notifyItemRemoved(it)
                true
            }
    }

    fun addItem(item: PostModel) {
        items.add(item)
    }

    override fun onCreateViewHolder(container: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(container.context)
        return Holder(inflater.inflate(R.layout.item_post, container, false))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: Holder, i: Int) = holder.bind(items[i])

    inner class Holder(superView: View) : RecyclerView.ViewHolder(superView) {
        fun bind(item: PostModel) = with(itemView) {
            post_title?.text = item.title
            post_content?.text = item.content
            itemView.setOnClickListener { callback.onPostClick(item) }
        }
    }

    interface Callback {
        fun onPostClick(item: PostModel)
    }
}