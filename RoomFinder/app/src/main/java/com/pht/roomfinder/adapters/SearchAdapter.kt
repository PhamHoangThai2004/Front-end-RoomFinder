package com.pht.roomfinder.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.pht.roomfinder.databinding.ItemSearchBinding
import com.pht.roomfinder.model.Post
import com.pht.roomfinder.utils.Const
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SearchAdapter(private val list: List<Post>, private val onClick: (Int) -> Unit) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    inner class ViewHolder(val bin: ItemSearchBinding) : RecyclerView.ViewHolder(bin.root) {
        private val viewHolderScope = CoroutineScope(Dispatchers.Main + Job())

        init {
            bin.root.setOnClickListener {
                val post = list[bindingAdapterPosition]
                if (post.postID == null) {
                    Toast.makeText(bin.root.context, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                onClick(post.postID)
            }
        }

        fun clear() {
            viewHolderScope.cancel()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val bin = ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(bin)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = list[position]
        holder.bin.post = post
        holder.bin.textViewCreatedAt.text = Const.changeTime(post.createdAt.toString())
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

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.clear()
    }

    override fun getItemCount() = list.size
}