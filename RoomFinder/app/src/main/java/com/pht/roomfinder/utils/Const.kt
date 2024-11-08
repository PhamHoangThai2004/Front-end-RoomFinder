package com.pht.roomfinder.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.Window
import android.view.WindowManager

class Const {
    companion object {
        const val HTTP_API = "http://192.168.102.4/roomfinder/"

        const val TOKEN = "token"

        fun setDialog(layout: Int, context: Context) : Dialog {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(layout)
            dialog.setCancelable(false)

            val window: Window? = dialog.window
            window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val windowAttribute = window?.attributes
            windowAttribute?.gravity = Gravity.CENTER
            window?.attributes = windowAttribute

            return dialog
        }
    }
}