package com.pht.roomfinder.user.post

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.pht.roomfinder.adapters.SearchAdapter
import com.pht.roomfinder.databinding.FragmentPostBinding
import com.pht.roomfinder.model.User
import com.pht.roomfinder.user.detail.DetailActivity
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

        toDetailPost()

        return bin.root
    }

    private fun showPostFilter() {
        postViewModel.listPosts.observe(viewLifecycleOwner) { listPosts ->
            val adapter = SearchAdapter(listPosts) {
                postViewModel.selectPost(it)
            }
            bin.recyclerViewFilter.adapter = adapter
            bin.recyclerViewFilter.layoutManager = GridLayoutManager(requireContext(), 2)
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

    @SuppressLint("NewApi")
    private fun toDetailPost() {
        postViewModel.selectedPost.observe(viewLifecycleOwner) {
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