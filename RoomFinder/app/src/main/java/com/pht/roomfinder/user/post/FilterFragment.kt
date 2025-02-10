package com.pht.roomfinder.user.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.pht.roomfinder.R
import com.pht.roomfinder.databinding.FragmentFilterBinding
import com.pht.roomfinder.viewmodel.PostViewModel

class FilterFragment : DialogFragment() {
    private lateinit var bin: FragmentFilterBinding
    private lateinit var postViewModel: PostViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        postViewModel = ViewModelProvider(requireActivity())[PostViewModel::class.java]
        bin = FragmentFilterBinding.inflate(inflater, container, false)
        bin.postViewModel = postViewModel
        bin.lifecycleOwner = viewLifecycleOwner

        bin.imageButton.setOnClickListener {
            dismiss()
        }

        changeSelect()

        setFilter()

        return bin.root
    }

    private fun changeSelect() {
        bin.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                postViewModel.category.value = postViewModel.listCategory.value!![position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        bin.spinnerProvince.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                postViewModel.province.value = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        bin.spinnerPrice.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                postViewModel.price.value = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        bin.spinnerAcreage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                postViewModel.acreage.value = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }

    private fun setFilter() {
        postViewModel.listCategory.observe(this) {
            val adapterCategory = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, postViewModel.listCategory.value!!)
            adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            bin.spinnerCategory.adapter = adapterCategory

            val positionCategory = postViewModel.listCategory.value!!.indexOf(postViewModel.category.value)
            bin.spinnerCategory.setSelection(positionCategory)

            if (postViewModel.province.value != "Toàn quốc") {
                val positionProvince = resources.getStringArray(R.array.province_names).indexOf(postViewModel.province.value)
                bin.spinnerProvince.setSelection(positionProvince)
            }

            if (postViewModel.price.value != "Tất cả") {
                val positionPrice = resources.getStringArray(R.array.price_range).indexOf(postViewModel.price.value)
                bin.spinnerPrice.setSelection(positionPrice)
            }

            if (postViewModel.acreage.value != "Tất cả") {
                val positionAcreage = resources.getStringArray(R.array.acreage_range).indexOf(postViewModel.acreage.value)
                bin.spinnerAcreage.setSelection(positionAcreage)
            }

        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }
}