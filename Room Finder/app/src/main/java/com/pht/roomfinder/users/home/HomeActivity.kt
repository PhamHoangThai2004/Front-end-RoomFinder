package com.pht.roomfinder.users.home

import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pht.roomfinder.R

class HomeActivity : AppCompatActivity() {

    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        textView = findViewById(R.id.textView)

        val intent = intent
        val accountID = intent.getIntExtra("accountID", 0)
        textView.text = accountID.toString()

        val loadingDialog: Dialog = Dialog(this).apply {
            setContentView(R.layout.dialog_loading)
        }

        textView.setOnClickListener {
            loadingDialog.show()
        }
    }
}