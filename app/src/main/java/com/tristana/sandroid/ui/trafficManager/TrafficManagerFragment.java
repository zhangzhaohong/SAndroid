package com.tristana.sandroid.ui.trafficManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.tristana.sandroid.R;
import com.tristana.sandroid.model.trafficManager.TrafficManagerModel;
import com.tristana.sandroid.tools.log.Timber;

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
        final RecyclerView trafficLightData = (RecyclerView) root.findViewById(R.id.trafficLightData);
        trafficLightData.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(requireActivity());
        trafficLightData.setLayoutManager(layoutManager);
        mAdapter = new TrafficManagerDataAdapter(testData(), requireActivity());
        trafficLightData.setAdapter(mAdapter);
        return root;
    }

    private ArrayList<TrafficManagerModel> testData() {
        ArrayList<TrafficManagerModel> result = new ArrayList<>();
        result.add(new TrafficManagerModel(getString(R.string.title_id), getString(R.string.title_red_duration), getString(R.string.title_yellow_duration), getString(R.string.title_green_duration)));
        result.add(new TrafficManagerModel("111111", "200", "300", "400"));
        return result;
    }
}