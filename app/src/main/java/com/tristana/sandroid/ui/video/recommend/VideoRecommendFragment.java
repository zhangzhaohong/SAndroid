package com.tristana.sandroid.ui.video.recommend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.tristana.sandroid.R;

public class VideoRecommendFragment extends Fragment {

    private VideoRecommendViewModel galleryViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (galleryViewModel == null)
            galleryViewModel =
                    ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(VideoRecommendViewModel.class);
        View root = inflater.inflate(R.layout.fragment_video_recommend, container, false);
        return root;
    }
}