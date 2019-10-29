package com.sfmap.api.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.sfmap.api.location.client.cache.OfflineCache;
import com.sfmap.api.location.client.impl.GpsLocator;
import com.sfmap.api.location.client.impl.GpsRegeoHandler;
import com.sfmap.api.location.client.impl.NetLocatorSfImpl;
import com.sfmap.api.location.client.impl.NetLocatorSystemImpl;
import com.sfmap.api.location.client.util.CoordinateTransformUtil;
import com.sfmap.api.location.client.util.NetLocationListener;
import com.sfmap.api.location.client.util.NetLocator;
import com.sfmap.api.location.client.util.PRNGFixes;
import com.sfmap.api.location.client.util.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.sfmap.api.location.client.util.Utils.getGpsLoaalTime;

/**
 * 单例内部类实现
 */
class SfMapLocationClientImpl {
    private static final String TAG = "SfMapLocationClientImpl";
    private static final String AK_META_KEY = "com.sfmap.apikey";
    private static final long DEFAULT_GPS_MIN_DISTANCE = 0;

    private static volatile SfMapLocationClientImpl sSfMapLocationClientImpl;
    private final Application mApplication;
    private final List<SfMapLocationClient> clients = new CopyOnWriteArrayList<>();
    private final Map<SfMapLocationClient, SfMapLocationClientOption> cachedOptions = new HashMap<>();
    private final LocationManager mSystemLocationManager;
    private final GpsLocator mGpsLocator;
    private final NetLocator mNetLocator;
    private final String mApiKey;
    private SfMapLocationClientOption mLocationOption;
    private boolean mDestroyed;
    private boolean mGpsOutOfService = true;
    private final GpsRegeoHandler mGpsRegeoHandler = new GpsRegeoHandler();
    private int gpsSatellites = 0;

    /*package */
    static SfMapLocationClientImpl singleton(Context context) {
        if(sSfMapLocationClientImpl != null) {
            return sSfMapLocationClientImpl;
        }

        synchronized (SfMapLocationClientImpl.class) {
            if(sSfMapLocationClientImpl == null) {
                sSfMapLocationClientImpl = new SfMapLocationClientImpl(context);
            }

            return sSfMapLocationClientImpl;
        }
    }

    /* package */ synchronized void addClient(SfMapLocationClient client, SfMapLocationClientOption locationOption) {
        if(client == null) {
            throw new NullPointerException("Client must not by null");
        }

        if(locationOption == null) {
            throw new NullPointerException("Options must not by null");
        }


        if(!clients.contains(client)) {
            clients.add(client);
            cachedOptions.put(client, locationOption);
            setLocationOption(locationOption);
        }
    }

    /* package */ synchronized void removeClient(SfMapLocationClient client) {
        if(client == null) {
            throw new NullPointerException("Client must not by null");
        }

        if(clients.contains(client)) {
            clients.remove(client);
        }

        stopLocation();
        if(!clients.isEmpty()) {
            stopLocation();
            SfMapLocationClient lastClient = clients.get(clients.size() - 1);
            SfMapLocationClientOption lastOption = cachedOptions.get(lastClient);
            setLocationOption(lastOption);
            if(lastClient.isStarted()) {
                startLocation();
            }
        }
    }

