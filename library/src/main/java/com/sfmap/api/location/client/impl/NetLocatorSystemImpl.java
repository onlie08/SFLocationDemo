package com.sfmap.api.location.client.impl;

import java.util.Locale;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.sfmap.api.location.BuildConfig;
import com.sfmap.api.location.SfMapLocation;
import com.sfmap.api.location.client.util.NetLocationListener;
import com.sfmap.api.location.client.util.NetLocator;

public class NetLocatorSystemImpl implements NetLocator, LocationListener {
    private static final String TAG = "NetLocatorSystemImpl";
    private final LocationManager mLocationManager;
    private final Application mApplication;
    private boolean mIsOnce;
    private NetLocationListener mNetLocationListener;
    private boolean mProviderDisabled;


    public NetLocatorSystemImpl(Context context) {
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mApplication = (Application) context.getApplicationContext();
    }

    @Override
    public void setNetLocationListener(NetLocationListener listener) {
        mNetLocationListener = listener;
    }

    @Override
    public void stopLocation() {
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void startLocation(long intervalMs) {

            if (ActivityCompat.checkSelfPermission(mApplication, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                if(mProviderDisabled) {
                    notifyErrorCode(SfMapLocation.ERROR_CODE_SERVICE_FAIL);
                    return;
                }
                if (mIsOnce) {
                    mLocationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, Looper.getMainLooper());
                } else {
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, intervalMs, 0, this, Looper.getMainLooper());
                }
            } else {
                notifyErrorCode(SfMapLocation.ERROR_CODE_FAILURE_LOCATION_PERMISSION);
            }

    }

    private void notifyErrorCode(int errorCode) {
        if(mNetLocationListener != null) {
            mNetLocationListener.onError(errorCode);
        }
    }

    @Override
    public void setIsOnce(boolean onceLocation) {
        mIsOnce = true;
    }

    @Override
    public void setApiKey(String apiKey) {
        //do nothing
    }

    @Override
    public void destroy() {
        //do nothing
    }

    @Override
    public void setNeedAddress(boolean needAddress) {
        //do nothing
    }

    @Override
    public void setUseGcj02(boolean useGcj02) {
        //中国大陆以外地区直接使用系统内置的网络定位，不做gcj02坐标的切换
    }

    @Override
    public void onLocationChanged(Location location) {
        if(mNetLocationListener != null) {
            mNetLocationListener.onNetLocationChanged(new SfMapLocation(location));
        }
    }

    @SuppressLint("LogNotTimber")
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if(BuildConfig.DEBUG) {
            Log.v(TAG, String.format(Locale.US, "provider(%s) with new status(%s)", provider, status));
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        mProviderDisabled = false;
    }

    @Override
    public void onProviderDisabled(String provider) {
        mProviderDisabled = true;
    }
}
