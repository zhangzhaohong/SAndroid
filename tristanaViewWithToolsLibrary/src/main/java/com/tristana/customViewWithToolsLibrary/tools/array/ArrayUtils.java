package com.tristana.customViewWithToolsLibrary.tools.array;

import com.tristana.customViewWithToolsLibrary.tools.log.Timber;

import java.util.ArrayList;
import java.util.Arrays;

public class ArrayUtils {
    private final Timber timber;

    public ArrayUtils() {
        timber = new Timber().getTimber();
    }

    public ArrayList<Object> textToArrayList(String text) {
        String[] arr = text.split(",");
        return new ArrayList<Object>(Arrays.asList(arr));
    }

    public String arrayListToString(ArrayList<String> arrayList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < arrayList.size(); i++) {
            if (i == arrayList.size() - 1) {
                stringBuilder.append(arrayList.get(i));
            } else {
                stringBuilder.append(arrayList.get(i)).append(",");
            }
        }
        return stringBuilder.toString();
    }
}
