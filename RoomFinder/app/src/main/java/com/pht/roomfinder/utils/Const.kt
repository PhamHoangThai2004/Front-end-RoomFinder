package com.pht.roomfinder.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.format.DateUtils
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.pht.roomfinder.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class Const {
    companion object {
        const val HTTP_API = "http://192.168.102.11/roomfinder/"
        const val TOKEN = "token"
        const val PASSWORD = "password"
        const val EMAIL = "email"
        const val SAVE_ACCOUNT = "saveAccount"

        fun formatDate(timeString: String): String {
            if (timeString != "") {
                val formatFirst = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

                val date = formatFirst.parse(timeString)!!
                return format.format(date)
            }
            return "19/12/2024"
        }

        fun formatDateTime(timeString: String): String {
            if (timeString != "") {
                val formatFirst = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val format = SimpleDateFormat("HH:mm:ss dd-MM-yyyy", Locale.getDefault())

                val date = formatFirst.parse(timeString)!!
                return format.format(date)
            }
            return "4:42:12 19/12/2024"
        }

        fun changeTime(timeString: String): String {
            if (timeString != "") {
                val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val date = format.parse(timeString)

                val currentTime = System.currentTimeMillis()
                val time = currentTime - date!!.time

                val formattedText = when {
                    time < DateUtils.MINUTE_IN_MILLIS -> "Vừa xong"
                    time < DateUtils.HOUR_IN_MILLIS -> "${time / DateUtils.MINUTE_IN_MILLIS} phút trước"
                    time < DateUtils.DAY_IN_MILLIS -> "${time / DateUtils.HOUR_IN_MILLIS} giờ trước"
                    time < DateUtils.WEEK_IN_MILLIS -> "${time / DateUtils.DAY_IN_MILLIS} ngày trước"
                    else -> "${time / DateUtils.WEEK_IN_MILLIS} tuần trước"
                }
                return formattedText
            }
            return "1 tháng trước"
        }

        fun setDialog(layout: Int, context: Context): Dialog {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(layout)
            dialog.setCancelable(false)

            val window: Window? = dialog.window
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val windowAttribute = window?.attributes
            windowAttribute?.gravity = Gravity.CENTER
            window?.attributes = windowAttribute

            return dialog
        }

        suspend fun loadImage(
            context: Context,
            imagePath: String,
            imageView: ImageView,
            type: Int = 0
        ) {
            withContext(Dispatchers.IO) {
                try {
                    val bitmap = Glide.with(context)
                        .asBitmap().load(imagePath).submit().get()
                    withContext(Dispatchers.Main) {
                        imageView.setImageBitmap(bitmap)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        if (type == 0) imageView.setImageResource(R.drawable.home_background)
                        else imageView.setImageResource(R.drawable.avatar)
                    }
                }
            }
        }
    }
}