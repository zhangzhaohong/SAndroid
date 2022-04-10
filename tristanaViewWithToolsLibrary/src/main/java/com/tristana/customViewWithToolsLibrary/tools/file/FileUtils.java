package com.tristana.customViewWithToolsLibrary.tools.file;

import android.content.Context;

import com.blankj.utilcode.util.LogUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;

public class FileUtils {

    public void writeData(Context context, String fileName, String text) {
        FileWriter fileWriter;
        try {
            String path = Objects.requireNonNull(context.getExternalCacheDir()).getPath() + "/" + fileName + ".sfd";
            LogUtils.i("FilePath:" + path);
            LogUtils.i("FileData:" + text);
            fileWriter = new FileWriter(path, true);
            String data = text + "\r\n";
            fileWriter.write(data);
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i("Exception:" + e.toString());
        }
    }

    public ArrayList<String> readLine(Context context, String fileName) {
        ArrayList<String> result = new ArrayList<>();
        try {
            String path = Objects.requireNonNull(context.getExternalCacheDir()).getPath() + "/" + fileName + ".sfd";
            LogUtils.i("FilePath:" + path);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
            for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                LogUtils.d(line);
                result.add(line);
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i("Exception:" + e.toString());
        }
        return result;
    }

    public boolean deleteFile(Context context, String fileName) {
        try {
            String path = Objects.requireNonNull(context.getExternalCacheDir()).getPath() + "/" + fileName + ".sfd";
            LogUtils.i("FilePath:" + path);
            File file = new File(path);
            if (file.isFile() && file.exists()) {
                return file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i("Exception:" + e.toString());
        }
        return false;
    }

}
