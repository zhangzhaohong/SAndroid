package com.event.tracker.ws.connector;

public class MessageType {

    /**
     * 连接Socket
     */
    public static final int CONNECT = 0;
    /**
     * 接收到的消息
     */
    public static final int RECEIVE_MESSAGE = 1;

    /**
     * 销毁
     */
    public static final int QUIT = 2;

    /**
     * 发送消息
     */
    public static final int SEND = 3;
}
