package com.tristana.sandroid.ui.illegalManager.fragment.picture;

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
import com.tristana.sandroid.model.illegalManager.CardType;
import com.tristana.sandroid.model.illegalManager.IllegalFileModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

public class IllegalPictureDataAdapter extends RecyclerView.Adapter<IllegalPictureDataAdapter.CustomViewHolder> {

    private Activity activity;
    private ArrayList<IllegalFileModel> data;
    private ArrayList<Bitmap> picList;

    public IllegalPictureDataAdapter(ArrayList<IllegalFileModel> defaultData, ArrayList<Bitmap> picList, Activity activity) {
        this.data = defaultData;
        this.picList = picList;
        this.activity = activity;
    }

    public void setData(ArrayList<IllegalFileModel> data, ArrayList<Bitmap> picList, Activity activity) {
        this.data = data;
        this.picList = picList;
        this.activity = activity;
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
        holder.play1.setVisibility(View.GONE);
        holder.play2.setVisibility(View.GONE);
        holder.pv1.setBackground(new BitmapDrawable(activity.getResources(), picList.get(position * 2)));
        holder.pv2.setBackground(new BitmapDrawable(activity.getResources(), picList.get(position * 2 + 1)));
        holder.text1.setText(data.get(position * 2).getContent());
        holder.text2.setText(data.get(position * 2 + 1).getContent());
        RelativeLayout.LayoutParams pv1LayoutParams = (RelativeLayout.LayoutParams) holder.pv1.getLayoutParams();
        pv1LayoutParams.height = 300;
        holder.pv1.setLayoutParams(pv1LayoutParams);
        RelativeLayout.LayoutParams pv2LayoutParams = (RelativeLayout.LayoutParams) holder.pv2.getLayoutParams();
        pv2LayoutParams.height = 300;
        holder.pv2.setLayoutParams(pv2LayoutParams);
        holder.pv1.setOnClickListener(jumpToViewer(CardType.TYPE_LEFT, position));
        holder.pv2.setOnClickListener(jumpToViewer(CardType.TYPE_RIGHT, position));
    }

    private View.OnClickListener jumpToViewer(final String cardType, final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
                Bundle bundle = new Bundle();
                switch (cardType) {
                    case CardType.TYPE_LEFT:
                        bundle.putString("ImageUrl", data.get(position * 2).getCover());
                        break;
                    case CardType.TYPE_RIGHT:
                        bundle.putString("ImageUrl", data.get(position * 2 + 1).getCover());
                        break;
                }
                navController.navigate(R.id.nav_imageViewer, bundle);
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
