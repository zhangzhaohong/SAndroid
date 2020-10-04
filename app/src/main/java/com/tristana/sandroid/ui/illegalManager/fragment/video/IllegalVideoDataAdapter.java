package com.tristana.sandroid.ui.illegalManager.fragment.video;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.tristana.sandroid.R;
import com.tristana.sandroid.model.illegalManager.IllegalFileModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

public class IllegalVideoDataAdapter extends RecyclerView.Adapter<IllegalVideoDataAdapter.CustomViewHolder> {

    private Context context;
    private ArrayList<IllegalFileModel> data;
    private ArrayList<Bitmap> picList;

    public IllegalVideoDataAdapter(ArrayList<IllegalFileModel> defaultData, ArrayList<Bitmap> picList, Context context) {
        this.data = defaultData;
        this.picList = picList;
        this.context = context;
    }

    public void setData(ArrayList<IllegalFileModel> data, ArrayList<Bitmap> picList, Context context) {
        this.data = data;
        this.picList = picList;
        this.context = context;
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
        holder.play1.setVisibility(View.VISIBLE);
        holder.play2.setVisibility(View.VISIBLE);
        holder.pv1.setBackground(new BitmapDrawable(context.getResources(), picList.get(position * 2)));
        holder.pv2.setBackground(new BitmapDrawable(context.getResources(), picList.get(position * 2 + 1)));
        holder.text1.setText(data.get(position * 2).getContent());
        holder.text2.setText(data.get(position * 2 + 1).getContent());
        RelativeLayout.LayoutParams pv1LayoutParams = (RelativeLayout.LayoutParams) holder.pv1.getLayoutParams();
        pv1LayoutParams.height = 700;
        holder.pv1.setLayoutParams(pv1LayoutParams);
        RelativeLayout.LayoutParams pv2LayoutParams = (RelativeLayout.LayoutParams) holder.pv2.getLayoutParams();
        pv2LayoutParams.height = 700;
        holder.pv2.setLayoutParams(pv2LayoutParams);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return data.size() / 2;
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        private AppCompatImageView play1;
        private AppCompatImageView play2;
        private AppCompatImageView pv1;
        private AppCompatImageView pv2;
        private AppCompatTextView text1;
        private AppCompatTextView text2;
        private LinearLayoutCompat linearLayoutCompat;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayoutCompat = (LinearLayoutCompat) itemView;
            play1 = itemView.findViewById(R.id.play1);
            play2 = itemView.findViewById(R.id.play2);
            pv1 = itemView.findViewById(R.id.pv1);
            pv2 = itemView.findViewById(R.id.pv2);
            text1 = itemView.findViewById(R.id.text1);
            text2 = itemView.findViewById(R.id.text2);
        }
    }

}
