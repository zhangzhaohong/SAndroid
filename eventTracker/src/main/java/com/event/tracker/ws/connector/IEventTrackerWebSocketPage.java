package com.event.tracker.ws.connector;

import okhttp3.Response;

public abstract class IEventTrackerWebSocketPage implements WebSocketResultListener {

    /**
     * @param text 需要发送的文本数据
     */
    abstract boolean sendText(String text);

    @Override
    public void onClosing(int code, String reason) {

    }

    @Override
    public void onFail(Throwable t, Response response) {

    }

    @Override
    public void connection() {

    }
}
