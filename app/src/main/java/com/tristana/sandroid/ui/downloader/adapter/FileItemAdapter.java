package com.tristana.sandroid.ui.downloader.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.arialyy.aria.core.download.DownloadEntity;
import com.blankj.utilcode.util.LogUtils;
import com.tristana.sandroid.R;
import com.tristana.sandroid.ui.downloader.DownloadStateEnums;

import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * @author koala
 * @version 1.0
 * @date 2022/4/17 13:08
 * @description
 */
public class FileItemAdapter extends RecyclerView.Adapter<FileItemAdapter.FileItemHolder> {

    private final List<DownloadEntity> fileInfoList;
    private final Context context;
    private AppCompatTextView file_name;
    private AppCompatTextView file_type;
    private AppCompatTextView task_status;
    private AppCompatTextView file_size;

    public FileItemAdapter(Context context, List<DownloadEntity> fileInfoList) {
        this.context = context;
        Collections.reverse(fileInfoList);
        this.fileInfoList = fileInfoList;
    }

    @NonNull
    @Override
    public FileItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = View.inflate(parent.getContext(), R.layout.item_downloader_view, null);
        return new FileItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FileItemHolder holder, int position) {
        holder.setData(fileInfoList.get(position));
    }

    @Override
    public int getItemCount() {
        if (fileInfoList != null) {
            return fileInfoList.size();
        }
        return 0;
    }

    public void insertView(@Nullable DownloadEntity entity) {
        this.fileInfoList.add(0, entity);
    }

    class FileItemHolder extends RecyclerView.ViewHolder {
        public FileItemHolder(@NonNull View itemView) {
            super(itemView);
            file_name = itemView.findViewById(R.id.file_name);
            file_type = itemView.findViewById(R.id.file_type);
            task_status = itemView.findViewById(R.id.task_status);
            file_size = itemView.findViewById(R.id.file_size);
        }

        public void setData(DownloadEntity downloadEntity) {
            file_name.setText(downloadEntity.getFileName());
            file_type.setText(getFileTypeByDownloadPath(downloadEntity));
            String status = DownloadStateEnums.getMsgByNum(downloadEntity.getState());
            if (status != null && !status.trim().equals("")) {
                task_status.setText(status);
                refreshTaskTag(task_status, downloadEntity);
            }
            String fileSize = downloadEntity.getConvertFileSize();
            GradientDrawable gradientDrawable = (GradientDrawable) file_size.getBackground();
            gradientDrawable.setColor(context.getResources().getColor(R.color.tag_483D8B));
            if (fileSize != null && !fileSize.equals("")) {
                file_size.setText(fileSize);
                file_size.setVisibility(View.VISIBLE);
            } else {
                file_size.setVisibility(View.GONE);
            }
        }

        private void refreshTaskTag(AppCompatTextView taskStatus, DownloadEntity downloadEntity) {
            GradientDrawable gradientDrawable = (GradientDrawable) taskStatus.getBackground();
            switch (downloadEntity.getState()) {
                case 0:
                    gradientDrawable.setColor(Color.RED);
                    break;
                case 1:
                    gradientDrawable.setColor(context.getResources().getColor(R.color.green_006400));
                    break;
                case 2:
                    gradientDrawable.setColor(context.getResources().getColor(R.color.yellow_EE7942));
                    break;
                case 3:
                    gradientDrawable.setColor(context.getResources().getColor(R.color.gray_4A708B));
                    break;
                case 4:
                    gradientDrawable.setColor(context.getResources().getColor(R.color.blue_008B8B));
                    break;
                case 5:
                    gradientDrawable.setColor(context.getResources().getColor(R.color.red_8B636C));
                    break;
                case 6:
                    gradientDrawable.setColor(context.getResources().getColor(R.color.red_8B475D));
                    break;
                case 7:
                    gradientDrawable.setColor(context.getResources().getColor(R.color.gray_555555));
                    break;
                default:
                    break;
            }
        }

        private String getFileTypeByDownloadPath(DownloadEntity downloadEntity) {
            String[] data = downloadEntity.getFilePath().split("\\.");
            return data[data.length - 1];
        }
    }

}
