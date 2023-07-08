package com.tristana.sandroid.ui.music.area

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.tristana.sandroid.R
import com.tristana.sandroid.ui.music.area.recommend.MusicRecommendFragment
import com.tristana.sandroid.ui.music.area.search.MusicSearchFragment
import com.tristana.sandroid.ui.music.area.search.MusicSearchViewModel

class MusicAreaFragment : Fragment() {

    private var viewModel: MusicAreaViewModel? = null

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.music_area_tab_view)
    lateinit var tabView: TabLayout

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.music_area_container_view)
    lateinit var containerView: ViewPager2
    private lateinit var musicAreaViewAdapter: MusicAreaViewAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        if (viewModel == null) viewModel =
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
                .create(
                    MusicAreaViewModel::class.java
                )
        musicAreaViewAdapter = MusicAreaViewAdapter(this)
        val root = inflater.inflate(R.layout.fragment_music_area, container, false)
        ButterKnife.bind(this, root)
        containerView.adapter = musicAreaViewAdapter
        containerView.isUserInputEnabled = false
        TabLayoutMediator(tabView, containerView) { tabItem, position ->
            tabItem.text = "OBJECT ${position + 1}"
        }.attach()
        return root
    }

    class MusicAreaViewAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            // Return a NEW fragment instance in createFragment(int)
            return when (position) {
                0 -> MusicRecommendFragment()
                1 -> MusicSearchFragment()
                else -> MusicRecommendFragment()
            }
            // fragment.arguments = Bundle().apply {
            //     // Our object is just an integer :-P
            //     putInt(argObject, position + 1)
            // }
        }
    }

}