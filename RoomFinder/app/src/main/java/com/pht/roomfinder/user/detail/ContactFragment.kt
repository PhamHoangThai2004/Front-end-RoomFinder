package com.pht.roomfinder.user.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.pht.roomfinder.adapters.SearchAdapter
import com.pht.roomfinder.databinding.FragmentContactBinding
import com.pht.roomfinder.utils.Const
import com.pht.roomfinder.viewmodel.DetailViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactFragment : Fragment() {
    private lateinit var bin: FragmentContactBinding
    private lateinit var detailViewModel: DetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        detailViewModel = ViewModelProvider(requireActivity())[DetailViewModel::class.java]
        bin = FragmentContactBinding.inflate(inflater, container, false)
        bin.detailViewModel = detailViewModel
        bin.lifecycleOwner = viewLifecycleOwner

        detailViewModel.postDetail.value!!.user?.userId?.let {
            detailViewModel.getUserDetail(it)
        }

        detailViewModel.listPost.observe(viewLifecycleOwner) {
            setAdapter()
            setAvatar()
        }

        return bin.root
    }

    private fun setAvatar() {
        if (detailViewModel.userDetail.value?.avatar.isNullOrEmpty().not()) {
            CoroutineScope(Dispatchers.Main).launch {
                Const.loadImage(
                    bin.root.context,
                    detailViewModel.userDetail.value?.avatar!!,
                    bin.imageViewAvatar, 1
                )
            }
        }
    }

    private fun setAdapter() {
        try {
            val adapter = SearchAdapter(detailViewModel.listPost.value!!) {
                detailViewModel.selectPost(it)
            }
            bin.recyclerViewListPost.adapter = adapter
            bin.recyclerViewListPost.layoutManager = GridLayoutManager(requireContext(), 2)

        } catch (e: Exception) {
            Log.d("BBB", "error: ${e.message}")
        }
    }

}