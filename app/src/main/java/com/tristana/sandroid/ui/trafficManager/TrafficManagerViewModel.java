package com.tristana.sandroid.ui.trafficManager;

import android.content.Context;

import com.tristana.sandroid.R;
import com.tristana.sandroid.model.trafficManager.TrafficManagerModel;
import com.tristana.sandroid.tools.http.HttpUtils;
import com.tristana.sandroid.tools.log.Timber;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TrafficManagerViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    private Boolean isFirstRequest = true;

    private Boolean isRequest;

    private int timeRequest = 60;

    public MutableLiveData<Boolean> getFinishStatus() {
        return mFinish;
    }

    private MutableLiveData<Boolean> mFinish;

    public void setNeedStop(Boolean needStop) {
        this.needStop = needStop;
    }

    public Boolean getNeedStop() {
        return needStop;
    }

    private Boolean needStop;

    public MutableLiveData<ArrayList<TrafficManagerModel>> getLightData() {
        return mLightData;
    }

    private MutableLiveData<ArrayList<TrafficManagerModel>> mLightData;

    public MutableLiveData<String> getToast() {
        return mToast;
    }

    private MutableLiveData<String> mToast;

    public TrafficManagerViewModel() {
        isRequest = false;
        mToast = new MutableLiveData<>();
        mText = new MutableLiveData<>();
        mLightData = new MutableLiveData<>();
        mFinish = new MutableLiveData<>(false);
        mText.setValue("This is traffic manager fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void startRequest(final Context context) {
        final String url = "https://data.meternity.cn/api/v0/lightStatus.php";
        if (!isRequest && !needStop) {
            isRequest = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String[] data = new HttpUtils().getDataFromUrl(url);
                    if (Integer.parseInt(data[0]) <= 400) {
                        String json = data[1];
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            int code = Integer.parseInt(jsonObject.get("code").toString());
                            if (code == 0) {
                                JSONArray trafficData = jsonObject.getJSONArray("status");
                                new Timber("TrafficManagerViewModel").d("length" + trafficData.length());
                                ArrayList<TrafficManagerModel> result = new ArrayList<>();
                                result.add(new TrafficManagerModel(context.getString(R.string.title_id), context.getString(R.string.title_red_duration), context.getString(R.string.title_yellow_duration), context.getString(R.string.title_green_duration)));
                                for (int i = 0; i < trafficData.length(); i++) {
                                    JSONObject tData = new JSONObject(trafficData.get(i).toString());
                                    new Timber("TrafficManagerViewModel").d("tData" + tData);
                                    result.add(new TrafficManagerModel(tData.getString("roadId"), tData.getString("redLightDuration"), tData.getString("yellowLightDuration"), tData.getString("greenLightDuration")));
                                }
                                new Timber("TrafficManagerViewModel").d("length_result" + result.size());
                                mLightData.postValue(result);
                            } else {
                                mToast.postValue("请求失败！code：" + data[0] + "\n" + data[1]);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mToast.postValue("JSONException" + "\n" + data[1]);
                        }
                    } else {
                        mToast.postValue("请求失败！code：" + data[0] + "\n" + data[1]);
                    }
                    isRequest = false;
                    isFirstRequest = false;
                }
            }).start();
        } else {
            mToast.setValue("上一个请求正在进行中，请稍后重试！");
        }
    }

    public void startTimer() {
        if (!isFirstRequest && !needStop) {
            mFinish.setValue(false);
            //timeRequest 单位为秒
            long start = System.currentTimeMillis();
            //end 计算结束时间
            final long end = start + timeRequest * 1000;
            final Timer timer = new Timer();
            //延迟0毫秒（即立即执行）开始，每隔1000毫秒执行一次
            timer.schedule(new TimerTask() {
                public void run() {
                    if (needStop) {
                        timer.schedule(new TimerTask() {
                            public void run() {
                                new Timber("TrafficManagerViewModel").d("页面退出，操作暂停！");
                                mFinish.postValue(true);
                                timer.cancel();
                            }
                        }, new Date(end));
                    }
                    //show是剩余时间，即要显示的时间
                    long show = end - System.currentTimeMillis();
                    long h = show / 1000 / 60 / 60;//时
                    long m = show / 1000 / 60 % 60;//分
                    long s = show / 1000 % 60;//秒
                    new Timber("TrafficManagerViewModel").d("现在时间：" + h + "时" + m + "分" + s + "秒");
                }
            }, 0, 1000);
            //计时结束时候，停止全部timer计时计划任务
            timer.schedule(new TimerTask() {
                public void run() {
                    mFinish.postValue(true);
                    timer.cancel();
                }
            }, new Date(end));
        }
    }
}