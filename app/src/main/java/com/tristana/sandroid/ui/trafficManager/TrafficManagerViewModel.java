package com.tristana.sandroid.ui.trafficManager;

import android.os.Build;

import com.google.gson.Gson;
import com.tristana.sandroid.model.trafficManager.LightStatusRespModel;
import com.tristana.sandroid.model.trafficManager.TrafficManagerModel;
import com.tristana.sandroid.model.trafficManager.TrafficSortType;
import com.tristana.sandroid.tools.http.HttpUtils;
import com.tristana.sandroid.tools.http.RequestInfo;
import com.tristana.sandroid.tools.log.Timber;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TrafficManagerViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    private Boolean isFirstRequest = true;

    private Boolean isRequest;

    /***
     * 千万别把数值改小 要不然服务器容易炸
     * */
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

    public MutableLiveData<ArrayList<TrafficManagerModel>> getLightSortData() {
        return mLightSortData;
    }

    private MutableLiveData<ArrayList<TrafficManagerModel>> mLightSortData;

    public MutableLiveData<String> getToast() {
        return mToast;
    }

    private MutableLiveData<String> mToast;

    public TrafficManagerViewModel() {
        isRequest = false;
        mToast = new MutableLiveData<>();
        mText = new MutableLiveData<>();
        mLightData = new MutableLiveData<>();
        mLightSortData = new MutableLiveData<>();
        mFinish = new MutableLiveData<>(false);
        mText.setValue("This is traffic manager fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void startRequest(final String sortType) {
        if (!isRequest && !needStop) {
            isRequest = true;
            new Thread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void run() {
                    Map<String, Object> header = new HashMap<>();
                    Map<String, Object> urlParams = new HashMap<>();
                    urlParams.put("api_key", RequestInfo.REQUEST_API_KEY);
                    String[] data = new HttpUtils().getDataFromUrlByOkHttp3(RequestInfo.REQUEST_LIGHT_LIST, urlParams, header);
                    if (Integer.parseInt(data[0]) <= 400) {
                        String json = data[1];
                        Gson gson = new Gson();
                        LightStatusRespModel lightStatusRespModel = gson.fromJson(json, LightStatusRespModel.class);
                        int code = Integer.parseInt(lightStatusRespModel.getCode());
                        if (code == 0) {
                            List<LightStatusRespModel.StatusBean> status = lightStatusRespModel.getStatus();
                            ArrayList<TrafficManagerModel> result = new ArrayList<>();
                            for (int i = 0; i < status.size(); i ++) {
                                LightStatusRespModel.StatusBean cData = status.get(i);
                                result.add(new TrafficManagerModel(cData.getRoadId(), cData.getRedLightDuration(), cData.getYellowLightDuration(), cData.getGreenLightDuration()));
                            }
                            mLightData.postValue(result);
                        } else {
                            mToast.postValue("请求失败！code：" + data[0] + "\n" + data[1]);
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sortData(ArrayList<TrafficManagerModel> result, final String sortType) {
        result.sort(new Comparator<TrafficManagerModel>() {
            @Override
            public int compare(TrafficManagerModel x, TrafficManagerModel y) {
                switch (sortType) {
                    case TrafficSortType.SORT_BY_ROAD_UP:
                    case TrafficSortType.SORT_BY_ROAD_DOWN:
                        return Integer.compare(getInteger(x.getId()), getInteger(y.getId()));
                    case TrafficSortType.SORT_BY_RED_LIGHT_UP:
                    case TrafficSortType.SORT_BY_RED_LIGHT_DOWN:
                        return Integer.compare(getInteger(x.getRedDuration()), getInteger(y.getRedDuration()));
                }
                return 0;
            }
        });
        switch (sortType) {
            case TrafficSortType.SORT_BY_ROAD_DOWN:
            case TrafficSortType.SORT_BY_RED_LIGHT_DOWN:
                ArrayList<TrafficManagerModel> finalResult = new ArrayList<>();
                for (int i = result.size(); i > 0; i--) {
                    finalResult.add(result.get(i - 1));
                }
                result = finalResult;
        }
        mLightSortData.postValue(result);
    }

    private int getInteger(String input) {
        try {
            if (input == null || input.equals("")) {
                return 0;
            } else {
                return Integer.parseInt(input);
            }
        } catch (Exception e) {
            return 0;
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
                    if (getNeedStop()) {
                        new Timber("TrafficManagerViewModel").d("页面离开，停止计时操作！");
                        timer.cancel();
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