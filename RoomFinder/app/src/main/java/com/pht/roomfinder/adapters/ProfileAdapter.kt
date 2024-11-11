package com.pht.roomfinder.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pht.roomfinder.R
import com.pht.roomfinder.user.profile.FunctionName

class ProfileAdapter (val list: List<FunctionName>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_SWITCH = 0
        const val TYPE_IMAGE_VIEW = 1
        const val TYPE_TEXT_VIEW = 2
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    class SwitchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.text_View)
        val switch: Switch = itemView.findViewById(R.id.switch_Profile)
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.text_View)
        val imageView: ImageView = itemView.findViewById(R.id.image_View)
    }

    class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.text_View)
        val textViewLabel: TextView = itemView.findViewById(R.id.text_View_Label)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_SWITCH -> {
                val view = inflater.inflate(R.layout.item_switch, parent, false)
                SwitchViewHolder(view)
            }
            TYPE_IMAGE_VIEW -> {
                val view = inflater.inflate(R.layout.item_image_view, parent, false)
                ImageViewHolder(view)
            }
            TYPE_TEXT_VIEW -> {
                val view = inflater.inflate(R.layout.item_text_view, parent, false)
                TextViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SwitchViewHolder -> {
                val functionName = list[position]
                holder.textView.text = holder.itemView.context.getString(functionName.name)
            }
            is ImageViewHolder -> {
                val functionName = list[position]
                holder.textView.text = holder.itemView.context.getString(functionName.name)
                holder.imageView.setImageResource(functionName.icon!!)
            }
            is TextViewHolder -> {
                val functionName = list[position]
                holder.textView.text = holder.itemView.context.getString(functionName.name)
                holder.textViewLabel.text = holder.itemView.context.getString(functionName.text!!)
            }
        }
    }

    override fun getItemCount() = list.size

    override fun getItemViewType(position: Int): Int {
        return list[position].type
    }
}