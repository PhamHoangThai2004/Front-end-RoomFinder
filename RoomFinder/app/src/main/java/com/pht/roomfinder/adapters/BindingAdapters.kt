package com.pht.roomfinder.adapters

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.widget.EditText
import androidx.databinding.BindingAdapter

object BindingAdapters {

    @SuppressLint("ClickableViewAccessibility")
    @JvmStatic
    @BindingAdapter("onDrawableEndClick")
    fun setDrawableEndClickListener(editText: EditText, action: () -> Unit) {
        editText.setOnTouchListener { v, event ->
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
}
