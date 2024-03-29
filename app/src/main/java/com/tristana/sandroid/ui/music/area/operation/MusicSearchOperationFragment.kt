package com.tristana.sandroid.ui.music.area.operation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import butterknife.BindView
import butterknife.ButterKnife
import com.therouter.router.Route
import com.tristana.library.tools.toast.ToastUtils
import com.tristana.sandroid.MyApplication
import com.tristana.sandroid.R
import com.tristana.sandroid.ui.music.area.MusicAreaFragment
import com.tristana.sandroid.ui.music.area.MusicAreaViewModel

@Route(path = MusicSearchOperationFragment.ROUTE)
class MusicSearchOperationFragment : Fragment() {

    companion object {
        const val ROUTE = "/app/music/search/operation"
    }

    private var viewModel: MusicSearchOperationViewModel? = null
    private var musicAreaViewModel: MusicAreaViewModel? = MyApplication.viewModelStore[MusicAreaFragment.ROUTE] as MusicAreaViewModel?

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.music_search_operation_name)
    lateinit var musicSearchNameView: AppCompatEditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        if (viewModel == null) viewModel =
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
                .create(
                    MusicSearchOperationViewModel::class.java
                )
        val root = inflater.inflate(R.layout.fragment_music_search_operation, container, false)
        ButterKnife.bind(this, root)
        musicSearchNameView.doOnTextChanged { text, _, _, _ ->
            viewModel?.requestTips(text.toString())
        }
        musicAreaViewModel?.searchMusicName?.value = "我们的18岁"
        musicAreaViewModel?.searchMusicServiceId?.value = 2
        return root
    }

}