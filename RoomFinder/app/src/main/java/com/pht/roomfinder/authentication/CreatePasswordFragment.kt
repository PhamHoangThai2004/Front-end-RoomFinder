package com.pht.roomfinder.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.pht.roomfinder.R
import com.pht.roomfinder.databinding.FragmentCreatePasswordBinding
import com.pht.roomfinder.utils.Const
import com.pht.roomfinder.viewmodel.ForgotViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CreatePasswordFragment : Fragment() {
    private lateinit var bin: FragmentCreatePasswordBinding
    private lateinit var forgotViewModel: ForgotViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        forgotViewModel = ViewModelProvider(requireActivity())[ForgotViewModel::class.java]
        bin = FragmentCreatePasswordBinding.inflate(inflater, container, false)
        bin.lifecycleOwner = viewLifecycleOwner
        bin.forgotViewModel = forgotViewModel

        val dialog = Const.setDialog(R.layout.dialog_checkmark, requireContext())

        forgotViewModel.success.observe(viewLifecycleOwner) {
            if (it) {
                viewLifecycleOwner.lifecycleScope.launch {
                    dialog.show()
                    delay(2000)
                    dialog.dismiss()
                    forgotViewModel.move.value = 0
                }
            }
        }

        return bin.root
    }

}