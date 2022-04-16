package com.tristana.customViewWithToolsLibrary.tools.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.Dns;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtils {

    public Bitmap getBitmap(String url) {
        Bitmap bitmap = null;
        //1.第一步创建OkHttpClient对象
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequestsPerHost(128);
        dispatcher.setMaxRequests(128);
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newBuilder().dispatcher(dispatcher)
                .connectionPool(new ConnectionPool(64, 10, TimeUnit.MINUTES))
                .connectTimeout(20, TimeUnit.SECONDS)
                .dns(Dns.SYSTEM)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .build();
        //2.第二步创建request
        Request.Builder builder = new Request.Builder();
        Request.Builder requestBuilder = builder.url(Objects.requireNonNull(url)).get();
        final Request request = requestBuilder.build();
        //3.新建一个Call对象
        final Call call = okHttpClient.newCall(request);
        //4.同步请求网络execute()
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                bitmap = BitmapFactory.decodeStream(response.body().byteStream());
            } else {
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public String[] getDataFromCustomUrlByOkHttp3(String requestInfo, Map<String, Object> params, Map<String, Object> header) {
        final String[] result = {null, null};
        String url = null;
        //1.第一步创建OkHttpClient对象
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequestsPerHost(128);
        dispatcher.setMaxRequests(128);
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newBuilder().dispatcher(dispatcher)
                .connectionPool(new ConnectionPool(64, 10, TimeUnit.MINUTES))
                .connectTimeout(20, TimeUnit.SECONDS)
                .dns(Dns.SYSTEM)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .build();
        //初始化参数
        if (requestInfo.equals(RequestInfo.IS_TEST)) {
            if (params != null && !params.isEmpty()) {
                url = Objects.requireNonNull(params.get("url")).toString();
            } else {
                result[0] = "-2";
                result[1] = "url非法！";
            }
        } else {
            url = RequestInfo.REQUEST_URL + requestInfo;
            url = appendParams(url, params);
        }
        //2.第二步创建request
        Request.Builder builder = new Request.Builder();
        Request.Builder requestBuilder = builder.url(Objects.requireNonNull(url)).get();
        if (header != null && !header.isEmpty()) {
            for (String key : header.keySet()) {
                requestBuilder = requestBuilder.addHeader(key, Objects.requireNonNull(header.get(key)).toString());
            }
        }
        final Request request = requestBuilder.build();
        //3.新建一个Call对象
        final Call call = okHttpClient.newCall(request);
        //4.同步请求网络execute()
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                String resp = response.body().string();
                result[0] = String.valueOf(response.code());
                result[1] = resp;
            } else {
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
            result[0] = "-1";
            result[1] = e.toString();
        }
        return result;
    }

    /**
     * 拼接参数
     *
     * @param url
     * @param params
     * @return
     */
    private String appendParams(String url, Map<String, Object> params) {
        StringBuilder sb = new StringBuilder();
        sb.append(url).append("?");
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                sb.append(key).append("=").append(params.get(key)).append("&");
            }
        }
        sb = sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public String[] postDataFromUrlByOkHttp3(String url, Map<String, Object> params, Map<String, Object> header) {
        final String[] result = {null, null};
        //1.第一步创建OkHttpClient对象
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequestsPerHost(128);
        dispatcher.setMaxRequests(128);
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newBuilder().dispatcher(dispatcher)
                .connectionPool(new ConnectionPool(64, 10, TimeUnit.MINUTES))
                .connectTimeout(20, TimeUnit.SECONDS)
                .dns(Dns.SYSTEM)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .build();
        //2.创建 FormBody 添加需要的键值对
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                formBodyBuilder.add(key, Objects.requireNonNull(params.get(key)).toString());
            }
        }
        //3.第二步创建request
        Request.Builder builder = new Request.Builder();
        Request.Builder requestBuilder = builder.url(Objects.requireNonNull(url)).get();
        if (header != null && !header.isEmpty()) {
            for (String key : header.keySet()) {
                requestBuilder = requestBuilder.addHeader(key, Objects.requireNonNull(header.get(key)).toString());
            }
        }
        final Request request = requestBuilder.post(formBodyBuilder.build()).build();
        //4.新建一个Call对象
        final Call call = okHttpClient.newCall(request);
        //5.同步请求网络execute()
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                String resp = response.body().string();
                result[0] = String.valueOf(response.code());
                result[1] = resp;
            } else {
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
            result[0] = "-1";
            result[1] = e.toString();
        }
        return result;
    }

    public String getFileNameFromUrlByOkHttp3(String url, Map<String, Object> params, Map<String, Object> header) {
        String result = null;
        //1.第一步创建OkHttpClient对象
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequestsPerHost(128);
        dispatcher.setMaxRequests(128);
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newBuilder().dispatcher(dispatcher)
                .connectionPool(new ConnectionPool(64, 10, TimeUnit.MINUTES))
                .connectTimeout(20, TimeUnit.SECONDS)
                .dns(Dns.SYSTEM)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .build();
        //初始化参数
        if (params != null && !params.isEmpty()) {
            url = Objects.requireNonNull(params.get("url")).toString();
        }
        //2.第二步创建request
        Request.Builder builder = new Request.Builder();
        Request.Builder requestBuilder = builder.url(Objects.requireNonNull(url)).get();
        if (header != null && !header.isEmpty()) {
            for (String key : header.keySet()) {
                requestBuilder = requestBuilder.addHeader(key, Objects.requireNonNull(header.get(key)).toString());
            }
        }
        final Request request = requestBuilder.build();
        //3.新建一个Call对象
        final Call call = okHttpClient.newCall(request);
        //4.同步请求网络execute()
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                result = getHeaderFileName(response);
            } else {
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String getHeaderFileName(Response response) {
        String dispositionHeader = response.header("Content-Disposition");
        if (dispositionHeader != null && !TextUtils.isEmpty(dispositionHeader)) {
            dispositionHeader.replace("attachment;filename=", "");
            dispositionHeader.replace("filename*=utf-8", "");
            String[] strings = dispositionHeader.split(";");
            if (strings.length > 1) {
                dispositionHeader = strings[1].trim().replace("filename=", "");
                dispositionHeader = dispositionHeader.replace("\"", "");
                return dispositionHeader;
            }
            return null;
        }
        return null;
    }

}
