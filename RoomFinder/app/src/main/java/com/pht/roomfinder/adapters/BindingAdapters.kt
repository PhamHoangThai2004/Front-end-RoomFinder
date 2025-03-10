package com.pht.roomfinder.adapters

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.pht.roomfinder.R

object BindingAdapters {

    @SuppressLint("ClickableViewAccessibility")
    @JvmStatic
    @BindingAdapter("onDrawableEndClick")
    fun setDrawableEndClickListener(editText: EditText, action: () -> Unit) {
        editText.setOnTouchListener { _, event ->
            val drawableEnd = 2 // Vị trí của drawableEnd trong EditText (có thể là 2)
            if (event.action == MotionEvent.ACTION_UP) {
                // Kiểm tra xem người dùng có nhấn vào biểu tượng ở cuối EditText không
                val drawableRight = editText.compoundDrawables[drawableEnd]
                if (drawableRight != null && event.rawX >= (editText.right - drawableRight.bounds.width())) {
                    // Thực hiện hành động tìm kiếm từ ViewModel
                    action()
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    @SuppressLint("SetTextI18n")
    @JvmStatic
    @BindingAdapter("formatText", "formatValue", "formatType", requireAll = false)
    fun setText(textView: TextView, text: Int?, value: Any?, type: Int?) {
        val context = textView.context
        val formatType = type ?: 1
        when (formatType) {
            1 -> textView.text = "\u2022" + " " + context.getString(text!!) + ": " + value
            2 -> textView.text = context.getString(text!!) + ": " + value
            3 -> textView.text = "\u2022" + " " + context.getString(R.string.post_id) + ": #" + value
            4 -> textView.text = context.getString(text!!) + ": " + value + " " + context.getString(R.string.million_month)
            5 -> textView.text = context.getString(text!!) + ": " + value + " m" + "\u00B2"
            6 -> textView.text = if (value == null) {
                    context.getString(R.string.no_found)
                } else {
                    context.getString(R.string.not_result) + " " + "\"" + value + "\""
                }
            else -> textView.text = "\u2022" + " " + value + " " + context.getString(R.string.posts)
        }
    }
}
