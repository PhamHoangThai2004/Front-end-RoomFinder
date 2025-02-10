package com.pht.roomfinder.user.home

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.pht.roomfinder.R
import com.pht.roomfinder.adapters.SearchAdapter
import com.pht.roomfinder.databinding.FragmentSearchBinding
import com.pht.roomfinder.model.User
import com.pht.roomfinder.user.detail.DetailActivity
import com.pht.roomfinder.utils.Const
import com.pht.roomfinder.viewmodel.HomeViewModel

class SearchFragment : Fragment() {
    private lateinit var bin: FragmentSearchBinding
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        bin = FragmentSearchBinding.inflate(inflater, container, false)
        bin.lifecycleOwner = viewLifecycleOwner
        bin.homeViewModel = homeViewModel

        val dialogLoading = Const.setDialog(R.layout.dialog_loading, requireContext())

        setDialogLoading(dialogLoading)

        getListSearch()

        toDetailPost()

        return bin.root
    }

    private fun setDialogLoading(dialogLoading: Dialog) {
        homeViewModel.isLoad.observe(viewLifecycleOwner) {
            if (it) dialogLoading.show()
            else dialogLoading.dismiss()
        }
    }

    private fun getListSearch() {
        if (homeViewModel.listSearch.value != null && homeViewModel.listSearch.value!!.isNotEmpty()) {
            val adapter = SearchAdapter(homeViewModel.listSearch.value!!) {
                homeViewModel.selectedPost.value = it
            }
            bin.recyclerViewSearchList.adapter = adapter
            bin.recyclerViewSearchList.layoutManager = GridLayoutManager(requireContext(), 2)
            homeViewModel.isNull.value = false
        } else {
            homeViewModel.isNull.value = true
            homeViewModel.tmpSearch.value = homeViewModel.keySearch.value
        }
    }

    @SuppressLint("NewApi")
    private fun toDetailPost() {
        homeViewModel.selectedPost.observe(viewLifecycleOwner) {
            val user = requireActivity().intent.getSerializableExtra("user", User::class.java)

            val intent = Intent(requireContext(), DetailActivity::class.java)
            val bundle = Bundle()
            bundle.putInt("userId", user?.userId ?: -1)
            bundle.putInt("postId", it)
            intent.putExtras(bundle)

            startActivity(intent)
        }

    }
}