package com.example.pharmacy_store.Adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.pharmacy_store.About
import com.example.pharmacy_store.Security

class AccountPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> About()
            1 -> Security()
            else -> throw IllegalStateException("Invalid position")
        }
    }
}