    private SfMapLocationClientImpl(Context context) {
        PRNGFixes.apply();
        mApplication = (Application) context.getApplicationContext();
        mApiKey = getApiKey(context);
        //顺丰网络定位只支持中国大陆的数据，港澳台使用系统内置的网络定位
        if(Utils.isCnOperator(context)) {
            mNetLocator = new NetLocatorSfImpl(context);
        } else {
            mNetLocator = new NetLocatorSystemImpl(context);
        }

        mNetLocator.setApiKey(mApiKey);
        mNetLocator.setNetLocationListener(new NetLocationListener() {
            @Override
            public void onNetLocationChanged(SfMapLocation location) {
                if(mGpsOutOfService) {
                    location.setmSatellites(gpsSatellites);
                    notifyAllClient(location);
                } else {
                    if(BuildConfig.DEBUG) {
                        Log.w(TAG,"Net locator emit location while gps is available");
                    }
                }
            }

            @Override
            public void onError(int errorCode) {
                if(mGpsOutOfService) {
                    notifyLocationError(errorCode);
                } else {
                    if(BuildConfig.DEBUG) {
                        Log.w(TAG,"Net locator emit locating error while gps is available");
                    }
                }
            }
        });



        mSystemLocationManager = (LocationManager) mApplication.getSystemService(Context.LOCATION_SERVICE);

        mGpsLocator = GpsLocator.singleton(context);
        mGpsLocator.setGpsLocationListener(new GpsLocator.GpsLocationListener() {
            @Override
            public void onGpsLocationChanged(Location location) {
                //GPS 定位成功，停止网络定位，GPS无法定位时，会有重启网络定位
                stopNetLocator();
                if(useGcj02()) {
                    wgs84ToGcj02(location);
                }
                mGpsOutOfService = false;
                if(needAddress()) {
                    mGpsRegeoHandler.setContext(mApplication);
                    mGpsRegeoHandler.setApiKey(mApiKey);
                    regeoLocation(location);
                } else {
                    SfMapLocation sfMapLocation = new SfMapLocation(location);
                    sfMapLocation.setmSatellites(gpsSatellites);
                    notifyAllClient(sfMapLocation);
                }
            }

            @Override
            public void onGpsOutOfService() {
                if(isDeviceOnly()) {
                    return;
                }

                if(!mGpsOutOfService) {
                    mGpsOutOfService = true;

                    startNetLocator();
                }
            }

            @Override
            public void onGpsSatellite(int availableCount) {
                gpsSatellites = availableCount;
            }
        });

        mGpsRegeoHandler.setResultCallback(new GpsRegeoHandler.ResultCallback() {

            @Override
            public void onRegeoResult(SfMapLocation location) {
                notifyAllClient(location);
            }
        });

    }

    private boolean isDeviceOnly() {
        return mLocationOption != null &&
                mLocationOption.getLocationMode() == SfMapLocationClientOption.SfMapLocationMode.Device_Sensors;
    }

    private void regeoLocation(Location location) {
        SfMapLocation sfMapLocation = new SfMapLocation(location);
        sfMapLocation.setmSatellites(gpsSatellites);
        mGpsRegeoHandler.regeoLocation(sfMapLocation);
    }

    private boolean needAddress() {
        return mLocationOption != null && mLocationOption.isNeedAddress();
    }

    private boolean useGcj02() {
        return mLocationOption == null || mLocationOption.isUseGcj02();
    }

    private void wgs84ToGcj02(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        double[] transformed = CoordinateTransformUtil.wgs84togcj02(longitude, latitude);
        location.setLatitude(transformed[1]);
        location.setLongitude(transformed[0]);
    }

