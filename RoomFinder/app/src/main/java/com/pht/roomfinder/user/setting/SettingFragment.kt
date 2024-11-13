package com.pht.roomfinder.user.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pht.roomfinder.R
import com.pht.roomfinder.adapters.OptionAdapter.Companion.TYPE_IMAGE_VIEW
import com.pht.roomfinder.adapters.OptionAdapter.Companion.TYPE_SWITCH
import com.pht.roomfinder.adapters.OptionAdapter.Companion.TYPE_TEXT_VIEW
import com.pht.roomfinder.databinding.FragmentSettingBinding
import com.pht.roomfinder.viewmodel.UserViewModel

class SettingFragment : Fragment() {
    private lateinit var bin: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        bin = FragmentSettingBinding.inflate(inflater, container, false)
        bin.lifecycleOwner = viewLifecycleOwner

        return bin.root
    }

}