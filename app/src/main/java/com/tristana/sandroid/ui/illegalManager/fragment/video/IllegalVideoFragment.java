package com.tristana.sandroid.ui.illegalManager.fragment.video;

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

public class IllegalVideoFragment extends Fragment {

    private IllegalVideoViewModel illegalVideoViewModel;
    private IllegalDataAdapter mAdapter;
    private GridLayoutManager gridLayoutManager;
    private Boolean hasLoad = false;

    private Timber timber = new Timber("IllegalVideoFragment");

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (illegalVideoViewModel == null)
            illegalVideoViewModel =
                    ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(IllegalVideoViewModel.class);
        else
            hasLoad = true;
        View root = inflater.inflate(R.layout.fragment_illegal_video, container, false);
        RecyclerView illegalVidData = root.findViewById(R.id.illegalVidData);
        gridLayoutManager = new GridLayoutManager(requireActivity(), 2);
        illegalVidData.setLayoutManager(gridLayoutManager);
        ArrayList<IllegalFileModel> defaultData = new ArrayList<>();
        ArrayList<Bitmap> picList = new ArrayList<>();
        mAdapter = new IllegalDataAdapter(defaultData, picList, requireActivity(), PageType.TYPE_VID);
        illegalVidData.setAdapter(mAdapter);
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
                if (!hasLoad)
                    illegalVideoViewModel.startGetPic();
            }
        });
        illegalVideoViewModel.getPicList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Bitmap>>() {
            @Override
            public void onChanged(ArrayList<Bitmap> bitmaps) {
                mAdapter.setData(illegalVideoViewModel.getFileList().getValue(), bitmaps, requireActivity(), PageType.TYPE_VID);
            }
        });
        if (!hasLoad)
            illegalVideoViewModel.startRequest();
        return root;
    }
}