    @SuppressLint("LogNotTimber")
    private String getApiKey(Context context) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            return bundle.getString(AK_META_KEY);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
        }
        return null;
    }


    public void startLocation() {

        if(checkPrecondition()) {
            mGpsOutOfService = true;
            SfMapLocationClientOption.SfMapLocationMode mode = mLocationOption.getLocationMode();
            if(mode == SfMapLocationClientOption.SfMapLocationMode.High_Accuracy) {
                startNetLocator();
                startGpsLocator();
            } else if(mode == SfMapLocationClientOption.SfMapLocationMode.Battery_Saving) {
                startNetLocator();
            } else {
                startGpsLocator();
            }
        } else {
            Log.v(TAG, "Precondition not ok, ignore start location");
        }
    }

    private void setLocationOption(SfMapLocationClientOption option) {
        mLocationOption = option;
    }

    synchronized void destroyClient(SfMapLocationClient client) {
        if(clients.contains(client)) {
            removeClient(client);
        }

        cachedOptions.remove(client);

        if(clients.isEmpty()) {
            destroy();
            sSfMapLocationClientImpl = null;
        }
    }


    private void destroy() {
        mDestroyed = true;
        mGpsLocator.stopLocation();
        mGpsLocator.setGpsLocationListener(null);
        mGpsLocator.destroy();
        mNetLocator.stopLocation();
        mNetLocator.setNetLocationListener(null);
        mNetLocator.destroy();
    }

    private void stopLocation() {
        stopGpsLocator();
        stopNetLocator();
    }

    private void stopGpsLocator() {
        mGpsLocator.stopLocation();
    }

    private void startGpsLocator() {
        mGpsLocator.setIsOnce(mLocationOption.isOnceLocation());
        mGpsLocator.startLocation(mLocationOption.getInterval(), DEFAULT_GPS_MIN_DISTANCE);
    }

    private boolean checkPrecondition() {
        if(mApplication == null) {
            notifyLocationError(SfMapLocation.ERROR_CODE_INVALID_PARAMETER);
            return false;
        }

        if(TextUtils.isEmpty(mApiKey)) {
            notifyLocationError(SfMapLocation.ERROR_CODE_INVALID_PARAMETER);
            return false;
        }

        if(mLocationOption == null) {
            notifyLocationError(SfMapLocation.ERROR_CODE_INVALID_PARAMETER);
            return false;
        }

        if(mSystemLocationManager == null) {
            notifyLocationError(SfMapLocation.ERROR_CODE_FAILURE_LOCATION);
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(mApplication.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                notifyLocationError(SfMapLocation.ERROR_CODE_FAILURE_LOCATION_PERMISSION);
                return false;
            }

            if(mApplication.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                notifyLocationError(SfMapLocation.ERROR_CODE_FAILURE_LOCATION_PERMISSION);
                return false;
            }
        }

        if(mDestroyed) {
            throw new IllegalStateException("SfMapLocationClientImpl is destroyed");
        }

        boolean networkConnected = Utils.isNetworkConnected(mApplication);
        boolean gpsProviderEnabled = mSystemLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(!networkConnected && !gpsProviderEnabled) {
            notifyLocationError(SfMapLocation.ERROR_CODE_NOCGI_WIFIOFF);
            return false;
        }

        return true;
    }

    private void notifyLocationError(final int errorCode) {
        SfMapLocation location = offlineCachedLocation(errorCode);
        if(location == null) {
             location = new SfMapLocation(errorCode);
        }
        notifyAllClient(location);
    }

    private SfMapLocation offlineCachedLocation(int errorCode) {

        boolean cacheEnabled = mLocationOption.isLocationCachedEnabled();
        if(!cacheEnabled) {
            return null;
        }

        boolean okOffline = recoverableByErrorCode(errorCode);

        SfMapLocation location = null;
        if(okOffline) {
            location = OfflineCache.singleton(mApplication).getCachedLocation();
        } else {
            OfflineCache.singleton(mApplication).clear();
        }
        return location;
    }

    private boolean recoverableByErrorCode(int errorCode) {
        //不能使用缓存的错误码
        Integer[] deadCodes = new Integer[]{
                SfMapLocation.ERROR_CODE_FAILURE_INIT,
                SfMapLocation.ERROR_CODE_AIRPLANEMODE_WIFIOFF,
                SfMapLocation.ERROR_CODE_FAILURE_LOCATION_PERMISSION,
                SfMapLocation.ERROR_CODE_FAILURE_LOCATION_PARAMETER,
                SfMapLocation.ERROR_CODE_FAILURE_AUTH,
                SfMapLocation.ERROR_CODE_INVALID_PARAMETER
        };

        List<Integer> deadCodeList = Arrays.asList(deadCodes);
        return !deadCodeList.contains(errorCode);
    }

    private synchronized void notifyAllClient(SfMapLocation location) {

        OfflineCache.singleton(mApplication).saveCache(location);
        for(SfMapLocationClient client : clients) {
            try {
                String gps = "返回结果-最终：Provider:"+location.getProvider()+" Longitude:"+location.getLongitude()+" Latitude:"+location.getLatitude()+ " Time:"+ getGpsLoaalTime(location.getTime())+ " Altitude:"+location.getAltitude()+ " adcode:"+location.getmAdcode()+ " Satellites:"+location.getmSatellites() + "\n";
                Utils.saveGpsInfo(gps);
                client.onLocationChanged(location);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(mLocationOption.isOnceLocation()) {
            stopLocation();
        }
    }

    private void stopNetLocator() {
        mNetLocator.stopLocation();
    }

    private void startNetLocator() {
        mNetLocator.setIsOnce(mLocationOption.isOnceLocation());
        mNetLocator.setNeedAddress(mLocationOption.isNeedAddress());
        mNetLocator.setUseGcj02(mLocationOption.isUseGcj02());
        mNetLocator.startLocation(mLocationOption.getInterval());
    }
}
