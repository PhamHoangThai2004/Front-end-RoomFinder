package com.pht.roomfinder.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pht.roomfinder.databinding.ImageItemBinding
import com.pht.roomfinder.model.Images
import com.pht.roomfinder.utils.Const
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class ImageAdapter(private val images: List<Images>) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    inner class ViewHolder(val bin: ImageItemBinding) : RecyclerView.ViewHolder(bin.root) {
        private val viewHolderScope = CoroutineScope(Dispatchers.Main + Job())

        fun clear() {
            viewHolderScope.cancel()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val bin = ImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(bin)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = images[position]
        CoroutineScope(Dispatchers.Main).launch {
            Const.loadImage(holder.bin.root.context, image.imagePath!!, holder.bin.imageView)
        }
        holder.bin.executePendingBindings()
    }

    override fun getItemCount() = images.size

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.clear()
    }
}