package com.tristana.sandroid.ui.video.area

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
import com.tristana.sandroid.MyApplication
import com.tristana.sandroid.R
import com.tristana.sandroid.ui.video.area.recommend.VideoRecommendFragment
import com.tristana.sandroid.ui.video.area.resolver.VideoResolverFragment

class VideoAreaFragment : Fragment() {

    companion object {
        const val ROUTE = "/app/video/area"
    }

    private var viewModel: VideoAreaViewModel? = null

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.video_area_tab_view)
    lateinit var tabView: TabLayout

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.video_area_container_view)
    lateinit var containerView: ViewPager2
    private lateinit var videoAreaViewAdapter: FragmentStateAdapter

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            tabView.visibility = when (position) {
                0 -> View.VISIBLE
                1 -> View.GONE
                else -> View.VISIBLE
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        if (viewModel == null) viewModel =
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
                .create(
                    VideoAreaViewModel::class.java
                )
        viewModel?.let {
            MyApplication.viewModelStore.put(ROUTE, it)
        }
        videoAreaViewAdapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 2

            override fun createFragment(position: Int): Fragment {
                // Return a NEW fragment instance in createFragment(int)
                return when (position) {
                    0 -> VideoResolverFragment()
                    1 -> VideoRecommendFragment()
                    else -> VideoResolverFragment()
                }
                // fragment.arguments = Bundle().apply {
                //     // Our object is just an integer :-P
                //     putInt(argObject, position + 1)
                // }
            }
        }
        val root = inflater.inflate(R.layout.fragment_video_area, container, false)
        ButterKnife.bind(this, root)
        containerView.adapter = videoAreaViewAdapter
        containerView.isUserInputEnabled = true
        TabLayoutMediator(tabView, containerView) { tabItem, position ->
            tabItem.text = when (position) {
                0 -> requireContext().resources.getString(R.string.title_video_area_search)
                1 -> requireContext().resources.getString(R.string.title_video_area_recommend)
                else -> requireContext().resources.getString(R.string.title_video_area_search)
            }
        }.attach()
        containerView.registerOnPageChangeCallback(onPageChangeCallback)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        containerView.unregisterOnPageChangeCallback(onPageChangeCallback)
    }
}