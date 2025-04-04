package com.pht.roomfinder.view.favorite

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.pht.roomfinder.R
import com.pht.roomfinder.adapters.PostAdapter
import com.pht.roomfinder.databinding.FragmentFavoriteBinding
import com.pht.roomfinder.model.User
import com.pht.roomfinder.utils.Const
import com.pht.roomfinder.view.detail.DetailActivity
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

        setDialogLoading()

        setData()

        toDetailPost()

        return bin.root
    }

    private fun setData() {
        val adapter = PostAdapter(emptyList()) {
            favoriteViewModel.selectPost(it)
        }
        bin.recyclerViewFavoritePosts.adapter = adapter
        bin.recyclerViewFavoritePosts.layoutManager = GridLayoutManager(requireContext(), 2)

        favoriteViewModel.listFavorite.observe(viewLifecycleOwner) {
            adapter.updateData(it)
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

    private fun setDialogLoading() {
        val dialogLoading = Const.setDialog(R.layout.dialog_loading, requireContext())
        favoriteViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) dialogLoading.show()
            else dialogLoading.dismiss()
        }
    }

}