package com.event.tracker;

/**
 * @author koala
 * @version 1.0
 * @date 2023/5/31 14:02
 * @description
 */
public class TrackerInstance {

    private volatile static TrackerInstance trackerInstance;

    private TrackerInstance() {
    }

    public static TrackerInstance getInstance() {
        if (trackerInstance == null) {
            synchronized (TrackerInstance.class) {
                if (trackerInstance == null) {
                    trackerInstance = new TrackerInstance();
                }
            }
        }
        return trackerInstance;
    }
}
