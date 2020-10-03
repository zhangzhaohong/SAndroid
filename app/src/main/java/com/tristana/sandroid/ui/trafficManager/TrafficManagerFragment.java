package com.tristana.sandroid.ui.trafficManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.tristana.sandroid.R;
import com.tristana.sandroid.model.trafficManager.TrafficManagerModel;
import com.tristana.sandroid.tools.log.Timber;
import com.tristana.sandroid.tools.toast.ToastUtils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TrafficManagerFragment extends Fragment {

    private TrafficManagerViewModel trafficManagerViewModel;
    private TrafficManagerDataAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    Timber timber = new Timber("TrafficManagerFragment");

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        trafficManagerViewModel =
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(TrafficManagerViewModel.class);
        View root = inflater.inflate(R.layout.fragment_traffic_manager, container, false);
        trafficManagerViewModel.setNeedStop(false);
        final RecyclerView trafficLightData = (RecyclerView) root.findViewById(R.id.trafficLightData);
        trafficLightData.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(requireActivity());
        trafficLightData.setLayoutManager(layoutManager);
        mAdapter = new TrafficManagerDataAdapter(initData(), requireActivity());
        trafficLightData.setAdapter(mAdapter);
        trafficManagerViewModel.startRequest(requireActivity());
        trafficManagerViewModel.getLightData().observe(getViewLifecycleOwner(), new Observer<ArrayList<TrafficManagerModel>>() {
            @Override
            public void onChanged(ArrayList<TrafficManagerModel> trafficManagerModels) {
                timber.d("Data have changed!");
                mAdapter.setData(trafficManagerModels);
                trafficManagerViewModel.startTimer();
            }
        });
        trafficManagerViewModel.getToast().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                ToastUtils.showToast(requireActivity(), s);
            }
        });
        trafficManagerViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        final AppCompatSpinner trafficManagerSpinner = (AppCompatSpinner) root.findViewById(R.id.trafficManagerSpinner);
        trafficManagerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                timber.d(l + " " + i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        trafficManagerViewModel.getFinishStatus().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean && !trafficManagerViewModel.getNeedStop()) {
                    trafficManagerViewModel.startRequest(requireActivity());
                }
            }
        });
        return root;
    }

    @Override
    public void onStop() {
        timber.d("STOP");
        trafficManagerViewModel.setNeedStop(true);
        super.onStop();
    }

    @Override
    public void onResume() {
        timber.d("RESUME");
        trafficManagerViewModel.setNeedStop(false);
        trafficManagerViewModel.startTimer();
        super.onResume();
    }

    private ArrayList<TrafficManagerModel> initData() {
        ArrayList<TrafficManagerModel> result = new ArrayList<>();
        result.add(new TrafficManagerModel(getString(R.string.title_id), getString(R.string.title_red_duration), getString(R.string.title_yellow_duration), getString(R.string.title_green_duration)));
        return result;
    }

}