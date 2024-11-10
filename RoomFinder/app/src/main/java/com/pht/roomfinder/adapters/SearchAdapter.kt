package com.pht.roomfinder.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pht.roomfinder.databinding.ItemSearchBinding
import com.pht.roomfinder.model.Post

class SearchAdapter(private val list: List<Post>) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    inner class ViewHolder(val bin: ItemSearchBinding) : RecyclerView.ViewHolder(bin.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val bin = ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(bin)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = list[position]
        holder.bin.post = post
        holder.bin.executePendingBindings()
    }

    override fun getItemCount() = list.size
}