package com.tristana.sandroid.ui.illegalManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tristana.sandroid.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class IllegalManagerFragment extends Fragment {

    private IllegalManagerViewModel galleryViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(IllegalManagerViewModel.class);
        View root = inflater.inflate(R.layout.fragment_illegal_manager, container, false);
        final TextView textView = root.findViewById(R.id.text_IllegalManager);
        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}