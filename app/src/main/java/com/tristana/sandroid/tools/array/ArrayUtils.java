package com.tristana.sandroid.tools.array;

import com.tristana.customViewLibrary.tools.log.Timber;

import java.util.ArrayList;
import java.util.Arrays;

public class ArrayUtils {
    private final Timber timber;

    public ArrayUtils() {
        timber = new Timber("ArrayUtils");
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
        timber.d("Data:" + stringBuilder.toString());
        return stringBuilder.toString();
    }
}
