package com.event.tracker.ws.connector;

import okhttp3.Response;

public interface IEventTrackerWebsocketResponseDispatcher {

    /**
     * 连接成功
     */
    void connection(SocketResultListenerStorage resultListenerStorage);

    /**
     *连接错误
     */
    void onFail(Throwable t, Response response, SocketResultListenerStorage resultListenerStorage);

    /**
     * 接收到消息
     */
    void onMessageReceive(String jsonText, SocketResultListenerStorage resultListenerStorage);

    /**
     * 断开连接，关闭 （异常）
     */
    void onClosing(int code, String reason, SocketResultListenerStorage resultListenerStorage);

}
