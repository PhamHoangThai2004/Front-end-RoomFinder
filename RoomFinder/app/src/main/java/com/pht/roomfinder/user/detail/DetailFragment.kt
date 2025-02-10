package com.pht.roomfinder.user.detail

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.pht.roomfinder.R
import com.pht.roomfinder.adapters.ImageAdapter
import com.pht.roomfinder.databinding.FragmentDetailBinding
import com.pht.roomfinder.viewmodel.DetailViewModel

class DetailFragment : Fragment() {
    private lateinit var bin: FragmentDetailBinding
    private lateinit var detailViewModel: DetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        detailViewModel = ViewModelProvider(requireActivity())[DetailViewModel::class.java]
        bin = FragmentDetailBinding.inflate(inflater, container, false)
        bin.detailViewModel = detailViewModel
        bin.lifecycleOwner = viewLifecycleOwner

        detailViewModel.postDetail.observe(viewLifecycleOwner) {
            setImages()
            detailViewModel.setValue()
        }

        detailViewModel.isOpenDialog.observe(viewLifecycleOwner) {
            if (it) openDialog()
        }

        changeImages()

        changePager()

        bin.invalidateAll()

        return bin.root
    }

    private fun setImages() {
        if (detailViewModel.postDetail.value?.images.isNullOrEmpty().not()) {
            val imageAdapter = ImageAdapter(detailViewModel.postDetail.value?.images!!)
            bin.viewPagerImage.adapter = imageAdapter
            detailViewModel.totalImages = detailViewModel.postDetail.value!!.images!!.size
            if (detailViewModel.totalImages == 1) {
                detailViewModel.isShowNext.value = false
                detailViewModel.isShowBack.value = false
            }
        } else {
            detailViewModel.isNull.value = true
        }
    }

    private fun changeImages() {
        detailViewModel.currentImage.observe(viewLifecycleOwner) {
            bin.viewPagerImage.setCurrentItem(it, true)
        }
    }

    private fun changePager() {
        bin.viewPagerImage.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                detailViewModel.currentImage.value = position
                detailViewModel.updateArrowStatus()
            }
        })
    }

    private fun openDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_delete_confirm)
        dialog.setCancelable(false)

        val window = dialog.window ?: return
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttribute = window.attributes
        windowAttribute.gravity = Gravity.CENTER
        window.attributes = windowAttribute

        val buttonConfirm = dialog.findViewById<Button>(R.id.button_Confirm)
        val buttonCancel = dialog.findViewById<Button>(R.id.button_Cancel)

        buttonConfirm.setOnClickListener {
            dialog.dismiss()
            detailViewModel.deleting()
        }

        buttonCancel.setOnClickListener {
            dialog.dismiss()
            detailViewModel.isOpenDialog.value = false
        }

        dialog.show()
    }
}