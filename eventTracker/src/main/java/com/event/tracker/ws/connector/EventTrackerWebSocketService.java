package com.event.tracker.ws.connector;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;

import androidx.annotation.Nullable;

import okhttp3.Response;

public class EventTrackerWebSocketService extends Service implements WebSocketResultListener {

    private WebSocketThread webSocketThread;
    private IEventTrackerWebsocketResponseDispatcher responseDispatcher;
    private final SocketResultListenerStorage socketResultListenerStorage = new SocketResultListenerStorage();

    @Override
    public void onCreate() {
        super.onCreate();
        webSocketThread = new WebSocketThread();
        webSocketThread.setWebSocketResultListener(this);
        webSocketThread.start();
        responseDispatcher = new WebsocketResponseDispatcher();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new WebSocketBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webSocketThread.getHandler() != null)
            webSocketThread.getHandler().sendEmptyMessage(MessageType.QUIT);
    }

    public boolean sendText(String text) {
        if (webSocketThread.getHandler() != null) {
            Message message = Message.obtain();
            message.obj = text;
            message.what = MessageType.SEND;
            return webSocketThread.getHandler().sendMessage(message);
        }
        return false;
    }

    public void addSocketListener(WebSocketResultListener listener) {
        socketResultListenerStorage.addCallBack(listener);
    }

    public void removeSocketListener(WebSocketResultListener listener) {
        socketResultListenerStorage.removeCallBack(listener);
    }

    @Override
    public void connection() {
        responseDispatcher.connection(socketResultListenerStorage);
    }

    @Override
    public void onFail(Throwable t, Response response) {
        responseDispatcher.onFail(t, response, socketResultListenerStorage);
    }

    @Override
    public void onMessageReceive(String jsonText) {
        responseDispatcher.onMessageReceive(jsonText, socketResultListenerStorage);
    }

    @Override
    public void onClosing(int code, String reason) {
        responseDispatcher.onClosing(code, reason, socketResultListenerStorage);
    }

    public class WebSocketBinder extends Binder {
        public EventTrackerWebSocketService getWebSocketService() {
            return EventTrackerWebSocketService.this;
        }
    }

    public boolean isSendConnected() {
        return webSocketThread != null && webSocketThread.isSendConnected();
    }

}
