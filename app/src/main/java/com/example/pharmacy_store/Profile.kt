package com.example.pharmacy_store


import SharedPrefManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class Profile : Fragment(), PopupMenu.OnMenuItemClickListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager: ViewPager2 = view.findViewById(R.id.container)
        viewPager.adapter = AccountPagerAdapter(this)


        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "About"
                1 -> "Security"
                else -> throw IllegalStateException("Invalid position")
            }
        }.attach()

        val signoutPowerIcon: ImageView = view.findViewById(R.id.signout_power_icon)
        signoutPowerIcon.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), it)
            popupMenu.inflate(R.menu.sign_out_menu)
            popupMenu.setOnMenuItemClickListener(this)
            popupMenu.show()
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sign_out -> {
                val sharedPrefManager = SharedPrefManager(requireContext())
                sharedPrefManager.setLoggedIn(false)

                // Navigate to DivideBegin
                val intent = Intent(requireActivity(), DivideBegin::class.java)
                startActivity(intent)
                requireActivity().finish()
                true
            }
            else -> false
        }
    }

}