package com.pht.roomfinder.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pht.roomfinder.databinding.ItemPostBinding
import com.pht.roomfinder.model.Post

class PostItemAdapter (private val list: List<Post>) : RecyclerView.Adapter<PostItemAdapter.ViewHolder>() {

    inner class ViewHolder(val bin: ItemPostBinding) : RecyclerView.ViewHolder(bin.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val bin = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(bin)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = list[position]
        holder.bin.post = post
        holder.bin.executePendingBindings()
    }

    override fun getItemCount() = list.size


}