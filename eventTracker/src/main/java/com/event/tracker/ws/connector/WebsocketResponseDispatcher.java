package com.event.tracker.ws.connector;

import okhttp3.Response;

public class WebsocketResponseDispatcher implements IEventTrackerWebsocketResponseDispatcher {

    @Override
    public void connection(SocketResultListenerStorage resultListenerStorage) {
        for (WebSocketResultListener socketResultListener : resultListenerStorage.getCallBacks()) {
            socketResultListener.connection();
        }
    }

    @Override
    public void onFail(Throwable t, Response response, SocketResultListenerStorage resultListenerStorage) {
        for (WebSocketResultListener socketResultListener : resultListenerStorage.getCallBacks()) {
            socketResultListener.onFail(t, response);
        }
    }

    @Override
    public void onMessageReceive(String message, SocketResultListenerStorage resultListenerStorage) {
        for (WebSocketResultListener socketResultListener : resultListenerStorage.getCallBacks()) {
            socketResultListener.onMessageReceive(message);
        }
    }

    @Override
    public void onClosing(int code, String reason, SocketResultListenerStorage resultListenerStorage) {
        for (WebSocketResultListener socketResultListener : resultListenerStorage.getCallBacks()) {
            socketResultListener.onClosing(code, reason);
        }
    }
}
