package com.pht.roomfinder.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pht.roomfinder.user.favorite.FavoriteFragment
import com.pht.roomfinder.user.home.HomeFragment
import com.pht.roomfinder.user.post.PostFragment
import com.pht.roomfinder.user.setting.SettingFragment

class BottomNavigationAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount() = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> PostFragment()
            2 -> FavoriteFragment()
            else -> SettingFragment()
        }
    }

}