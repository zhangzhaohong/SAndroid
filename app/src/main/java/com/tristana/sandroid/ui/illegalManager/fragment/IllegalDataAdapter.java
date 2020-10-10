package com.tristana.sandroid.ui.illegalManager.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.tristana.sandroid.R;
import com.tristana.sandroid.model.illegalManager.IllegalFileModel;
import com.tristana.sandroid.model.illegalManager.PageType;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

public class IllegalDataAdapter extends RecyclerView.Adapter<IllegalDataAdapter.CustomViewHolder> {

    private Activity activity;
    private ArrayList<IllegalFileModel> data;
    private ArrayList<Bitmap> picList;
    private FragmentManager supportFragmentManager;
    private String type;

    public IllegalDataAdapter(ArrayList<IllegalFileModel> defaultData, ArrayList<Bitmap> picList, Activity activity, String type) {
        this.data = defaultData;
        this.picList = picList;
        this.activity = activity;
        this.type = type;
    }

    public void setData(ArrayList<IllegalFileModel> data, ArrayList<Bitmap> picList, Activity activity, String type) {
        this.data = data;
        this.picList = picList;
        this.activity = activity;
        this.type = type;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayoutCompat linearLayoutCompat;
        linearLayoutCompat = (LinearLayoutCompat) LayoutInflater.from(parent.getContext()).inflate(R.layout.include_illegal_data_list, parent, false);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayoutCompat.setLayoutParams(lp);
        return new CustomViewHolder(linearLayoutCompat);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        if (type.equals(PageType.TYPE_VID))
            holder.play1.setVisibility(View.VISIBLE);
        else
            holder.play1.setVisibility(View.GONE);
        holder.pv1.setBackground(new BitmapDrawable(activity.getResources(), picList.get(position)));
        holder.text1.setText(data.get(position).getContent());
        RelativeLayout.LayoutParams pv1LayoutParams = (RelativeLayout.LayoutParams) holder.pv1.getLayoutParams();
        switch (type) {
            case PageType.TYPE_PIC:
                pv1LayoutParams.height = 300;
                break;
            case PageType.TYPE_VID:
                pv1LayoutParams.height = 700;
                break;
        }
        holder.pv1.setLayoutParams(pv1LayoutParams);
        holder.play1.setOnClickListener(jumpToPlayer(position));
        holder.pv1.setOnClickListener(jumpToPlayer(position));
    }

    private View.OnClickListener jumpToPlayer(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
                Bundle bundle = new Bundle();
                switch (type) {
                    case PageType.TYPE_PIC:
                        bundle.putString("ImageUrl", data.get(position).getCover());
                        navController.navigate(R.id.nav_imageViewer, bundle);
                        break;
                    case PageType.TYPE_VID:
                        bundle.putString("VideoUrl", data.get(position).getFile());
                        navController.navigate(R.id.nav_videoPlayer, bundle);
                        break;
                }
            }
        };
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        private AppCompatImageView play1;
        private AppCompatImageView pv1;
        private AppCompatTextView text1;
        private LinearLayoutCompat linearLayoutCompat;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayoutCompat = (LinearLayoutCompat) itemView;
            play1 = itemView.findViewById(R.id.play1);
            pv1 = itemView.findViewById(R.id.pv1);
            text1 = itemView.findViewById(R.id.text1);
        }
    }

}
