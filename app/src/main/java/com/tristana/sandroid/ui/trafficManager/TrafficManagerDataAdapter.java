package com.tristana.sandroid.ui.trafficManager;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.tristana.sandroid.R;
import com.tristana.sandroid.model.trafficManager.TrafficManagerModel;
import com.tristana.sandroid.tools.log.Timber;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

public class TrafficManagerDataAdapter extends RecyclerView.Adapter<TrafficManagerDataAdapter.CustomViewHolder> {

    private ArrayList<TrafficManagerModel> trafficManagerModel;

    private Timber timber;

    private Context context;

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        public AppCompatTextView roadId;
        public AppCompatTextView roadRedLightDuration;
        public AppCompatTextView roadYellowLightDuration;
        public AppCompatTextView roadGreenLightDuration;
        public LinearLayoutCompat linearLayoutCompat;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayoutCompat = (LinearLayoutCompat) itemView;
            roadId = (AppCompatTextView) itemView.findViewById(R.id.roadId);
            roadRedLightDuration = (AppCompatTextView) itemView.findViewById(R.id.roadRedLightDuration);
            roadYellowLightDuration = (AppCompatTextView) itemView.findViewById(R.id.roadYellowLightDuration);
            roadGreenLightDuration = (AppCompatTextView) itemView.findViewById(R.id.roadGreenLightDuration);
        }
    }

    public TrafficManagerDataAdapter(ArrayList<TrafficManagerModel> trafficManagerModel, Context context) {
        this.trafficManagerModel = trafficManagerModel;
        this.timber = new Timber("TrafficManagerDataAdapter");
        this.context = context;
        timber.d("TrafficManagerDataAdapter init!");
    }

    @NonNull
    @Override
    public TrafficManagerDataAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        timber.d("Start to inflate!" + viewType);
        LinearLayoutCompat linearLayoutCompat;
        linearLayoutCompat = (LinearLayoutCompat) LayoutInflater.from(parent.getContext()).inflate(R.layout.include_traffic_data_list, parent, false);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayoutCompat.setLayoutParams(lp);
        return new CustomViewHolder(linearLayoutCompat);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull TrafficManagerDataAdapter.CustomViewHolder holder, int position) {
        timber.d("position " + position);
        TrafficManagerModel data = trafficManagerModel.get(position);
        holder.roadId.setText(data.getId());
        holder.roadRedLightDuration.setText(data.getRedDuration());
        holder.roadYellowLightDuration.setText(data.getYellowDuration());
        holder.roadGreenLightDuration.setText(data.getGreenDuration());
        if (position <= 0) {
            holder.linearLayoutCompat.setBackgroundColor(context.getColor(R.color.colorTitleBg));
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return trafficManagerModel.size();
    }
}
