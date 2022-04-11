package com.tristana.sandroid.ui.home

import com.tristana.sandroid.ui.home.HomeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
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
        val bottomNavigationView = root.findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        return root
    }
}