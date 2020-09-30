package com.tristana.sandroid.tools.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

public class HttpUtils {
    public String[] getDataFromUrl(final String urlString) {
        final String[] result = {null, null};
        try {
            int code = 0;
            InputStream inputStreams = null;
            StringBuilder stringBuilder = new StringBuilder();
            URL url = new URL(urlString);
            URLConnection connection = null;
            try {
                connection = url.openConnection();
                connection.setConnectTimeout(5000);
            } catch (IOException e) {
                e.printStackTrace();
                result[0] = "-1";
                result[1] = e.toString();
            }
            if (!Objects.equals(result[0], "-1")) {
                if (urlString.startsWith("https:")) {
                    HttpsURLConnection urlConnection = (HttpsURLConnection) connection;
                    code = Objects.requireNonNull(urlConnection).getResponseCode();
                    inputStreams = urlConnection.getInputStream();
                } else if (urlString.startsWith("http:")) {
                    HttpURLConnection urlConnection = (HttpURLConnection) connection;
                    code = Objects.requireNonNull(urlConnection).getResponseCode();
                    inputStreams = urlConnection.getInputStream();
                }
                if (code <= 400) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStreams));
                    String data;
                    while ((data = bufferedReader.readLine()) != null) {
                        stringBuilder.append(data);
                    }
                    result[0] = Integer.toString(code);
                    result[1] = stringBuilder.toString();
                } else {
                    result[0] = Integer.toString(code);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            result[0] = "-1";
            result[1] = e.toString();
        }
        return result;
    }
}
