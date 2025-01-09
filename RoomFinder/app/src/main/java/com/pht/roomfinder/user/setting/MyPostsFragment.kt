package com.pht.roomfinder.user.setting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.pht.roomfinder.adapters.SearchAdapter
import com.pht.roomfinder.databinding.FragmentMyPostsBinding
import com.pht.roomfinder.user.detail.DetailActivity
import com.pht.roomfinder.viewmodel.UserViewModel


class MyPostsFragment : Fragment() {
    private lateinit var bin: FragmentMyPostsBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        bin = FragmentMyPostsBinding.inflate(inflater, container, false)
        bin.lifecycleOwner = viewLifecycleOwner
        bin.userViewModel = userViewModel

        userViewModel.getListPosts()

        setData()

        toDetailPost()

        return bin.root
    }

    private fun setData() {
        userViewModel.listPosts.observe(viewLifecycleOwner) { listPosts ->
            val adapter = SearchAdapter(listPosts) {
                userViewModel.selectedPost.value = it
            }
            bin.recyclerViewFavoritePosts.adapter = adapter
            bin.recyclerViewFavoritePosts.layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    private fun toDetailPost() {
        userViewModel.selectedPost.observe(viewLifecycleOwner) {
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("postID", it)
            startActivity(intent)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    userViewModel.popBack()
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        userViewModel.isShowBottomNavigation.value = true
    }
}