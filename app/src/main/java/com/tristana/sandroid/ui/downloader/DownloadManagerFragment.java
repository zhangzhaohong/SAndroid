package com.tristana.sandroid.ui.downloader;

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

public class DownloadManagerFragment extends Fragment {

    private DownloadManagerViewModel downloadManagerViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (downloadManagerViewModel == null)
            downloadManagerViewModel =
                    ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(DownloadManagerViewModel.class);
        View root = inflater.inflate(R.layout.fragment_download_manager, container, false);
        final TextView textView = root.findViewById(R.id.text_download_manager);
        downloadManagerViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}