package com.tristana.sandroid.ui.illegalManager.fragment.video;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Build;

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
    private MutableLiveData<ArrayList<IllegalFileModel>> fileList;
    private MutableLiveData<ArrayList<Bitmap>> picList;
    private boolean isRequest = false;

    public IllegalVideoViewModel() {
        fileList = new MutableLiveData<>();
        picList = new MutableLiveData<>();
        mToast = new MutableLiveData<>();
        mText = new MutableLiveData<>();
        mText.setValue("This is illegalVideo fragment");
    }

    public MutableLiveData<ArrayList<IllegalFileModel>> getFileList() {
        return fileList;
    }

    public MutableLiveData<ArrayList<Bitmap>> getPicList() {
        return picList;
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
                    result.add(compressBitmap(new HttpUtils().getBitmap(data.get(i).getCover())));
                }
                picList.postValue(result);
            }
        }).start();
    }

    /**
     * 得到bitmap的大小
     */
    public static int getBitmapSize(Bitmap bitmap) {
        long result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            result = bitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            result = bitmap.getByteCount();
        } else {
            // 在低版本中用一行的字节x高度
            result = bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
        }
        return (int) result / 1024 / 1024;
    }

    private Bitmap compressBitmap(Bitmap bitmap) {
        float option = 1.0F;
        Bitmap newBitmap = bitmap;
        while (getBitmapSize(newBitmap) > 100 && (int) option * 10 > 0) {
            option = (option * 10 - 1) / 10;
            Matrix matrix = new Matrix();
            matrix.postScale(option, option);
            newBitmap = Bitmap.createBitmap(newBitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        return newBitmap;
    }

}