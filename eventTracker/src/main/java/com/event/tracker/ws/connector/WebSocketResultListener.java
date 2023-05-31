package com.event.tracker.ws.connector;

import okhttp3.Response;

public interface WebSocketResultListener {

    /**
     * 连接成功
     */
    void connection();

    /**
     * 连接错误
     */
    void onFail(Throwable t, Response response);

    /**
     * 接收到消息
     */
    void onMessageReceive(String jsonText);

    /**
     * 断开连接，关闭 （异常）
     */
    void onClosing(int code, String reason);

}
