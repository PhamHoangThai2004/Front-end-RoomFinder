package com.pht.roomfinder.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.pht.roomfinder.R
import com.pht.roomfinder.databinding.ItemNotificationBinding
import com.pht.roomfinder.model.Notification
import com.pht.roomfinder.utils.Const
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

class NotificationAdapter(
    private var list: List<Notification>,
    private val onClick: (Int) -> Unit,
    private val onDelete: (Int) -> Unit
) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    inner class ViewHolder(val bin: ItemNotificationBinding) : RecyclerView.ViewHolder(bin.root) {
        private val viewHolderScope = CoroutineScope(Dispatchers.Main + Job())

        init {
            bin.root.setOnClickListener {
                val notification = list[bindingAdapterPosition]
                if (notification.postId <= 0) {
                    Toast.makeText(
                        bin.root.context,
                        bin.root.context.getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                onClick(notification.postId)
                onDelete(notification.id)
            }
        }

        fun clear() {
            viewHolderScope.cancel()
        }
    }

    override fun onViewRecycled(holder: NotificationAdapter.ViewHolder) {
        super.onViewRecycled(holder)
        holder.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val bin =
            ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(bin)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = list[position]
        holder.bin.notification = notification
        holder.bin.textViewTime.text = Const.changeTime(notification.time)
        holder.bin.executePendingBindings()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<Notification>) {
        list = newList
        notifyDataSetChanged()
    }
}