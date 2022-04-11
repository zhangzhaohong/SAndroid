package com.tristana.sandroid.ui.home

import com.tristana.sandroid.ui.home.HomeViewModel
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.LogUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.tristana.sandroid.R
import com.tristana.sandroid.ui.about.AboutFragment
import com.tristana.sandroid.ui.main.MainFragment

class HomeFragment : Fragment() {
    private var homeViewModel: HomeViewModel? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        if (homeViewModel == null) homeViewModel =
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
                .create(
                    HomeViewModel::class.java
                )
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val navigationView = root.findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        //1、先拿NavHostFragment
        // val navHostFragment = childFragmentManager.findFragmentById(R.id.bottom_nav_host_fragment) as NavHostFragment
        //2、再拿NavController
        // val navController: NavController = navHostFragment.navController
        // NavigationUI.setupActionBarWithNavController(requireActivity(), navController, mAppBarConfiguration!!)
        // NavigationUI.setupWithNavController(navigationView, navController)
        val viewpager: ViewPager2 = root.findViewById(R.id.bottom_nav_host_fragment_viewpager)
        viewpager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return 2
            }

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> MainFragment()
                    1 -> AboutFragment()
                    else -> MainFragment()
                }
            }
        }
        viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                navigationView.menu.getItem(position).isChecked = true
            }
        })
        navigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bottom_navigation_main -> {
                    viewpager.setCurrentItem(0, true)
                }
                R.id.bottom_navigation_about -> {
                    viewpager.setCurrentItem(1, true)
                }
                else -> {
                    viewpager.setCurrentItem(0, true)
                }
            }
            true
        }
        return root
    }
}