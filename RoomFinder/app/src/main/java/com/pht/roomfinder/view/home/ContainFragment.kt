package com.pht.roomfinder.view.home

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.pht.roomfinder.R
import com.pht.roomfinder.databinding.FragmentContainBinding
import com.pht.roomfinder.model.User
import com.pht.roomfinder.utils.Const
import com.pht.roomfinder.view.detail.DetailActivity
import com.pht.roomfinder.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

class ContainFragment : Fragment() {
    private lateinit var bin: FragmentContainBinding
    private lateinit var homeViewModel: HomeViewModel

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        bin = FragmentContainBinding.inflate(inflater, container, false)
        bin.lifecycleOwner = viewLifecycleOwner
        bin.homeViewModel = homeViewModel

        homeViewModel.getListGroupData()

        val dialogLoading = Const.setDialog(R.layout.dialog_loading, requireContext())

        setDialogLoading(dialogLoading)

        toDetailPost()

        return bin.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun toDetailPost() {
        lifecycleScope.launch {
            homeViewModel.selectedPost.collect {
                if (it > 0) {
                    val user =
                        requireActivity().intent.getSerializableExtra("user", User::class.java)
                    val intent = Intent(requireContext(), DetailActivity::class.java)
                    val bundle = Bundle()
                    bundle.putInt("userId", user?.userId ?: -1)
                    bundle.putInt("postId", it)
                    intent.putExtras(bundle)
                    startActivity(intent)
                    homeViewModel.setSelectedPost(0)
                }
            }
        }
    }

    private fun setDialogLoading(dialogLoading: Dialog) {
        lifecycleScope.launch {
            homeViewModel.uiState.collect {
                if (it.isLoading) dialogLoading.show()
                else dialogLoading.dismiss()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.fragment_Container_View) as NavHostFragment
        homeViewModel.toLayout.observe(viewLifecycleOwner) {
            when (it) {
                HomeViewModel.HOME -> {
                    navHostFragment.navController.navigate(R.id.homeFragment)
                }

                HomeViewModel.SEARCH -> {
                    navHostFragment.navController.navigate(R.id.searchFragment)
                }

                HomeViewModel.FILTER -> {
                    navHostFragment.navController.navigate(R.id.postFragment)
                }

                HomeViewModel.BONUS -> {
                    navHostFragment.navController.navigate((R.id.bonusFragment))
                }

                HomeViewModel.FIND_ROOMMATES -> {
                    navHostFragment.navController.navigate(R.id.roommateFragment)
                }
            }
        }
    }
}