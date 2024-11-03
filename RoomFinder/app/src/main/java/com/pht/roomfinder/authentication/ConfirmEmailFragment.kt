package com.pht.roomfinder.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pht.roomfinder.R
import com.pht.roomfinder.databinding.FragmentConfirmEmailBinding
import com.pht.roomfinder.viewmodel.ForgotViewModel

class ConfirmEmailFragment : Fragment() {
    private lateinit var bin: FragmentConfirmEmailBinding
    private lateinit var forgotViewModel: ForgotViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        forgotViewModel = (activity as ForgotActivity).forgotViewModel
        bin = FragmentConfirmEmailBinding.inflate(inflater, container, false)
        bin.lifecycleOwner = viewLifecycleOwner
        bin.forgotViewModel = forgotViewModel
        return bin.root
    }

}