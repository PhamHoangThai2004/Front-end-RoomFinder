package com.pht.roomfinder.user.favorite

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
import com.pht.roomfinder.databinding.FragmentFavoriteBinding
import com.pht.roomfinder.model.User
import com.pht.roomfinder.user.detail.DetailActivity
import com.pht.roomfinder.viewmodel.FavoriteViewModel

class FavoriteFragment : Fragment() {
    private lateinit var bin: FragmentFavoriteBinding
    private lateinit var favoriteViewModel: FavoriteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        favoriteViewModel = ViewModelProvider(this)[FavoriteViewModel::class.java]
        bin = FragmentFavoriteBinding.inflate(inflater, container, false)
        bin.favoriteViewModel = favoriteViewModel
        bin.lifecycleOwner = viewLifecycleOwner

        favoriteViewModel.getFavoritePosts()

        setData()

        toDetailPost()

        return bin.root
    }

    private fun setData() {
        favoriteViewModel.favoritePosts.observe(viewLifecycleOwner) { listPosts ->
            val adapter = SearchAdapter(listPosts) {
                favoriteViewModel.selectPost(it)
            }
            bin.recyclerViewFavoritePosts.adapter = adapter
            bin.recyclerViewFavoritePosts.layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    @SuppressLint("NewApi")
    private fun toDetailPost() {
        favoriteViewModel.selectedPost.observe(viewLifecycleOwner) {
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