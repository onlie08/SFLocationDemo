package com.sfmap.api.location.client.impl;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.sfmap.api.location.BuildConfig;
import com.sfmap.api.location.R;
import com.sfmap.api.location.SfMapLocation;
import com.sfmap.api.location.SfMapLocationClient;
import com.sfmap.api.location.client.bean.CellTowerBean;
import com.sfmap.api.location.client.bean.DesUtil;
import com.sfmap.api.location.client.bean.LbsApiResult;
import com.sfmap.api.location.client.bean.LbsApiResultData;
import com.sfmap.api.location.client.bean.RequestBean;
import com.sfmap.api.location.client.bean.ResponseBean;
import com.sfmap.api.location.client.bean.WifiDeviceBean;
import com.sfmap.api.location.client.util.AppInfo;
import com.sfmap.api.location.client.util.NetLocationListener;
import com.sfmap.api.location.client.util.NetLocator;
import com.sfmap.api.location.client.util.NetworkDataManager;
import com.sfmap.api.location.client.util.Utils;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.List;

public class NetLocatorSfImpl implements NetLocator {
    private static final int MSG_LOOP_NETWORK_LOCATION = 10000;
    private static final String TAG = "NetLocatorSfImpl";
    private final Application mApplication;
    private NetLocationListener mNetLocationListener;
    private Handler mUiHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MSG_LOOP_NETWORK_LOCATION) {
                if (BuildConfig.DEBUG) {
                    Log.v(TAG, "Handling message MSG_LOOP_NETWORK_LOCATION...");
                }
                if (mLocating) {
                    startActualNetLocating();
                }
                mUiHandler.removeMessages(MSG_LOOP_NETWORK_LOCATION);
                if (!mOnceLocation && mLocating) {
                    mUiHandler.sendEmptyMessageDelayed(MSG_LOOP_NETWORK_LOCATION, mIntervalMs);
                }
                return true;
            }
            return false;
        }
    });


    private long mIntervalMs;
    private boolean mOnceLocation;
    private volatile boolean mLocating;
    private ResponseBean mLastSuccessResponseBean;
    private String mApiKey;
    private boolean mNeedAddress;
    private boolean mUseGcj02;
    private boolean mTraceEnable;
    private RequestBean requestBean;
    private volatile long mLastNetworkRequestTime;

    @Override
    public void startLocation(long intervalMs) {
        mIntervalMs = intervalMs;
        if (mNetworkDataManager == null) {
            initNetWorkDataManager(mApplication);
        }
        if (mNetworkDataManager != null) {
            mNetworkDataManager.addOnDataAvailableCallback(mOnDataAvailableCallback);
        } else {
            notifyLocationError(SfMapLocation.ERROR_CODE_FAILURE_LOCATION_PERMISSION);
            return;
        }
        mLocating = true;

        mUiHandler.removeMessages(MSG_LOOP_NETWORK_LOCATION);
        mUiHandler.sendEmptyMessage(MSG_LOOP_NETWORK_LOCATION);
    }

    private void startActualNetLocating() {

        if (isAirPlaneModeOn()) {
            notifyLocationError(SfMapLocation.ERROR_CODE_AIRPLANEMODE_WIFIOFF);
            return;
        }

        //没有相关权限时，可能导致NetworkDataManager为空
        if (mNetworkDataManager == null) {
            notifyLocationError(SfMapLocation.ERROR_CODE_FAILURE_LOCATION_PERMISSION);
        } else {
            //用户设置了比定时时间（扫描+网络请求）小的定位间隔,可能导致真实的定位（扫描+网络请求）间隔
            //比用户设置的间隔小
            if (!mNetworkDataManager.startNetworkScan()) {
                if (BuildConfig.DEBUG) {
                    Log.v(TAG, "Start network scan request failed.");
                }
            }
            publishLocationUpdate();
        }
    }

    private boolean isAirPlaneModeOn() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.Global.getInt(mApplication.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        } else {
            return Settings.System.getInt(mApplication.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        }
    }

    @Override
    public void stopLocation() {
        mLocating = false;
        mUiHandler.removeMessages(MSG_LOOP_NETWORK_LOCATION);
        if (mNetworkDataManager != null) {
            mNetworkDataManager.removeOnDataAvailableCallback(mOnDataAvailableCallback);
        }
    }

    @Override
    public void setIsOnce(boolean onceLocation) {
        mOnceLocation = onceLocation;
    }

    @Override
    public void setApiKey(String apiKey) {
        mApiKey = apiKey;
    }

    @Override
    public void destroy() {
        //do nothing
    }

    @Override
    public void setNeedAddress(boolean needAddress) {
        mNeedAddress = needAddress;
    }

    @Override
    public void setUseGcj02(boolean useGcj02) {
        mUseGcj02 = useGcj02;
    }

    @Override
    public void setTraceEnable(boolean traceEnable) {
        mTraceEnable = traceEnable;
    }

    @Override
    public void setNetLocationListener(NetLocationListener listener) {
        mNetLocationListener = listener;
    }

    private class LocationLoader extends AsyncTask<String, Void, ResponseBean> {
        private final Gson mGson = new Gson();
        private final WeakReference<NetLocatorSfImpl> mNetLocatorRef;

        LocationLoader(NetLocatorSfImpl netLocator) {
            mNetLocatorRef = new WeakReference<>(netLocator);
        }

        @Override
        protected ResponseBean doInBackground(String... strings) {

            NetLocatorSfImpl netLocator = mNetLocatorRef.get();
            if (netLocator == null || strings == null || strings.length != 1) {
                saveGpsInfo("返回结果-结果为空");
                return null;
            }

            boolean isNetworkConnected = Utils.isNetworkConnected(netLocator.mApplication);
            ResponseBean result;


            String requestUrl = strings[0];
            if (BuildConfig.DEBUG) {
                Log.v(TAG, "Send network request to url:" + requestUrl);
            }

//            String jsonResult = isNetworkConnected ? Utils.get(requestUrl, null, "UTF-8") : null;
            String jsonResult = Utils.get(requestUrl, null, "UTF-8");
            Log.d(TAG, "requestUrl:" + requestUrl + "\njsonResult:" + jsonResult);
            postEventBusLocData("[返回结果-解密前]\n" + requestUrl);
            saveGpsInfo("返回结果-解密前：isNetworkConnected:" + isNetworkConnected + jsonResult);
            if (TextUtils.isEmpty(jsonResult)) {
                if (netLocator.mLastSuccessResponseBean == null) {
                    saveGpsInfo("返回结果null-ERROR_CODE_FAILURE_CONNECTION");
                    result = new ResponseBean();
                    result.setErrCode(String.valueOf(SfMapLocation.ERROR_CODE_FAILURE_CONNECTION));
                } else {
                    saveGpsInfo("返回结果null-返回上次定位结果");
                    result = netLocator.mLastSuccessResponseBean;
                }
                postEventBusLocData("[返回结果]:null,错误码: " + result.getErrCode());
                return result;
            }

            try {
                LbsApiResult lbsApiResult = mGson.fromJson(jsonResult, LbsApiResult.class);
                if (lbsApiResult != null &&
                        lbsApiResult.isSuccess() &&
                        lbsApiResult.getResult() != null &&
                        lbsApiResult.getResult().isSuccess()) {
                    String responseBody = new DesUtil().decrypt(
                            lbsApiResult.getResult().getMsg(),
                            "UTF-8"
                    );
                    saveGpsInfo("返回结果-解密后：" + responseBody);
                    result = mGson.fromJson(responseBody, ResponseBean.class);
                    postEventBusLocData("\n[返回结果-解密后]\n"+responseBody );
                } else {
                    saveGpsInfo("返回结果-报错");
                    result = translateErrorResult(lbsApiResult, netLocator);
                    postEventBusLocData("[返回结果]\n报错,错误码: "+result.getErrCode() );
                }
            } catch (Exception e) {
                result = new ResponseBean();
                result.setErrCode(String.valueOf(SfMapLocation.ERROR_CODE_FAILURE_PARSER));

                e.printStackTrace();

            }

            return result;

        }

        @NonNull
        private ResponseBean translateErrorResult(LbsApiResult lbsApiResult, NetLocatorSfImpl netLocator) {
            ResponseBean result = new ResponseBean();
            if (lbsApiResult != null && lbsApiResult.getResult() != null) {
                LbsApiResultData apiResultData = lbsApiResult.getResult();
                String errMsgEncrypted = apiResultData.getMsg();
                if (!TextUtils.isEmpty(errMsgEncrypted)) {
                    String errMsgDecrypted;
                    try {
                        errMsgDecrypted = new DesUtil().decrypt(errMsgEncrypted, "UTF-8");
                        saveGpsInfo("返回结果-报错：translateErrorResult：" + errMsgDecrypted);
                        postEventBusLocData("[返回结果]\n报错：translateErrorResult：" + apiResultData.getErr());
                        if (errMsgDecrypted == null) {
                            errMsgDecrypted = errMsgEncrypted;
                        }
                    } catch (Exception e) {
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, "Decrypt error message failed.");
                            e.printStackTrace();
                        }
                        //解密错误消息失败，直接用加密消息输出
                        errMsgDecrypted = errMsgEncrypted;
                    }
                    if (!TextUtils.isEmpty(errMsgDecrypted)) {
                        Log.e(TAG, errMsgDecrypted);
                    }

                    Log.e(TAG, String.format("API Error package:%s, sha1:%s, ak:%s.",
                            netLocator.mApplication.getPackageName(),
                            netLocator.mNetworkDataManager.getCertificateSHA1(),
                            netLocator.mApiKey
                    ));
                }

                if (apiResultData.isAuthError()) {
                    result.setErrCode(String.valueOf(SfMapLocation.ERROR_CODE_FAILURE_AUTH));
                    return result;
                }

            }

            result.setErrCode(String.valueOf(SfMapLocation.ERROR_CODE_SERVICE_FAIL));
            return result;
        }

        @Override
        protected void onPostExecute(ResponseBean responseBean) {

            NetLocatorSfImpl netLocator = mNetLocatorRef.get();
            if (netLocator == null) {
                return;
            }

            if (responseBean != null) {
                netLocator.updateLocationResponse(responseBean);
            }
        }
    }

    private void updateLocationResponse(ResponseBean responseBean) {
        if (responseBean != null) {
            if (responseBean.isSuccess()) {
                updateSuccessResponse(responseBean);
            } else if (mLastSuccessResponseBean == null || !mLastSuccessResponseBean.isSuccess()) {
                updateSuccessResponse(responseBean);
            }

            //一次性定位不会有周期性的定位回调，定位结果OK了之后直接返回给用户
            if (mOnceLocation) {
                publishLocationUpdate();
            }
        }


    }

    private void updateSuccessResponse(ResponseBean responseBean) {
        mLastSuccessResponseBean = responseBean;
    }

    private void notifyLocationError(int errorCode) {
        mNetworkDataManager.resetLastRequestBean();
        if (mNetLocationListener != null) {
            mNetLocationListener.onError(errorCode);
        }
    }

    private void publishLocationUpdate() {
        if (!mLocating) {
            return;
        }
        if (mNetLocationListener != null && mLastSuccessResponseBean != null) {
            ResponseBean responseBean = mLastSuccessResponseBean;
            SfMapLocation location = responseBean.getSfLocation();
            if (location != null) {
                this.mNetLocationListener.onNetLocationChanged(location);
            } else {
                this.notifyLocationError(SfMapLocation.ERROR_CODE_SERVICE_FAIL);
            }
        }
    }

    private NetworkDataManager mNetworkDataManager;
    private final NetworkDataManager.OnDataAvailableCallback mOnDataAvailableCallback =
            new NetworkDataManager.OnDataAvailableCallback() {
                //信号变化导致回调频率可能过快的问题,需要根据定位频率来限制
                @Override
                public void onDataAvailable() {
                    long currentUpTime = SystemClock.uptimeMillis();

                    boolean okToHandle = currentUpTime - mLastNetworkRequestTime >= (mIntervalMs / 2);
                    if (okToHandle) {
                        requestBean = mNetworkDataManager.getRequestData();
                        //todo 添加参数
                        sendLocationNetworkRequest();
                    } else {
                        if (BuildConfig.DEBUG) {
                            Log.v(TAG, "Do not handle network data changed.");
                        }
                    }
                }
            };

    private void sendLocationNetworkRequest() {
        String fullUrl = composeLocationRequestUrl();
        if (!TextUtils.isEmpty(fullUrl)) {
            mLastNetworkRequestTime = SystemClock.uptimeMillis();
            new LocationLoader(NetLocatorSfImpl.this).execute(fullUrl);
        } else {
            if (mLastSuccessResponseBean != null) {
                if (BuildConfig.DEBUG) {
                    Log.v(TAG, "Use location cache");
                }
                publishCacheLocation();
            }
        }
    }

    private void publishCacheLocation() {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mLastSuccessResponseBean != null) {
                    SfMapLocation location = mLastSuccessResponseBean.getSfLocation();
                    if (location != null) {
                        mNetLocationListener.onNetLocationChanged(location);
                    }
                }
            }
        });
    }

    private String composeLocationRequestUrl() {
        if (requestBean == null) {
            return null;
        }
        if (!requestBean.isValid()) {
            notifyLocationError(SfMapLocation.ERROR_CODE_NOCGI_WIFIOFF);
            return null;
        }
        requestBean.setNeedAddress(mNeedAddress);
        requestBean.setUseGcj02(mUseGcj02);
        String url = AppInfo.getNetLocationUrl(mApplication);


//        String url = BuildConfig.LOCATION_BACKEND_HOST + BuildConfig.LOCATION_API_PATH;
        Gson gson = new Gson();
        String requestString = gson.toJson(requestBean);
//        String requestString = "{\"appCerSha1\":\"CD:B6:21:64:52:78:42:74:88:E0:0F:03:C9:32:48:ED:5F:74:83:31\",\"appPackageName\":\"com.sfmap.map.internal\",\"celltowers\":[{\"cell_id\":55390883,\"lac\":46970,\"mcc\":460,\"mnc\":1,\"signalstrength\":-65,\"time\":0,\"timingadvance\":0},{\"cell_id\":2147483647,\"lac\":2147483647,\"mcc\":460,\"mnc\":1,\"signalstrength\":-81,\"time\":0,\"timingadvance\":0},{\"cell_id\":61804093,\"lac\":14814,\"mcc\":460,\"mnc\":0,\"signalstrength\":-89,\"time\":0,\"timingadvance\":0},{\"cell_id\":2147483647,\"lac\":2147483647,\"mcc\":460,\"mnc\":0,\"signalstrength\":-95,\"time\":0,\"timingadvance\":0}],\"gcj02\":0,\"netType\":0,\"opt\":1,\"wifilist\":[]}";

        String log = "请求参数-网络：requestString:" + requestString;
        Log.d(TAG, log);
        saveGpsInfo(log);
        String encryptData = new DesUtil().encrypt(requestString);
        if (isLocationRequestLogEnabled()) {
            Log.v(TAG, "Network location request is --> \n" + requestString);
            sendRequestBroadcast(requestBean, gson);
        }
        String fullUrl = url + "?param=" + encryptData + "&type=2&ak=" + mApiKey;
        postEventBusLocData(String.format("\n[请求参数] \nUrl: %1$s" +
                        "\nSha1: %2$s\nAK: %3$s\n包名: %4$s\n",
                url,requestBean.getAppCerSha1(),mApiKey,requestBean.getAppPackageName()));
        return fullUrl;
    }

    private boolean isLocationRequestLogEnabled() {
        return mApplication.getResources().getBoolean(R.bool.enable_location_request_log);
    }

    private void sendRequestBroadcast(RequestBean requestBean, Gson gson) {
        Intent intent = new Intent(SfMapLocationClient.ACTION_NAME_NETWORK_LOCATION_REQUEST);
        List<CellTowerBean> cellTowers = requestBean.getCelltowers();

        StringBuilder stringBuilder = new StringBuilder();
        if (cellTowers != null) {
            for (CellTowerBean cellTower : cellTowers) {
                stringBuilder.append(gson.toJson(cellTower));
                stringBuilder.append("\n");
            }
        }
        intent.putExtra("cellIds", stringBuilder.toString());

        List<WifiDeviceBean> wifis = requestBean.getWifilist();
        int wifiApCount = wifis == null ? 0 : wifis.size();
        intent.putExtra("wifiApCount", wifiApCount);

        mApplication.sendBroadcast(intent);
    }

    public NetLocatorSfImpl(Context context) {
        mApplication = (Application) context.getApplicationContext();

    }

    private void initNetWorkDataManager(Context context) {
        mNetworkDataManager = NetworkDataManager.singleton(context);
    }

    public void saveGpsInfo(String info) {
        if (mTraceEnable) {
            Utils.saveGpsInfo(info);
        }
    }

    public void postEventBusLocData(String info) {
        Log.d("返回数据", "返回数据:" + info);
        EventBus.getDefault().post(info);
    }
}
