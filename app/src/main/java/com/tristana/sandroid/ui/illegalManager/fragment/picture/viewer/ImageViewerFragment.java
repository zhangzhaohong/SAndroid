package com.tristana.sandroid.ui.illegalManager.fragment.picture.viewer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tristana.sandroid.R;
import com.tristana.sandroid.tools.log.Timber;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class ImageViewerFragment extends Fragment {

    private ImageViewerViewModel imageViewerViewModel;

    private Timber timber = new Timber("ImageViewerFragment");

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        imageViewerViewModel =
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(ImageViewerViewModel.class);
        View root = inflater.inflate(R.layout.fragment_image_viewer, container, false);
        String url = "";
        Bundle bundle = getArguments();
        if (bundle != null) {
            url = bundle.getString("ImageUrl");
        }
        final CustomImageView imageViewer = root.findViewById(R.id.imageViewer);
        imageViewer.setPlaceHolder(R.drawable.ic_pic_loading);
        imageViewer.setLoadingFailedPlaceHolder(R.drawable.ic_pic_failed);
        imageViewer.loadImageFromUrl(url);
        return root;
    }

}