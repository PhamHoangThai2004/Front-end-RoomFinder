package com.pht.roomfinder.user.post

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.pht.roomfinder.R
import com.pht.roomfinder.databinding.FragmentPostBinding
import com.pht.roomfinder.viewmodel.PostViewModel

class PostFragment : Fragment() {
    private lateinit var bin: FragmentPostBinding
    private lateinit var postViewModel: PostViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        postViewModel = ViewModelProvider(this)[PostViewModel::class.java]
        bin = FragmentPostBinding.inflate(inflater, container, false)
        bin.postViewModel = postViewModel
        bin.lifecycleOwner = viewLifecycleOwner

        return bin.root
    }

}