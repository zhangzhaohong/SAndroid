package com.event.tracker.ws.connector;

import com.event.tracker.TrackerInstance;

public class EventTrackerWebSocketInitUtils {
    private static EventTrackerWebSocketInitUtils instance;

    private EventTrackerWebSocketInitUtils() {
    }

    /**
     * 获取单例，非appContext,要先init
     */
    public static EventTrackerWebSocketInitUtils getInstance() {
        if (instance == null) {
            synchronized (EventTrackerWebSocketInitUtils.class) {
                if (instance == null) {
                    instance = new EventTrackerWebSocketInitUtils();
                }
            }
        }
        return instance;
    }

    public void init() {
        EventTrackerWebSocketBackgroundService.initAndStartHeartBeat(TrackerInstance.Companion.get().getApplication());
    }
}
