package com.tristana.sandroid.ui.illegalManager.fragment.picture;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tristana.sandroid.R;
import com.tristana.sandroid.model.illegalManager.IllegalFileModel;
import com.tristana.sandroid.tools.log.Timber;
import com.tristana.sandroid.tools.toast.ToastUtils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class IllegalPictureFragment extends Fragment {

    private IllegalPictureViewModel illegalPictureViewModel;
    private IllegalPictureDataAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private Timber timber = new Timber("IllegalPictureFragment");

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        illegalPictureViewModel =
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(IllegalPictureViewModel.class);
        View root = inflater.inflate(R.layout.fragment_illegal_picture, container, false);
        RecyclerView illegalPicData = root.findViewById(R.id.illegalPicData);
        layoutManager = new LinearLayoutManager(requireActivity());
        illegalPicData.setLayoutManager(layoutManager);
        ArrayList<IllegalFileModel> defaultData = new ArrayList<>();
        ArrayList<Bitmap> picList = new ArrayList<>();
        mAdapter = new IllegalPictureDataAdapter(defaultData, picList, requireActivity());
        illegalPicData.setAdapter(mAdapter);
        illegalPictureViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        illegalPictureViewModel.getToast().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                ToastUtils.showToast(requireActivity(), s);
            }
        });
        illegalPictureViewModel.getFileList().observe(getViewLifecycleOwner(), new Observer<ArrayList<IllegalFileModel>>() {
            @Override
            public void onChanged(ArrayList<IllegalFileModel> illegalFileModels) {
                timber.d("Data changed!" + illegalFileModels.size());
                illegalPictureViewModel.startGetPic();
//                mAdapter.setData(illegalFileModels, illegalPictureViewModel.getPicList().getValue(), requireActivity());
            }
        });
        illegalPictureViewModel.getPicList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Bitmap>>() {
            @Override
            public void onChanged(ArrayList<Bitmap> bitmaps) {
                mAdapter.setData(illegalPictureViewModel.getFileList().getValue(), bitmaps, requireActivity());
            }
        });
        illegalPictureViewModel.startRequest();
        return root;
    }
}