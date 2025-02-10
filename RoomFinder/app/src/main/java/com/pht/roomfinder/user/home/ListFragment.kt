package com.pht.roomfinder.user.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pht.roomfinder.adapters.ListItemAdapter
import com.pht.roomfinder.databinding.FragmentListBinding
import com.pht.roomfinder.model.User
import com.pht.roomfinder.user.detail.DetailActivity
import com.pht.roomfinder.viewmodel.HomeViewModel

class ListFragment : Fragment() {
    private lateinit var bin: FragmentListBinding
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        bin = FragmentListBinding.inflate(inflater, container, false)
        bin.lifecycleOwner = viewLifecycleOwner
        bin.homeViewModel = homeViewModel

        homeViewModel.getListGroupData()

        setListGroupData()

        toDetailPost()

        return bin.root
    }

    private fun setListGroupData() {
        homeViewModel.listGroupData.observe(viewLifecycleOwner) {
            val adapter = ListItemAdapter(it) { postID ->
                homeViewModel.selectedPost.value = postID
            }
            bin.recyclerViewParentList.adapter = adapter
            bin.recyclerViewParentList.layoutManager = LinearLayoutManager(requireContext())
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