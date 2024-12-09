package com.pht.roomfinder.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pht.roomfinder.databinding.ItemListBinding
import com.pht.roomfinder.response.ListGroupData

class ListItemAdapter(private val list: List<ListGroupData>) :
    RecyclerView.Adapter<ListItemAdapter.ViewHolder>() {

    inner class ViewHolder(val bin: ItemListBinding) : RecyclerView.ViewHolder(bin.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val bin = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(bin)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listGroupData = list[position]
        holder.bin.listGroupData = listGroupData
        holder.bin.executePendingBindings()

        val adapter = PostItemAdapter(listGroupData.listPost)
        holder.bin.recyclerViewList.layoutManager =
            LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        holder.bin.recyclerViewList.adapter = adapter
    }

    override fun getItemCount() = list.size
}