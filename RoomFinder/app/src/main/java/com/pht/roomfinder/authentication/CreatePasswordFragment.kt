package com.pht.roomfinder.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.pht.roomfinder.utils.Const
import com.pht.roomfinder.R
import com.pht.roomfinder.databinding.FragmentCreatePasswordBinding
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

        forgotViewModel = (activity as ForgotActivity).forgotViewModel
        bin = FragmentCreatePasswordBinding.inflate(inflater, container, false)
        bin.lifecycleOwner = viewLifecycleOwner
        bin.forgotViewModel = forgotViewModel

        val dialog = Const.setDialog(R.layout.dialog_checkmark, requireContext())

        forgotViewModel.errorMessage.observe(viewLifecycleOwner) {
            if (it == null) {
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