package com.event.tracker.ws.connector;

import android.os.Handler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReconnectWebSocketManager {

    private WebSocketThread webSocketThread;
    private volatile boolean retrying;      //正在重新连接
    private volatile boolean destroyed;
    private final ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();       //单线程

    public ReconnectWebSocketManager(WebSocketThread webSocketThread) {
        this.webSocketThread = webSocketThread;
        this.retrying = false;
        this.destroyed = false;
    }

    synchronized void performReconnect() {
        retrying = false;
        retry();
    }

    private synchronized void retry() {
        if (!retrying) {
            retrying = true;
            synchronized (singleThreadPool) {
                singleThreadPool.execute(new ReconnectRunnable());
            }
        }
    }

    //销毁
    public void destroy() {
        destroyed = true;
        if (singleThreadPool != null) singleThreadPool.shutdownNow();
        webSocketThread = null;
    }

    public class ReconnectRunnable implements Runnable {

        @Override
        public void run() {
            retrying = true;
            for (int i = 0; i < 20; i++) {
                if (destroyed) {
                    retrying = false;
                    return;
                }
                Handler handler = webSocketThread.getHandler();
                if (handler != null) {
                    if (webSocketThread.getConnectStatus() == 2) //已连接
                        break;
                    if (webSocketThread.getConnectStatus() == 1) //正在连接
                        continue;
                    if (webSocketThread.getConnectStatus() == 0)
                        handler.sendEmptyMessage(MessageType.CONNECT);
                } else {
                    break;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                retrying = false;
            }
        }
    }
}
