package com.event.tracker.ws.connector;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.event.tracker.TrackerInstance;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketThread extends Thread {

    public static final String TAG = "EventTrackerWebSocket";
    // public static final String WEB_SOCKET_URL = "ws://192.168.2.250:8080/websocket/v1/connection/" + TrackerInstance.Companion.get().getTrackerId();
    public static final String WEB_SOCKET_URL = TrackerInstance.Companion.get().getHost().replaceFirst("http://", "ws://") + "/websocket/v1/connection/" + TrackerInstance.Companion.get().getTrackerId();
    private final Map<String, String> headerMap = new HashMap<>();
    private Request request;
    private final WebSocketHandler webSocketHandler;
    private WebSocket mWebSocket;
    private final ReconnectWebSocketManager reconnectWebSocketManager;
    private WebSocketResultListener webSocketResultListener;

    private int connectStatus = 0;      //连接状态 0 -未连接，1 -正在连接，2 -已连接
    private boolean sendConnected = false; //是否回调了已经连接的回调

    public WebSocketThread() {
        sendConnected = false;
        webSocketHandler = new WebSocketHandler();
        reconnectWebSocketManager = new ReconnectWebSocketManager(this);
        initHeader();
        initRequest();
    }

    private void initRequest() {
        Request.Builder requestBuilder = new Request.Builder().url(WEB_SOCKET_URL);
        if (!headerMap.isEmpty()) {
            Set<String> strings = headerMap.keySet();
            StringBuilder stringBuffer = new StringBuilder();
            for (String string : strings) {
                stringBuffer.append(string).append(" ").append(headerMap.get(string)).append("\t");
            }
            LogUtils.e("web", stringBuffer.toString());
            requestBuilder.headers(Headers.of(headerMap));
        }
        request = requestBuilder.build();
    }

    private void initHeader() {
        headerMap.put("source", "android");
    }

    @Override
    public void run() {
        super.run();
        LogUtils.e("looper==", "run");
        Looper.prepare();
        // solve handler leak
        // webSocketHandler = new WebSocketHandler();
        webSocketHandler.sendEmptyMessage(MessageType.CONNECT);
        Looper.loop();
    }

    public boolean isSendConnected() {
        return sendConnected;
    }

    private void connect() {
        if (connectStatus == 0) { // 未连接
            connectStatus = 1;    // 正在连接

            mWebSocket = new OkHttpClient().newWebSocket(request, new WebSocketListener() {
                @Override
                public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                    super.onOpen(webSocket, response);
                    //连接成功 --回调该方法
                    connectStatus = 2;
                    LogUtils.d(TAG, "onOpen-success");
                    if (getWebSocketResultListener() != null) {
                        getWebSocketResultListener().connection();
                        sendConnected = true;
                    } else {
                        sendConnected = false;
                    }
                }

                @Override
                public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                    super.onMessage(webSocket, text);
                    connectStatus = 2;
                    LogUtils.d(TAG, "onMessage==" + text);
                    Message message = Message.obtain();
                    message.what = MessageType.RECEIVE_MESSAGE;
                    message.obj = text;
                    webSocketHandler.sendMessage(message);
                }

                @Override
                public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
                    super.onMessage(webSocket, bytes);
                }

                @Override
                public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                    super.onClosing(webSocket, code, reason);
                    connectStatus = 0;
                    LogUtils.d(TAG, "onClosing-reason==" + reason);
                    webSocket.close(code, reason);
                    reConnectWebSocket();

                    if (getWebSocketResultListener() != null)
                        getWebSocketResultListener().onClosing(code, reason);
                }

                @Override
                public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                    super.onClosed(webSocket, code, reason);
                    connectStatus = 0;
                    LogUtils.d(TAG, "onClosed-reason==" + reason);
                }

                @Override
                public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
                    super.onFailure(webSocket, t, response);
                    connectStatus = 0;
                    LogUtils.d(TAG, "onFailure");
                    if (getWebSocketResultListener() != null)
                        getWebSocketResultListener().onFail(t, response);
                }
            });
        }
    }

    private void sendTextContent(String sendContent) {
        if (mWebSocket != null && connectStatus == 2) {
            mWebSocket.send(sendContent);
        } else {
            reConnectWebSocket();
        }
    }

    /**
     * 重新连接
     */
    private void reConnectWebSocket() {
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            reconnectWebSocketManager.performReconnect();
            LogUtils.e(TAG, "调用了重新连接reConnectWebSocket()方法");
        }
    }

    private void disConnect() {
        if (connectStatus == 2) {
            if (mWebSocket != null) mWebSocket.close(1000, "normal");
            connectStatus = 0;
        }
    }

    /**
     * 结束并销毁
     */
    private void quite() {
        disConnect();
        mWebSocket = null;
        webSocketHandler.removeCallbacksAndMessages(null);
        reconnectWebSocketManager.destroy();
        Looper looper = Looper.myLooper();
        if (looper != null) looper.quitSafely();
    }

    public WebSocket getWebSocket() {
        return mWebSocket;
    }

    public Handler getHandler() {
        return webSocketHandler;
    }

    public int getConnectStatus() {
        return connectStatus;
    }

    public WebSocketResultListener getWebSocketResultListener() {
        return webSocketResultListener;
    }

    public void setWebSocketResultListener(WebSocketResultListener webSocketResultListener) {
        this.webSocketResultListener = webSocketResultListener;
    }

    @SuppressLint("HandlerLeak")
    public class WebSocketHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MessageType.CONNECT: //建立连接
                    connect();
                    break;
                case MessageType.RECEIVE_MESSAGE:   //接收到消息
                    String jsonContent = (String) msg.obj;
                    if (getWebSocketResultListener() != null)
                        getWebSocketResultListener().onMessageReceive(jsonContent);
                    break;
                case MessageType.QUIT:
                    quite();
                    break;
                case MessageType.SEND:
                    if (msg.obj instanceof String sendContent) {
                        sendTextContent(sendContent);
                    }
                    break;
            }
        }

    }
}
