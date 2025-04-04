package com.pht.roomfinder.view.setting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.pht.roomfinder.databinding.FragmentAvatarBinding
import com.pht.roomfinder.utils.Const
import com.pht.roomfinder.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AvatarFragment : Fragment() {
    private lateinit var bin: FragmentAvatarBinding
    private lateinit var userViewModel: UserViewModel
    private val choiceAvatar =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                userViewModel.setUpdate(true)
                val data = it.data
                val imageUri = data?.data
                imageUri.let { uri ->
                    Glide.with(requireContext()).load(uri).into(bin.imageViewAvatar)
                    userViewModel.imagePath.value = Const.changeToPathFile(uri!!, requireContext())
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        bin = FragmentAvatarBinding.inflate(inflater, container, false)
        bin.userViewModel = userViewModel
        bin.lifecycleOwner = viewLifecycleOwner

        userViewModel.avatar.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty().not()) {
                CoroutineScope(Dispatchers.Main).launch {
                    Const.loadImage(requireContext(), it!!, bin.imageViewAvatar, 1)
                }
            }
        }

        bin.buttonChoiceAvatar.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            choiceAvatar.launch(intent)
        }

        return bin.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    userViewModel.popBack()
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        userViewModel.setShowBottomNavigation(true)
    }

}