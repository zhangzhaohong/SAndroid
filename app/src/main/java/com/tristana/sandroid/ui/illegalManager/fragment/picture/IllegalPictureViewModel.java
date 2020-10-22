package com.tristana.sandroid.ui.illegalManager.fragment.picture;

import android.graphics.Bitmap;

import com.tristana.sandroid.model.illegalManager.IllegalFileModel;
import com.tristana.sandroid.tools.http.HttpUtils;
import com.tristana.sandroid.tools.http.RequestInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class IllegalPictureViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    private MutableLiveData<String> mToast;

    public MutableLiveData<ArrayList<IllegalFileModel>> getFileList() {
        return fileList;
    }

    private MutableLiveData<ArrayList<IllegalFileModel>> fileList;

    public MutableLiveData<ArrayList<Bitmap>> getPicList() {
        return picList;
    }

    private MutableLiveData<ArrayList<Bitmap>> picList;

    private boolean isRequest = false;

    public IllegalPictureViewModel() {
        fileList = new MutableLiveData<>();
        picList = new MutableLiveData<>();
        mToast = new MutableLiveData<>();
        mText = new MutableLiveData<>();
        mText.setValue("This is illegalPicture fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<String> getToast() {
        return mToast;
    }

    public void startRequest() {
        if (!isRequest) {
            isRequest = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Map<String, Object> header = new HashMap<>();
                    Map<String, Object> urlParams = new HashMap<>();
                    urlParams.put("api_key", RequestInfo.REQUEST_API_KEY);
                    String[] data = new HttpUtils().getDataFromUrlByOkHttp3(RequestInfo.REQUEST_PIC_LIST, urlParams, header);
                    if (Integer.parseInt(data[0]) == -1 || Integer.parseInt(data[0]) > 400) {
                        mText.postValue("请求失败 code:" + data[0] + "\n" + data[1]);
                    } else {
//                        mText.postValue(data[1]);
                        try {
                            JSONObject json = new JSONObject(data[1]);
                            if (json.get("code").equals("0")) {
                                JSONArray jsonArray = json.getJSONArray("data");
                                ArrayList<IllegalFileModel> result = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonData = jsonArray.getJSONObject(i);
                                    result.add(new IllegalFileModel(jsonData.getString("cover"), jsonData.getString("file"), jsonData.getString("content")));
                                }
                                fileList.postValue(result);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mText.postValue("JSONException code:" + data[0] + "\n" + data[1]);
                        }
                    }
                    isRequest = false;
                }
            }).start();
        } else {
            mToast.setValue("上一个请求正在进行中，请稍后重试！");
        }
    }

    public void startGetPic() {
        final ArrayList<Bitmap> result = new ArrayList<>();
        final ArrayList<IllegalFileModel> data = fileList.getValue();
        new Thread(new Runnable() {
            @Override
            public void run() {
                assert data != null;
                for (int i = 0; i < data.size(); i++) {
                    result.add(HttpUtils.getBitmap(data.get(i).getCover()));
                }
                picList.postValue(result);
            }
        }).start();
    }
}