package com.tristana.sandroid.ui.illegalManager.fragment.picture;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tristana.sandroid.R;
import com.tristana.sandroid.model.illegalManager.IllegalFileModel;
import com.tristana.sandroid.model.illegalManager.PageType;
import com.tristana.sandroid.tools.log.Timber;
import com.tristana.sandroid.tools.toast.ToastUtils;
import com.tristana.sandroid.ui.illegalManager.fragment.IllegalDataAdapter;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class IllegalPictureFragment extends Fragment {

    private IllegalPictureViewModel illegalPictureViewModel;
    private IllegalDataAdapter mAdapter;
    private GridLayoutManager gridLayoutManager;
    private Boolean hasLoad = false;

    private Timber timber = new Timber("IllegalPictureFragment");

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (illegalPictureViewModel == null)
            illegalPictureViewModel =
                    ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(IllegalPictureViewModel.class);
        else
            hasLoad = true;
        View root = inflater.inflate(R.layout.fragment_illegal_picture, container, false);
        RecyclerView illegalPicData = root.findViewById(R.id.illegalPicData);
        gridLayoutManager = new GridLayoutManager(requireActivity(), 2);
        illegalPicData.setLayoutManager(gridLayoutManager);
        ArrayList<IllegalFileModel> defaultData = new ArrayList<>();
        ArrayList<Bitmap> picList = new ArrayList<>();
        mAdapter = new IllegalDataAdapter(defaultData, picList, requireActivity(), PageType.TYPE_PIC);
        illegalPicData.setAdapter(mAdapter);
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
                if (!hasLoad)
                    illegalPictureViewModel.startGetPic();
            }
        });
        illegalPictureViewModel.getPicList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Bitmap>>() {
            @Override
            public void onChanged(ArrayList<Bitmap> bitmaps) {
                mAdapter.setData(illegalPictureViewModel.getFileList().getValue(), bitmaps, requireActivity(), PageType.TYPE_PIC);
            }
        });
        if (!hasLoad)
            illegalPictureViewModel.startRequest();
        return root;
    }
}