package com.pht.roomfinder.user.setting

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pht.roomfinder.MainActivity
import com.pht.roomfinder.R
import com.pht.roomfinder.adapters.OptionAdapter
import com.pht.roomfinder.adapters.OptionAdapter.Companion.TYPE_IMAGE_VIEW
import com.pht.roomfinder.adapters.OptionAdapter.Companion.TYPE_SWITCH
import com.pht.roomfinder.adapters.OptionAdapter.Companion.TYPE_TEXT_VIEW
import com.pht.roomfinder.authentication.AuthActivity
import com.pht.roomfinder.databinding.FragmentOptionBinding
import com.pht.roomfinder.listen.OnItemClickListener
import com.pht.roomfinder.utils.App
import com.pht.roomfinder.utils.AppLocale
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
    var list = setList()
    private var adapter = OptionAdapter(list)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]

        bin = FragmentOptionBinding.inflate(inflater, container, false)
        bin.lifecycleOwner = viewLifecycleOwner
        bin.userViewModel = userViewModel

        bin.recyclerViewProfile.adapter = adapter
        bin.recyclerViewProfile.layoutManager = LinearLayoutManager(requireContext())

        adapter.onItemClickListener = object : OnItemClickListener {
            override fun onSwitchChange(isChecked: Boolean, position: Int) {
                if (position == SettingFragment.SAVE_ACCOUNT) saveAccount(isChecked)
                else if (position == SettingFragment.THEME) changeTheme(isChecked)
            }

            override fun onImageClick(position: Int) {
                when (position) {
                    SettingFragment.INFORMATION_ACCOUNT -> {
                        userViewModel.toFunction(position)
                        userViewModel.setUpgrade(false)
                    }

                    SettingFragment.AVATAR -> {
                        userViewModel.toFunction(position)
                        userViewModel.setUpgrade(false)
                    }

                    SettingFragment.MY_POST -> {
                        userViewModel.toFunction(position)
                    }

                    SettingFragment.LOCATION -> {
                        Toast.makeText(requireContext(), "Chức năng này đang được phát triển", Toast.LENGTH_SHORT).show()
                    }

                    SettingFragment.CHANGE_PASSWORD -> {
                        userViewModel.toFunction(position)
                    }

                    SettingFragment.CONTACT_SUPPORT -> {
                        toContact()
                    }

                    SettingFragment.LOGOUT -> {
                        logout()
                    }
                }
            }

            override fun onTextClick(position: Int) {
               changeLanguages(position)
            }

        }

        return bin.root
    }

    private fun changeLanguages(position: Int) {
        val languages = DataLocal.getInstance().getString(AppLocale.APP_LANGUAGE) ?: "vi"
        DataLocal.getInstance().putString(AppLocale.APP_LANGUAGE, if (languages == "en") "vi" else "en")
        list[position] = ItemText(R.string.language, TYPE_TEXT_VIEW, if (languages == "en") R.string.vietnamese else R.string.english)
        adapter.notifyItemChanged(position)

        AppLocale.setAppLocale(requireContext())
        requireActivity().recreate()
    }

    private fun toContact() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("thai2k4honquang@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Hỗ trợ khách hàng")
            putExtra(Intent.EXTRA_TEXT, "Nội dung cần hỗ trợ ...")
            setPackage("com.google.android.gm")
        }
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "Không tìm thấy ứng dụng Gmail!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveAccount(isChecked: Boolean) {
        userViewModel.saveAccount(
            userViewModel.user.value?.email ?: "",
            DataLocal.getInstance().getString(Const.PASSWORD) ?: "",
            isChecked
        )
    }

    private fun changeTheme(isChecked: Boolean) {
        if (isChecked) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun logout() {
        userViewModel.logout()
        requireActivity().startActivity(Intent(requireContext(), AuthActivity::class.java))
        requireActivity().finish()
    }

    private fun setList(): MutableList<FunctionName> {
        val languages = DataLocal.getInstance().getString(AppLocale.APP_LANGUAGE) ?: "vi"

        val list: MutableList<FunctionName> = mutableListOf(
            ItemImage(R.string.profile, TYPE_IMAGE_VIEW, R.drawable.name_icon),
            ItemImage(R.string.avatar, TYPE_IMAGE_VIEW, R.drawable.avatar_icon),
            ItemImage(R.string.my_posts, TYPE_IMAGE_VIEW, R.drawable.post_icon),
            ItemImage(R.string.location, TYPE_IMAGE_VIEW, R.drawable.location_icon),
            ItemSwitch(R.string.save_account, TYPE_SWITCH, DataLocal.getInstance().getBoolean(Const.SAVE_ACCOUNT)),
            ItemImage(R.string.change_password, TYPE_IMAGE_VIEW, R.drawable.name_icon),
            ItemText(R.string.language, TYPE_TEXT_VIEW, if (languages == "en") R.string.english else R.string.vietnamese),
            ItemSwitch(R.string.theme, TYPE_SWITCH, false),
            ItemImage(R.string.contact_support, TYPE_IMAGE_VIEW, R.drawable.contact_support_icon),
            ItemImage(R.string.logout, TYPE_IMAGE_VIEW, R.drawable.logout_icon)
        )
        return list
    }
}