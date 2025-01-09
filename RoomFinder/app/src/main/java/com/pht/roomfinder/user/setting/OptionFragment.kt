package com.pht.roomfinder.user.setting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pht.roomfinder.R
import com.pht.roomfinder.adapters.OptionAdapter
import com.pht.roomfinder.adapters.OptionAdapter.Companion.TYPE_IMAGE_VIEW
import com.pht.roomfinder.adapters.OptionAdapter.Companion.TYPE_SWITCH
import com.pht.roomfinder.adapters.OptionAdapter.Companion.TYPE_TEXT_VIEW
import com.pht.roomfinder.authentication.AuthActivity
import com.pht.roomfinder.databinding.FragmentOptionBinding
import com.pht.roomfinder.listen.OnItemClickListener
import com.pht.roomfinder.utils.Const
import com.pht.roomfinder.utils.DataLocal
import com.pht.roomfinder.utils.FunctionName
import com.pht.roomfinder.utils.ItemImage
import com.pht.roomfinder.utils.ItemSwitch
import com.pht.roomfinder.utils.ItemText
import com.pht.roomfinder.viewmodel.UserViewModel

class OptionFragment : Fragment() {
    private lateinit var bin: FragmentOptionBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]

        bin = FragmentOptionBinding.inflate(inflater, container, false)
        bin.lifecycleOwner = viewLifecycleOwner
        bin.userViewModel = userViewModel

        val adapter = OptionAdapter(setList())
        bin.recyclerViewProfile.adapter = adapter
        bin.recyclerViewProfile.layoutManager = LinearLayoutManager(requireContext())

        adapter.onItemClickListener = object : OnItemClickListener {
            override fun onSwitchChange(isChecked: Boolean, position: Int) {
                if (position == SettingFragment.SAVE_ACCOUNT) userViewModel.saveAccount(
                    userViewModel.user.value?.email ?: "",
                    DataLocal.getInstance().getString(Const.PASSWORD) ?: "",
                    isChecked
                )
            }

            override fun onImageClick(position: Int) {
                when (position) {
                    SettingFragment.INFORMATION_ACCOUNT -> {
                        userViewModel.toFunction(position)
                        userViewModel.setUpgrade(false)
                    }

                    SettingFragment.MY_POST -> {
                        userViewModel.toFunction(position)
                    }

                    SettingFragment.CHANGE_PASSWORD -> {
                        userViewModel.toFunction(position)
                    }

                    SettingFragment.LOGOUT -> {
                        logOut()
                    }
                }
            }

            override fun onTextClick(position: Int) {
                Toast.makeText(
                    requireContext(),
                    "text ở vị trí $position được nhấn",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        return bin.root
    }

    private fun logOut() {
        userViewModel.logout()
        requireActivity().startActivity(Intent(requireContext(), AuthActivity::class.java))
        requireActivity().finish()
    }

    private fun setList(): List<FunctionName> {
        val list: List<FunctionName> = listOf(
            ItemImage(R.string.information_account, TYPE_IMAGE_VIEW, R.drawable.name_icon),
            ItemImage(R.string.avatar, TYPE_IMAGE_VIEW, R.drawable.avatar_icon),
            ItemImage(R.string.my_posts, TYPE_IMAGE_VIEW, R.drawable.post_icon),
            ItemImage(R.string.location, TYPE_IMAGE_VIEW, R.drawable.location_icon),
            ItemSwitch(
                R.string.save_account,
                TYPE_SWITCH,
                DataLocal.getInstance().getBoolean(Const.SAVE_ACCOUNT)
            ),
            ItemImage(R.string.change_password, TYPE_IMAGE_VIEW, R.drawable.name_icon),
            ItemText(R.string.language, TYPE_TEXT_VIEW, R.string.vietnamese),
            ItemSwitch(R.string.theme, TYPE_SWITCH, false),
            ItemImage(
                R.string.contact_support,
                TYPE_IMAGE_VIEW,
                R.drawable.contact_support_icon
            ),
            ItemImage(R.string.logout, TYPE_IMAGE_VIEW, R.drawable.logout_icon)
        )
        return list
    }
}