package com.tristana.sandroid.ui.illegalManager.fragment.picture.viewer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tristana.sandroid.R;
import com.tristana.sandroid.tools.log.Timber;
import com.tristana.sandroid.tools.text.TextUtils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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
        final ZoomImageView imageViewer = root.findViewById(R.id.imageViewer);
        imageViewer.placeholder(R.drawable.ic_pic_default);
        imageViewerViewModel.getPicList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Bitmap>>() {
            @Override
            public void onChanged(ArrayList<Bitmap> bitmaps) {
                imageViewer.setImageBitmap(bitmaps.get(0));
            }
        });
        if (TextUtils.checkEmpty(url)) {
            imageViewerViewModel.startGetPic(url);
        }
        return root;
    }

}