package com.pht.roomfinder.view.new_post

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pht.roomfinder.R
import com.pht.roomfinder.databinding.FragmentDescriptionBinding
import com.pht.roomfinder.viewmodel.NewPostViewModel

class DescriptionFragment : Fragment() {
    private lateinit var bin: FragmentDescriptionBinding
    private lateinit var newPostViewModel: NewPostViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bin = FragmentDescriptionBinding.inflate(inflater, container, false)
        newPostViewModel = ViewModelProvider(requireActivity())[NewPostViewModel::class.java]
        bin.newPostVM = newPostViewModel
        bin.lifecycleOwner = viewLifecycleOwner

        setInput()

        setSpinner()

        return bin.root
    }

    private fun setSpinner() {
        newPostViewModel.getListCategories()
        newPostViewModel.listCategories.observe(viewLifecycleOwner) {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, it)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            bin.spinnerCategory.adapter = adapter
        }

        bin.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                newPostViewModel.postState.value = newPostViewModel.postState.value.copy(
                    categoryName = newPostViewModel.listCategories.value!![position]
                )
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }

    private fun setInput() {
        bin.editTextTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString().trim()
                if (text.length < 30 || text.length > 100) {
                    bin.inputLayoutTitle.error =
                        requireContext().getString(R.string.error_title)
                } else {
                    bin.inputLayoutTitle.error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {}

        })

        bin.editTextDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString().trim()
                if (text.length < 50 || text.length > 3000) {
                    bin.inputLayoutDescription.error =
                        requireContext().getString(R.string.error_description)
                } else {
                    bin.inputLayoutDescription.error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {}

        })

        bin.editTextPrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString().trim()
                if (text.isNotEmpty()) {
                    bin.inputLayoutPrice.error = null
                } else {
                    bin.inputLayoutPrice.error = requireContext().getString(R.string.error_price)
                }
            }

            override fun afterTextChanged(s: Editable?) {}

        })

        bin.editTextAcreage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString().trim()
                if (text.isNotEmpty()) {
                    bin.inputLayoutAcreage.error = null
                } else {
                    bin.inputLayoutAcreage.error =
                        requireContext().getString(R.string.error_acreage)
                }
            }

            override fun afterTextChanged(s: Editable?) {}

        })

        bin.editTextBonus.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString().trim()
                if (text.length > 200) {
                    bin.inputLayoutBonus.error = requireContext().getString(R.string.error_bonus)
                } else {
                    bin.inputLayoutBonus.error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }

}