package com.sfmap.api.location.client.impl;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import com.sfmap.api.location.client.util.NetLocationListener;
import com.sfmap.api.location.client.util.NetLocator;
import com.sfmap.api.location.client.util.NetworkDataManager;
import com.sfmap.api.location.client.util.Utils;

import java.lang.ref.WeakReference;
import java.util.List;

import static com.sfmap.api.location.client.util.Utils.getGpsLoaalTime;

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
    private RequestBean requestBean;

    @Override
    public void startLocation(long intervalMs) {
        mIntervalMs = intervalMs;
        if(mNetworkDataManager == null) {
            initNetWorkDataManager(mApplication);
        }
        if(mNetworkDataManager != null) {
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

        if(isAirPlaneModeOn()) {
            notifyLocationError(SfMapLocation.ERROR_CODE_AIRPLANEMODE_WIFIOFF);
            return;
        }

        //没有相关权限时，可能导致NetworkDataManager为空
        if(mNetworkDataManager == null) {
            notifyLocationError(SfMapLocation.ERROR_CODE_FAILURE_LOCATION_PERMISSION);
        } else {
            //用户设置了比定时时间（扫描+网络请求）小的定位间隔,可能导致真实的定位（扫描+网络请求）间隔
            //比用户设置的间隔小
            if(!mNetworkDataManager.startNetworkScan()) {
                if(BuildConfig.DEBUG) {
                    Log.v(TAG, "Start network scan request failed."+Build.VERSION.SDK_INT);
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
        mLastSuccessResponseBean = null;
        mUiHandler.removeMessages(MSG_LOOP_NETWORK_LOCATION);
        if(mNetworkDataManager != null) {
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
    public void setNetLocationListener(NetLocationListener listener) {
        mNetLocationListener  = listener;
    }

    private static class LocationLoader extends AsyncTask<String, Void, ResponseBean> {
        private final Gson mGson = new Gson();
        private final WeakReference<NetLocatorSfImpl> mNetLocatorRef;

        LocationLoader(NetLocatorSfImpl netLocator) {
            mNetLocatorRef = new WeakReference<>(netLocator);
        }

        @Override
        protected ResponseBean doInBackground(String... strings) {

            NetLocatorSfImpl netLocator = mNetLocatorRef.get();
            if(netLocator == null || strings == null || strings.length != 1) {
                Utils.saveGpsInfo("返回结果-结果为空");
                return null;
            }

            boolean isNetworkConnected = Utils.isNetworkConnected(netLocator.mApplication);
            ResponseBean result;


            String requestUrl = strings[0];
            if(BuildConfig.DEBUG) {
                Log.v(TAG, "Send network request to url:" + requestUrl);
            }

//            String jsonResult = isNetworkConnected ? Utils.get(requestUrl, null, "UTF-8") : null;
            String jsonResult = Utils.get(requestUrl, null, "UTF-8") ;
            Utils.saveGpsInfo("返回结果-解密前：isNetworkConnected:"+isNetworkConnected + jsonResult);
            if(TextUtils.isEmpty(jsonResult)) {
                if(netLocator.mLastSuccessResponseBean == null) {
                    Utils.saveGpsInfo("返回结果null-ERROR_CODE_FAILURE_CONNECTION");
                    result = new ResponseBean();
                    result.setErrCode(String.valueOf(SfMapLocation.ERROR_CODE_FAILURE_CONNECTION));
                } else {
                    Utils.saveGpsInfo("返回结果null-返回上次定位结果");
                    result = netLocator.mLastSuccessResponseBean;
                }
                return result;
            }

            try {
                LbsApiResult lbsApiResult  =  mGson.fromJson(jsonResult, LbsApiResult.class);
                if (lbsApiResult != null &&
                        lbsApiResult.isSuccess() &&
                        lbsApiResult.getResult() != null &&
                        lbsApiResult.getResult().isSuccess()) {
                    String responseBody = new DesUtil().decrypt(
                            lbsApiResult.getResult().getMsg(),
                            "UTF-8"
                    );
                    Utils.saveGpsInfo("返回结果-解密后："+ responseBody);
                    result = mGson.fromJson(responseBody, ResponseBean.class);
                } else {
                    Utils.saveGpsInfo("返回结果-报错");
                    result = translateErrorResult(lbsApiResult, netLocator);
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

            if(lbsApiResult != null && lbsApiResult.getResult() != null) {
                LbsApiResultData apiResultData = lbsApiResult.getResult();
                String errMsgEncrypted = apiResultData.getMsg();
                Utils.saveGpsInfo("返回结果-报错：translateErrorResult："+errMsgEncrypted);
                if(!TextUtils.isEmpty(errMsgEncrypted)) {
                    String errMsgDecrypted;
                    try {
                        errMsgDecrypted = new DesUtil().decrypt(errMsgEncrypted, "UTF-8");
                        if(errMsgDecrypted == null) {
                            errMsgDecrypted = errMsgEncrypted;
                        }
                    } catch (Exception e) {
                        if(BuildConfig.DEBUG) {
                            Log.e(TAG, "Decrypt error message failed.");
                            e.printStackTrace();
                        }
                        //解密错误消息失败，直接用加密消息输出
                        errMsgDecrypted = errMsgEncrypted;
                    }
                    if(!TextUtils.isEmpty(errMsgDecrypted)) {
                        Log.e(TAG, errMsgDecrypted);
                    }

                    Log.e(TAG, String.format("API Error package:%s, sha1:%s, ak:%s.",
                            netLocator.mApplication.getPackageName(),
                            netLocator.mNetworkDataManager.getCertificateSHA1(),
                            netLocator.mApiKey
                            ));
                }

                if(apiResultData.isAuthError()) {
                    result.setErrCode(String.valueOf(SfMapLocation.ERROR_CODE_FAILURE_AUTH));
                    return result;
                }

            }

            result.setErrCode(String.valueOf(SfMapLocation.ERROR_CODE_SERVICE_FAIL));
            return result;
        }

        @Override
        protected void onPostExecute(ResponseBean responseBean) {
            String jsonRequest = mGson.toJson(responseBean);
            Log.i(TAG,"ResponseBean："+jsonRequest);
            NetLocatorSfImpl netLocator = mNetLocatorRef.get();
            if(netLocator == null) {
                return;
            }
            if(responseBean != null) {
                netLocator.updateLocationResponse(responseBean);
            }
        }
    }

    private void updateLocationResponse(ResponseBean responseBean) {
        if(responseBean != null ) {
            if(responseBean.isSuccess()) {
                mLastSuccessResponseBean = responseBean;
            } else if(mLastSuccessResponseBean == null || !mLastSuccessResponseBean.isSuccess()) {
                mLastSuccessResponseBean = responseBean;
            }

            //一次性定位不会有周期性的定位回调，定位结果OK了之后直接返回给用户
            if(mOnceLocation) {
                publishLocationUpdate();
            }
        }


    }

    private void notifyLocationError(int errorCode) {
        mNetworkDataManager.resetLastRequestBean();
        if(mNetLocationListener != null) {
            mNetLocationListener.onError(errorCode);
        }
    }

    private void publishLocationUpdate() {
        if(!mLocating) {
            return;
        }
        if(mNetLocationListener != null && mLastSuccessResponseBean != null) {
            ResponseBean responseBean = mLastSuccessResponseBean;
            SfMapLocation location = responseBean.getSfLocation();
            if(location != null) {
                this.mNetLocationListener.onNetLocationChanged(location);
            } else {
                this.notifyLocationError(SfMapLocation.ERROR_CODE_SERVICE_FAIL);
            }
        }
    }

    private NetworkDataManager mNetworkDataManager;
    private final NetworkDataManager.OnDataAvailableCallback mOnDataAvailableCallback =
            new NetworkDataManager.OnDataAvailableCallback() {
                @Override
                public void onDataAvailable() {
                    requestBean = mNetworkDataManager.getRequestData();
                    sendLocationNetworkRequest();
                }
            };

    private void sendLocationNetworkRequest() {
        String fullUrl = composeLocationRequestUrl();
        if(!TextUtils.isEmpty(fullUrl)) {
            new LocationLoader(NetLocatorSfImpl.this).execute(fullUrl);
        } else {
            if(mLastSuccessResponseBean != null) {
                if(BuildConfig.DEBUG) {
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
        if(requestBean == null) {
            return null;
        }
        if(!requestBean.isValid()) {
            Log.v(TAG, "requestBean.isValid()" + requestBean.isValid());
            notifyLocationError(SfMapLocation.ERROR_CODE_NOCGI_WIFIOFF);
            return null;
        }
        requestBean.setNeedAddress(mNeedAddress);
        requestBean.setUseGcj02(mUseGcj02);
        String url = BuildConfig.LOCATION_BACKEND_HOST + BuildConfig.LOCATION_API_PATH;
        Gson gson = new Gson();
        String requestString = gson.toJson(requestBean);
        String log = "请求参数-网络：requestString:"+requestString;
        Utils.saveGpsInfo(log);
        String encryptData = new DesUtil().encrypt(requestString);
        if(isLocationRequestLogEnabled()) {
            Log.v(TAG, "Network location request is --> \n" + requestString);
            sendRequestBroadcast(requestBean, gson);
        }
        Log.i(TAG,"参数加密后： "+url + "?param=" + encryptData + "&type=2&ak=" + mApiKey);
        return url + "?param=" + encryptData + "&type=2&ak=" + mApiKey;
    }

    private boolean isLocationRequestLogEnabled() {
        return true;
//        return mApplication.getResources().getBoolean(R.bool.enable_location_request_log);
    }

    private void sendRequestBroadcast(RequestBean requestBean, Gson gson) {
        Intent intent = new Intent(SfMapLocationClient.ACTION_NAME_NETWORK_LOCATION_REQUEST);
        List<CellTowerBean> cellTowers = requestBean.getCelltowers();

        StringBuilder stringBuilder = new StringBuilder();
        if(cellTowers != null) {
            for(CellTowerBean cellTower : cellTowers) {
                stringBuilder.append(gson.toJson(cellTower));
                stringBuilder.append("\n");
            }
        }
        intent.putExtra("cellIds", stringBuilder.toString());

        List<WifiDeviceBean> wifis = requestBean.getWifilist();
        StringBuilder stringBuilderw = new StringBuilder();
        if(cellTowers != null) {
            for(CellTowerBean cellTower : cellTowers) {
                stringBuilder.append(gson.toJson(cellTower));
                stringBuilder.append("\n");
            }
        }

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
}
