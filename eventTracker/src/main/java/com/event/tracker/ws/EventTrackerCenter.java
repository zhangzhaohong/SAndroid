package com.event.tracker.ws;

import android.app.Application;
import android.widget.Toast;

import com.event.tracker.ws.connector.EventTrackerWebSocketBackgroundService;
import com.event.tracker.ws.connector.EventTrackerWebSocketInitUtils;

public class EventTrackerCenter {
    public static Boolean status = false;
    /**
     * IM启动
     */
    public static void EventTrackerStart(Application application) {
        //开启IM webSocket 接收私信消息
        EventTrackerWebSocketInitUtils.getInstance().init();
        status = true;
        Toast.makeText(application, "开启长链接", Toast.LENGTH_SHORT).show();
    }

    /**
     * IM停止
     */
    public static void EventTrackerStop(Application application) {
        EventTrackerWebSocketBackgroundService.getInstance(application).stopHeartBeat();
        status = false;
        Toast.makeText(application, "停止长链接", Toast.LENGTH_SHORT).show();
    }
}