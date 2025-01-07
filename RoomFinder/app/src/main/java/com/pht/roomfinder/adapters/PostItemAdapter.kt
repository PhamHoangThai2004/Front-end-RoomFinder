package com.pht.roomfinder.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.pht.roomfinder.databinding.ItemPostBinding
import com.pht.roomfinder.model.Post
import com.pht.roomfinder.utils.Const
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class PostItemAdapter(private val list: List<Post>, private val onItemClick: (Int) -> Unit) :
    RecyclerView.Adapter<PostItemAdapter.ViewHolder>() {

    inner class ViewHolder(val bin: ItemPostBinding) : RecyclerView.ViewHolder(bin.root) {
        private val viewHolderScope = CoroutineScope(Dispatchers.Main + Job())

        fun clear() {
            viewHolderScope.cancel()
        }

        init {
            bin.root.setOnClickListener {
                val post = list[adapterPosition]
                if (post.postID == null) {
                    Toast.makeText(bin.root.context, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                onItemClick(post.postID)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val bin = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(bin)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = list[position]
        holder.bin.post = post

        if (post.images.isNullOrEmpty().not()) {
            CoroutineScope(Dispatchers.Main).launch {
                Const.loadImage(
                    holder.bin.root.context,
                    post.images?.get(0)?.imagePath!!,
                    holder.bin.viewRoom
                )
            }
        }
        holder.bin.executePendingBindings()
    }

    override fun getItemCount() = list.size

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.clear()
    }

}