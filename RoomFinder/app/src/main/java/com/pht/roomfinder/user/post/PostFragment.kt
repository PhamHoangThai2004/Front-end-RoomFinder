package com.pht.roomfinder.user.post

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.pht.roomfinder.R
import com.pht.roomfinder.adapters.SearchAdapter
import com.pht.roomfinder.databinding.FragmentPostBinding
import com.pht.roomfinder.viewmodel.PostViewModel

class PostFragment : Fragment() {
    private lateinit var bin: FragmentPostBinding
    private lateinit var postViewModel: PostViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        postViewModel = ViewModelProvider(requireActivity())[PostViewModel::class.java]
        bin = FragmentPostBinding.inflate(inflater, container, false)
        bin.postViewModel = postViewModel
        bin.lifecycleOwner = viewLifecycleOwner

        getListCategory()

        openFilter()

        showPostFilter()

        return bin.root
    }

    private fun showPostFilter() {
        postViewModel.listPost.observe(viewLifecycleOwner) {
            if (postViewModel.listPost.value != null && postViewModel.listPost.value!!.isNotEmpty()) {
                val adapter = SearchAdapter(postViewModel.listPost.value!!)
                bin.recyclerViewFilter.adapter = adapter
                bin.recyclerViewFilter.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                postViewModel.isNull.value = false
            } else {
                postViewModel.isNull.value = true
            }
        }

    }

    private fun openFilter() {
        postViewModel.isOpenFilter.observe(viewLifecycleOwner) {
            if (it) {
                val dialog = FilterFragment()
                dialog.show(parentFragmentManager, "FilterFragment")
            }
        }
    }

    private fun getListCategory() {
        postViewModel.getListCategory()
    }

}