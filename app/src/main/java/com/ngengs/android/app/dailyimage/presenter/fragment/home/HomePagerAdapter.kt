package com.ngengs.android.app.dailyimage.presenter.fragment.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ngengs.android.app.dailyimage.presenter.fragment.favorite.FavoriteFragment
import com.ngengs.android.app.dailyimage.presenter.fragment.latest.LatestImageFragment

class HomePagerAdapter(
    private val tabSize: Int,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = tabSize

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> LatestImageFragment()
            else -> FavoriteFragment()
        }
    }
}