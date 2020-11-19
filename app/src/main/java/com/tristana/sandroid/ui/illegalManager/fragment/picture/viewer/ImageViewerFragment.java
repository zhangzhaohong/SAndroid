package com.tristana.sandroid.ui.illegalManager.fragment.picture.viewer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tristana.sandroid.R;
import com.tristana.sandroid.tools.log.Timber;
import com.tristana.customViewLibrary.view.imageView.CustomImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class ImageViewerFragment extends Fragment {

    private ImageViewerViewModel imageViewerViewModel;

    private Timber timber = new Timber("ImageViewerFragment");
    private CustomImageView imageViewer;

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
        imageViewer = root.findViewById(R.id.imageViewer);
        imageViewer.setPlaceHolder(R.drawable.ic_pic_loading);
        imageViewer.setLoadingFailedPlaceHolder(R.drawable.ic_pic_failed);
        imageViewer.loadImageFromUrl(url);
        return root;
    }

    /**
     * Called when the view previously created by {@link #onCreateView} has
     * been detached from the fragment.  The next time the fragment needs
     * to be displayed, a new view will be created.  This is called
     * after {@link #onStop()} and before {@link #onDestroy()}.  It is called
     * <em>regardless</em> of whether {@link #onCreateView} returned a
     * non-null view.  Internally it is called after the view's state has
     * been saved but before it has been removed from its parent.
     */
    @Override
    public void onDestroyView() {
        imageViewer.releaseBitmap();
        super.onDestroyView();
    }
}