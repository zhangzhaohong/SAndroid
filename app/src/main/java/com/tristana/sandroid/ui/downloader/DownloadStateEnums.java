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
    ON_QUEUED(1, "已加入队列"),
    ON_DOWNLOADING(2, "下载中"),
    ON_PAUSED(3, "下载暂停"),
    ON_COMPLETED(4, "下载完成"),
    ON_CANCELLED(5, "下载取消"),
    ON_FAILED(6, "下载失败"),
    ON_REMOVED(7, "任务已移除"),
    ON_DELETED(8, "任务已删除"),
    ON_ADDED(9, "任务已添加"),
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

    public static String getMsgByNum(Integer num) {
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
