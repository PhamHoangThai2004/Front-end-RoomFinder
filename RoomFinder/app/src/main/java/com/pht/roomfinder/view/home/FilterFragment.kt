package com.pht.roomfinder.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.pht.roomfinder.R
import com.pht.roomfinder.databinding.FragmentFilterBinding
import com.pht.roomfinder.viewmodel.HomeViewModel

class FilterFragment : DialogFragment() {
    private lateinit var bin: FragmentFilterBinding
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        bin = FragmentFilterBinding.inflate(inflater, container, false)
        bin.homeViewModel = homeViewModel
        bin.lifecycleOwner = viewLifecycleOwner

        bin.imageButton.setOnClickListener {
            dismiss()
            homeViewModel.openFilter(false)
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
                homeViewModel.postState.value =
                    homeViewModel.postState.value.copy(categoryName = homeViewModel.listCategory.value!![position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        bin.spinnerArea.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                homeViewModel.postState.value = homeViewModel.postState.value.copy(
                    area = parent?.getItemAtPosition(position).toString()
                )
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
                homeViewModel.postState.value = homeViewModel.postState.value.copy(
                    price = parent?.getItemAtPosition(position).toString()
                )
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
                homeViewModel.postState.value = homeViewModel.postState.value.copy(
                    acreage = parent?.getItemAtPosition(position).toString()
                )
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }

    private fun setFilter() {
        homeViewModel.listCategory.observe(this) {
            val adapterCategory = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                homeViewModel.listCategory.value!!
            )
            adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            bin.spinnerCategory.adapter = adapterCategory

            val positionCategory =
                homeViewModel.listCategory.value!!.indexOf(homeViewModel.postState.value.categoryName)
            bin.spinnerCategory.setSelection(positionCategory)

            if (homeViewModel.postState.value.area != "Toàn quốc") {
                val positionArea = resources.getStringArray(R.array.province_names)
                    .indexOf(homeViewModel.postState.value.area)
                bin.spinnerArea.setSelection(positionArea)
            }

            if (homeViewModel.postState.value.price != "Tất cả") {
                val positionPrice =
                    resources.getStringArray(R.array.price_range)
                        .indexOf(homeViewModel.postState.value.price)
                bin.spinnerPrice.setSelection(positionPrice)
            }

            if (homeViewModel.postState.value.acreage != "Tất cả") {
                val positionAcreage = resources.getStringArray(R.array.acreage_range)
                    .indexOf(homeViewModel.postState.value.acreage)
                bin.spinnerAcreage.setSelection(positionAcreage)
            }

        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

}