package com.tristana.sandroid.ui.home

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tristana.sandroid.R
import com.tristana.sandroid.ui.about.AboutFragment
import com.tristana.sandroid.ui.main.MainFragment
import com.tristana.sandroid.ui.music.area.MusicAreaFragment
import com.tristana.sandroid.ui.video.area.VideoAreaFragment
import com.tristana.sandroid.ui.video.area.recommend.VideoRecommendFragment

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
        val toolbar = activity?.findViewById<Toolbar>(R.id.toolbar)
        val navigationView = root.findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        //1、先拿NavHostFragment
        // val navHostFragment = childFragmentManager.findFragmentById(R.id.bottom_nav_host_fragment) as NavHostFragment
        //2、再拿NavController
        // val navController: NavController = navHostFragment.navController
        // NavigationUI.setupActionBarWithNavController(requireActivity(), navController, mAppBarConfiguration!!)
        // NavigationUI.setupWithNavController(navigationView, navController)
        val viewpager: ViewPager2 = root.findViewById(R.id.bottom_nav_host_fragment_viewpager)
        viewpager.isUserInputEnabled = false
        viewpager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return 4
            }

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> MainFragment()
                    1 -> MusicAreaFragment()
                    2 -> VideoAreaFragment()
                    3 -> AboutFragment()
                    else -> MainFragment()
                }
            }
        }
        viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                navigationView.menu.getItem(position).isChecked = true
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    toolbar?.title = navigationView.menu.getItem(position).title
                }
                when (position) {
                    0 -> toolbar?.visibility = View.VISIBLE
                    1, 2 -> toolbar?.visibility = View.GONE
                    3 -> toolbar?.visibility = View.VISIBLE
                    else -> toolbar?.visibility = View.VISIBLE
                }
            }
        })
        navigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bottom_navigation_main -> {
                    viewpager.setCurrentItem(0, true)
                }

                R.id.bottom_navigation_music_area -> {
                    viewpager.setCurrentItem(1, true)
                }

                R.id.bottom_navigation_video_area -> {
                    viewpager.setCurrentItem(2, true)
                }

                R.id.bottom_navigation_about -> {
                    viewpager.setCurrentItem(3, true)
                }

                else -> {
                    viewpager.setCurrentItem(0, true)
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                toolbar?.title = it.title
            }
            true
        }
        return root
    }
}