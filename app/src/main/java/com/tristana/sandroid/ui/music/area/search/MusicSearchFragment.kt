package com.tristana.sandroid.ui.music.area.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import butterknife.BindView
import butterknife.ButterKnife
import com.blankj.utilcode.util.ToastUtils
import com.tristana.library.view.editTextView.CustomEditTextView
import com.tristana.sandroid.FragmentDirector
import com.tristana.sandroid.MyApplication
import com.tristana.sandroid.R
import com.tristana.sandroid.ui.music.area.MusicAreaFragment
import com.tristana.sandroid.ui.music.area.MusicAreaViewModel
import com.tristana.sandroid.ui.music.area.operation.MusicSearchOperationFragment


class MusicSearchFragment : Fragment() {

    private var viewModel: MusicSearchViewModel? = null
    private var musicAreaViewModel: MusicAreaViewModel? = MyApplication.viewModelStore[MusicAreaFragment.ROUTE] as MusicAreaViewModel?

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.music_search_content)
    lateinit var searchContentView: CustomEditTextView

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.music_search_test_1)
    lateinit var test1: AppCompatButton

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.music_search_test_2)
    lateinit var test2: AppCompatButton

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        if (viewModel == null) viewModel =
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
                .create(
                    MusicSearchViewModel::class.java
                )
        val root = inflater.inflate(R.layout.fragment_music_search, container, false)
        ButterKnife.bind(this, root)
        val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        searchContentView.initParameter(
            R.drawable.ic_music_search,
            InputType.TYPE_CLASS_TEXT,
            0,
            1,
            "请输入名称",
            0,
            0,
            false,
            R.drawable.ic_clear,
            false
        )
        searchContentView.getEditTextView().let {
            it.setOnClickListener {
                FragmentDirector.doDirect(navController, MusicSearchOperationFragment.ROUTE, null)
            }
            it.isFocusableInTouchMode = false
        }
        test1.setOnClickListener {
            searchContentView.updateIconResId(R.drawable.ic_music_kugou)
        }
        test2.setOnClickListener {
            searchContentView.updateIconResId(R.drawable.ic_music_netease)
        }
        return root
    }

    override fun onResume() {
        super.onResume()
        searchContentView.updateText(musicAreaViewModel?.searchMusicName?.value)
    }

}