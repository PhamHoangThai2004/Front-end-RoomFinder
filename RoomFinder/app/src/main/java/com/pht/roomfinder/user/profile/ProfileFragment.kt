package com.pht.roomfinder.user.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pht.roomfinder.R
import com.pht.roomfinder.adapters.ProfileAdapter
import com.pht.roomfinder.adapters.ProfileAdapter.Companion.TYPE_IMAGE_VIEW
import com.pht.roomfinder.adapters.ProfileAdapter.Companion.TYPE_SWITCH
import com.pht.roomfinder.adapters.ProfileAdapter.Companion.TYPE_TEXT_VIEW
import com.pht.roomfinder.databinding.FragmentProfileBinding
import com.pht.roomfinder.viewmodel.UserViewModel

class ProfileFragment : Fragment() {
    private lateinit var bin: FragmentProfileBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]

        bin = FragmentProfileBinding.inflate(inflater, container, false)
        bin.lifecycleOwner = viewLifecycleOwner
        bin.userViewModel = userViewModel

        val adapter = ProfileAdapter(setList())
        bin.recyclerViewProfile.adapter = adapter
        bin.recyclerViewProfile.layoutManager = LinearLayoutManager(requireContext())

        return bin.root
    }

    private fun setList(): List<FunctionName> {
        val list = listOf(
            FunctionName(R.string.information_account, R.drawable.name_icon, null, TYPE_IMAGE_VIEW),
            FunctionName(R.string.my_post, R.drawable.post_icon, null, TYPE_IMAGE_VIEW),
            FunctionName(R.string.location, R.drawable.location_icon, null, TYPE_IMAGE_VIEW),
            FunctionName(R.string.save_account, null, null, TYPE_SWITCH),
            FunctionName(R.string.change_password, R.drawable.name_icon, null, TYPE_IMAGE_VIEW),
            FunctionName(R.string.language, null, R.string.vietnamese, TYPE_TEXT_VIEW),
            FunctionName(R.string.theme, null, null, TYPE_SWITCH),
            FunctionName(
                R.string.contact_support,
                R.drawable.contact_support_icon,
                null,
                TYPE_IMAGE_VIEW
            ),
            FunctionName(R.string.logout, R.drawable.logout_icon, null, TYPE_IMAGE_VIEW)
        )
        return list
    }

}