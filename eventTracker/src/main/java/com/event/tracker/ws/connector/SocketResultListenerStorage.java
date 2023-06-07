package com.event.tracker.ws.connector;

import java.util.ArrayList;
import java.util.HashMap;

public class SocketResultListenerStorage {

    private final HashMap<String, WebSocketResultListener> idToCallBack = new HashMap<>();
    private final HashMap<WebSocketResultListener, String> callBackToId = new HashMap<>();

    public void addCallBack(WebSocketResultListener t) {
        String id = t.getClass().getSimpleName() + "/" + System.nanoTime() + "/" + (int) (Math.random() * Integer.MAX_VALUE);
        idToCallBack.put(id, t);
        callBackToId.put(t, id);
    }

    public void removeCallBack(WebSocketResultListener t) {
        idToCallBack.remove(callBackToId.remove(t));
    }

    public ArrayList<WebSocketResultListener> getCallBacks() {
        return new ArrayList<>(idToCallBack.values());
    }

}
