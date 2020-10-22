package com.tristana.sandroid.ui.illegalManager.fragment.video;

import android.graphics.Bitmap;

import com.google.gson.Gson;
import com.tristana.sandroid.model.illegalManager.IllegalFileModel;
import com.tristana.sandroid.model.illegalManager.IllegalVideoRespModel;
import com.tristana.sandroid.tools.http.HttpUtils;
import com.tristana.sandroid.tools.http.RequestInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class IllegalVideoViewModel extends ViewModel {

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

    public IllegalVideoViewModel() {
        fileList = new MutableLiveData<>();
        picList = new MutableLiveData<>();
        mToast = new MutableLiveData<>();
        mText = new MutableLiveData<>();
        mText.setValue("This is illegalVideo fragment");
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
                    String[] data = new HttpUtils().getDataFromUrlByOkHttp3(RequestInfo.REQUEST_VIDEO_LIST, urlParams, header);
                    if (Integer.parseInt(data[0]) == -1 || Integer.parseInt(data[0]) > 400) {
                        mText.postValue("请求失败 code:" + data[0] + "\n" + data[1]);
                    } else {
//                        mText.postValue(data[1]);
                        String json = data[1];
                        Gson gson = new Gson();
                        IllegalVideoRespModel illegalVideoRespModel = gson.fromJson(json, IllegalVideoRespModel.class);
                        int code = Integer.parseInt(illegalVideoRespModel.getCode());
                        if (code == 0) {
                            List<IllegalVideoRespModel.DataBean> dataBeans = illegalVideoRespModel.getData();
                            ArrayList<IllegalFileModel> result = new ArrayList<>();
                            for (int i = 0; i < dataBeans.size(); i++) {
                                IllegalVideoRespModel.DataBean cData = dataBeans.get(i);
                                result.add(new IllegalFileModel(cData.getCover(), cData.getFile(), cData.getContent()));
                            }
                            fileList.postValue(result);
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