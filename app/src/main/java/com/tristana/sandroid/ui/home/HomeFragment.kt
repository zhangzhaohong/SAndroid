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
import com.blankj.utilcode.util.LogUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.tristana.sandroid.R

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
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.bottom_nav_host_fragment) as NavHostFragment
        //2、再拿NavController
        val navController: NavController = navHostFragment.navController
        // NavigationUI.setupActionBarWithNavController(requireActivity(), navController, mAppBarConfiguration!!)
        NavigationUI.setupWithNavController(navigationView, navController)
        return root
    }
}