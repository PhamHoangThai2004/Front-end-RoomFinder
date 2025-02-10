package com.pht.roomfinder.user.setting

import android.annotation.SuppressLint
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
import com.pht.roomfinder.model.User
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

        userViewModel.getList()

        setData()

        toDetailPost()

        return bin.root
    }

    private fun setData() {
        userViewModel.listPosts.observe(viewLifecycleOwner) { listPosts ->
            val adapter = SearchAdapter(listPosts) {
                userViewModel.selectedPost.value = it
            }
            bin.recyclerViewMyPosts.adapter = adapter
            bin.recyclerViewMyPosts.layoutManager = GridLayoutManager(requireContext(), 2)
        }

        userViewModel.listExpired.observe(viewLifecycleOwner) { listExpired ->
            val adapter = SearchAdapter(listExpired) {
                userViewModel.selectedPost.value = it
            }
            bin.recyclerViewExpiredPosts.adapter = adapter
            bin.recyclerViewExpiredPosts.layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    @SuppressLint("NewApi")
    private fun toDetailPost() {
        userViewModel.selectedPost.observe(viewLifecycleOwner) {
            val user = requireActivity().intent.getSerializableExtra("user", User::class.java)

            val intent = Intent(requireContext(), DetailActivity::class.java)
            val bundle = Bundle()
            bundle.putInt("userId", user?.userId ?: -1)
            bundle.putInt("postId", it)
            intent.putExtras(bundle)

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