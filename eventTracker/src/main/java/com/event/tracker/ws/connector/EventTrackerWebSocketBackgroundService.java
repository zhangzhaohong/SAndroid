package com.event.tracker.ws.connector;

import android.content.Context;
import com.blankj.utilcode.util.LogUtils;

import com.event.tracker.TrackerInstance;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EventTrackerWebSocketBackgroundService extends IEventTrackerWebSocketPage implements Runnable {
    private static final int SEND_MESSAGE = 601;  //发送消息
    private static final int DISCONNECT_MESSAGE = 602; //断开连接

    private static EventTrackerWebSocketBackgroundService instance;
    private WebSocketServiceManager webSocketServiceManager;
    private ScheduledExecutorService scheduler;
    private final Context context;
    //发送数
    private int sendGeneration;
    //是否运行心跳
    private boolean runHeartBeat = false;


    /**
     * 初始化，非appContext
     */
    public static void initAndStartHeartBeat(Context context) {
        getInstance(context).onCreate();
    }


    /**
     * 获取单例，非appContext,要先init
     */
    public static EventTrackerWebSocketBackgroundService getInstance(Context context) {
        if (instance == null) {
            synchronized (EventTrackerWebSocketBackgroundService.class) {
                if (instance == null) {
                    instance = new EventTrackerWebSocketBackgroundService(context);
                }
            }
        }
        return instance;
    }

    private EventTrackerWebSocketBackgroundService(Context context) {
        this.context = context;
    }

    private void onCreate() {
        if (webSocketServiceManager == null) {
            webSocketServiceManager = new WebSocketServiceManager(context, this);
            webSocketServiceManager.bindService();
            sendGeneration = 0;
        }
    }

    //开始心跳
    public void startHeartBeat() {
        LogUtils.e(WebSocketThread.TAG, "启动心跳");
        runHeartBeat = true;
        if (scheduler == null) {
            scheduler = Executors.newScheduledThreadPool(1);
            scheduler.schedule(this, getDelay(), TimeUnit.SECONDS);
        }
    }

    //停止心跳
    public void stopHeartBeat() {
        runHeartBeat = false;
        if (webSocketServiceManager != null) {
            webSocketServiceManager.unbindService();
        }
        instance = null;
        webSocketServiceManager = null;
    }


    @Override
    public boolean sendText(String text) {
        if (runHeartBeat) {
            return webSocketServiceManager.sendText(text);
        } else return false;
    }

    @Override
    public void onMessageReceive(String jsonText) {
        try {
            JSONObject jsonObject = new JSONObject(jsonText);
            LogUtils.i(WebSocketThread.TAG, jsonObject + "");

            if (jsonObject.has("msgType") && Integer.valueOf(jsonObject.getInt("msgType")) != null) {
                int msgType = jsonObject.getInt("msgType");
                if (msgType == SEND_MESSAGE) {     //发送消息
                    String msg = jsonObject.getString("msg");
                    LogUtils.d(WebSocketThread.TAG, msg);
                } else if (msgType == DISCONNECT_MESSAGE) {     //断开连接
                    stopHeartBeat();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connection() {
        LogUtils.e(WebSocketThread.TAG, "连接成功");
        startHeartBeat();
    }

    @Override
    public void run() {
        if (runHeartBeat) {
            if (sendText(defineText())) {
                sendGeneration++;
                LogUtils.d(WebSocketThread.TAG, "发送心跳成功" + sendGeneration);
            }
            scheduler.schedule(this, getDelay(), TimeUnit.SECONDS);
        }
    }

    private String defineText() {
        return String.format("{\"data\":{\"userId\":%s},\"event\":\"ping\"}", TrackerInstance.Companion.get().getTrackerId());
    }

    private int getDelay() {
        return 10;
    }

}
