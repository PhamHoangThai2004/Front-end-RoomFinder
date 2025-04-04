package com.pht.roomfinder.view.post

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.pht.roomfinder.R
import com.pht.roomfinder.adapters.PostAdapter
import com.pht.roomfinder.databinding.FragmentMyPostsBinding
import com.pht.roomfinder.model.User
import com.pht.roomfinder.utils.Const
import com.pht.roomfinder.view.detail.DetailActivity
import com.pht.roomfinder.view.new_post.NewPostActivity
import com.pht.roomfinder.viewmodel.PostViewModel
import kotlinx.coroutines.launch


class MyPostsFragment : Fragment() {
    private lateinit var bin: FragmentMyPostsBinding
    private lateinit var postViewModel: PostViewModel

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        postViewModel = ViewModelProvider(this)[PostViewModel::class.java]
        bin = FragmentMyPostsBinding.inflate(inflater, container, false)
        bin.lifecycleOwner = viewLifecycleOwner
        bin.postViewModel = postViewModel

        setDialogLoading()

        postViewModel.getLists()

        setData()

        toDetailPost()

        lifecycleScope.launch {
            postViewModel.uiState.collect {
                if (it.isOpen) {
                    val user =
                        requireActivity().intent.getSerializableExtra("user", User::class.java)
                    val intent = Intent(requireContext(), NewPostActivity::class.java)
                    intent.putExtra("user", user)
                    startActivity(intent)
                    postViewModel.toNewPost(false)
                }
            }
        }

        return bin.root
    }

    private fun setDialogLoading() {
        val dialogLoading = Const.setDialog(R.layout.dialog_loading, requireContext())
        lifecycleScope.launch {
            postViewModel.uiState.collect {
                if (it.isLoading) dialogLoading.show()
                else dialogLoading.dismiss()
            }
        }
    }

    private fun setData() {
        postViewModel.listPosts.observe(viewLifecycleOwner) {
            val adapter = PostAdapter(it) { postId ->
                postViewModel.setSelectedPost(postId)
            }
            bin.recyclerViewMyPosts.adapter = adapter
            bin.recyclerViewMyPosts.layoutManager = GridLayoutManager(requireContext(), 2)
        }

        postViewModel.listExpired.observe(viewLifecycleOwner) {
            val adapter = PostAdapter(it) { postId ->
                postViewModel.setSelectedPost(postId)
            }
            bin.recyclerViewExpiredPosts.adapter = adapter
            bin.recyclerViewExpiredPosts.layoutManager = GridLayoutManager(requireContext(), 2)
        }
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