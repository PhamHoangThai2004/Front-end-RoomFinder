package com.pht.roomfinder.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pht.roomfinder.R

class ChoiceImgAdapter(private val imagesList: List<Uri>) :
    RecyclerView.Adapter<ChoiceImgAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.image_View)
        fun bind(imageUri: Uri) {
            Glide.with(itemView.context)
                .load(imageUri)
                .into(imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_images, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageUri = imagesList[position]
        holder.bind(imageUri)
    }

    override fun getItemCount() = imagesList.size
}