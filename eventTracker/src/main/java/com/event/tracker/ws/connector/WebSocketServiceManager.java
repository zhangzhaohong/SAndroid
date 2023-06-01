package com.event.tracker.ws.connector;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.blankj.utilcode.util.LogUtils;

public class WebSocketServiceManager {

    private final Context context;
    private EventTrackerWebSocketService eventTrackerWebSocketService;
    private boolean serviceBindSuccess = false;    //绑定成功
    private boolean binding = false;            //是否正在绑定
    private int bindTime = 0;       //重复连接次数
    private final WebSocketResultListener socketResultListener;

    public WebSocketServiceManager(Context context, WebSocketResultListener socketResultListener) {
        this.context = context;
        this.socketResultListener = socketResultListener;
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtils.e(WebSocketThread.TAG, "WebSocketService 已经连接");
            serviceBindSuccess = true;
            binding = false;
            bindTime = 0;   //连接成功后归零
            eventTrackerWebSocketService = ((EventTrackerWebSocketService.WebSocketBinder) service).getWebSocketService();
            eventTrackerWebSocketService.addSocketListener(socketResultListener);
            if (eventTrackerWebSocketService.isSendConnected()) {
                if (socketResultListener != null) {
                    socketResultListener.connection();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {     //service异常被关闭 回调该方法
            binding = false;
            serviceBindSuccess = false;
            if (bindTime < 5 && !binding) {
                LogUtils.e(WebSocketThread.TAG, String.format("WebSocketService 连接断开，开始第%s次重连", bindTime));
                bindService();
            }
        }
    };

    //绑定服务
    public void bindService() {
        serviceBindSuccess = false;
        binding = true;
        Intent intent = new Intent(context, EventTrackerWebSocketService.class);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        bindTime++;
    }

    //解绑服务
    public void unbindService() {
        binding = false;
        bindTime = 0;
        context.unbindService(serviceConnection);
        serviceBindSuccess = false;
    }

    //发送文本
    public boolean sendText(String textContent) {
        if (eventTrackerWebSocketService != null && serviceBindSuccess) {
            return eventTrackerWebSocketService.sendText(textContent);
        } else if (!binding) {
            bindService();
        }
        return false;
    }

}