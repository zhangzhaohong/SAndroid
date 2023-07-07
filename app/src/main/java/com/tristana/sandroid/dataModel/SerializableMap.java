package com.tristana.sandroid.dataModel;

import java.io.Serializable;
import java.util.Map;

public class SerializableMap implements Serializable {
    private Map<String, String> stringMap;
    private Map<String, int[]> intArrayMap;
    private Map<String, Object[]> objectArrayMap;
    private Map<String, Object> objectMap;

    public Map<String, Object> getObjectMap() {
        return objectMap;
    }

    public void setObjectMap(Map<String, Object> objectMap) {
        this.objectMap = objectMap;
    }

    public Map<String, Object[]> getObjectArrayMap() {
        return objectArrayMap;
    }

    public void setObjectArrayMap(Map<String, Object[]> objectArrayMap) {
        this.objectArrayMap = objectArrayMap;
    }

    public Map<String, String> getStringMap() {
        return stringMap;
    }

    public void setStringMap(Map<String, String> stringMap) {
        this.stringMap = stringMap;
    }

    public Map<String, int[]> getIntArrayMap() {
        return intArrayMap;
    }

    public void setIntArrayMap(Map<String, int[]> intArrayMap) {
        this.intArrayMap = intArrayMap;
    }
}
