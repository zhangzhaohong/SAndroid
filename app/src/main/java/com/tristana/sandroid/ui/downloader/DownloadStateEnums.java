package com.tristana.sandroid.ui.downloader;

import android.os.Build;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author koala
 * @version 1.0
 * @date 2022/4/17 14:48
 * @description
 */
public enum DownloadStateEnums {
    ON_FAILED(0, "下载失败"),
    ON_SUCCESS(1, "下载成功"),
    ON_PAUSED(2, "下载暂停"),
    WAITING_DOWNLOAD(3, "等待下载"),
    DOWNLOADING(4, "正在下载"),
    PREFETCH(5, "预处理"),
    PREFETCH_SUCCESS(6, "预处理完成"),
    ON_CANCEL(7, "任务取消"),
    ;

    private final int num;
    private final String msg;

    public int getNum() {
        return num;
    }

    public String getMsg() {
        return msg;
    }

    DownloadStateEnums(int num, String msg) {
        this.num = num;
        this.msg = msg;
    }

    public static String getMsgByNum(int num) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Optional<DownloadStateEnums> optional = Arrays.stream(DownloadStateEnums.values()).filter(item -> item.num == num).findFirst();
            if (optional.isPresent()) {
                return optional.get().getMsg();
            }
        } else {
            DownloadStateEnums[] values = DownloadStateEnums.values();
            for (DownloadStateEnums value : values) {
                if (value.getNum() == num) {
                    return value.getMsg();
                }
            }
        }
        return null;
    }
}
