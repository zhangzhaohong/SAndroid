package com.event.tracker.ws.connector;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.blankj.utilcode.util.LogUtils;

public class WebSocketServiceManager {

    private Context context;
    private EventTrackerWebSocketService myWebSocketService;
    private boolean serviceBindSucc = false;    //绑定成功
    private boolean binding = false;            //是否正在绑定
    private int bindTime = 0;       //重复连接次数
    private WebSocketResultListener socketResultListener;

    public WebSocketServiceManager(Context context, WebSocketResultListener socketResultListener) {
        this.context = context;
        this.socketResultListener = socketResultListener;
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtils.e(WebSocketThread.TAG, "WebSocketService 已经连接");
            serviceBindSucc = true;
            binding = false;
            bindTime = 0;   //连接成功后归零
            myWebSocketService = ((EventTrackerWebSocketService.WebSocketBinder) service).getWebSocketService();
            myWebSocketService.addSocketListener(socketResultListener);
            if (myWebSocketService.isSendConnected()) {
                if (socketResultListener != null) {
                    socketResultListener.connection();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {     //service异常被关闭 回调该方法
            binding = false;
            serviceBindSucc = false;
            if (bindTime < 5 && !binding) {
                LogUtils.e(WebSocketThread.TAG, String.format("WebSocketService 连接断开，开始第%s次重连", bindTime));
                bindService();
            }
        }
    };

    //绑定服务
    public void bindService() {
        serviceBindSucc = false;
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
        serviceBindSucc = false;
    }

    //发送文本
    public boolean sendText(String textContent) {
        if (myWebSocketService != null && serviceBindSucc) {
            return myWebSocketService.sendText(textContent);
        } else if (!binding) {
            bindService();
        }
        return false;
    }

}
