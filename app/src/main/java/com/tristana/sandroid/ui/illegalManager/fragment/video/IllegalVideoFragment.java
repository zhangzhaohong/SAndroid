package com.tristana.sandroid.ui.illegalManager.fragment.video;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tristana.sandroid.R;
import com.tristana.sandroid.model.illegalManager.IllegalFileModel;
import com.tristana.sandroid.tools.log.Timber;
import com.tristana.sandroid.tools.toast.ToastUtils;
import com.tristana.sandroid.ui.illegalManager.fragment.picture.IllegalPictureDataAdapter;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class IllegalVideoFragment extends Fragment {

    private IllegalVideoViewModel illegalVideoViewModel;
    private IllegalVideoDataAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private Timber timber = new Timber("IllegalVideoFragment");

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        illegalVideoViewModel =
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(IllegalVideoViewModel.class);
        View root = inflater.inflate(R.layout.fragment_illegal_video, container, false);
        RecyclerView illegalVidData = root.findViewById(R.id.illegalVidData);
        layoutManager = new LinearLayoutManager(requireActivity());
        illegalVidData.setLayoutManager(layoutManager);
        ArrayList<IllegalFileModel> defaultData = new ArrayList<>();
        ArrayList<Bitmap> picList = new ArrayList<>();
        mAdapter = new IllegalVideoDataAdapter(defaultData, picList, requireActivity());
        illegalVidData.setAdapter(mAdapter);
        illegalVideoViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        illegalVideoViewModel.getToast().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                ToastUtils.showToast(requireActivity(), s);
            }
        });
        illegalVideoViewModel.getFileList().observe(getViewLifecycleOwner(), new Observer<ArrayList<IllegalFileModel>>() {
            @Override
            public void onChanged(ArrayList<IllegalFileModel> illegalFileModels) {
                timber.d("Data changed!" + illegalFileModels.size());
                illegalVideoViewModel.startGetPic();
//                mAdapter.setData(illegalFileModels, illegalPictureViewModel.getPicList().getValue(), requireActivity());
            }
        });
        illegalVideoViewModel.getPicList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Bitmap>>() {
            @Override
            public void onChanged(ArrayList<Bitmap> bitmaps) {
                mAdapter.setData(illegalVideoViewModel.getFileList().getValue(), bitmaps, requireActivity());
            }
        });
        illegalVideoViewModel.startRequest();
        return root;
    }
}