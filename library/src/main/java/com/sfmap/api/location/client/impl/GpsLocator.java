package com.sfmap.api.location.client.impl;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.location.OnNmeaMessageListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.sfmap.api.location.BuildConfig;
import com.sfmap.api.location.client.util.Utils;

public class GpsLocator implements LocationListener, GpsStatus.Listener {

    private static final String TAG = "GpsLocator";
    private final Application mApplication;
    private long mLastMinDistance;
    private long mLastMinTime;
    private GpsStatus mGpsStatus;
    private static volatile GpsLocator sGpsLocator;
    private final LocationManager mLocationManager;
    private volatile boolean mGpsLocationStart;
    private GpsLocationListener mGpsLocationListener;
    private boolean onceLocation;
    private boolean mGpsOutOfService;

    public void destroy() {
        if(mLocationManager != null) {
            mLocationManager.removeUpdates(this);
            mLocationManager.removeGpsStatusListener(this);
        }
        sGpsLocator = null;
    }

    @Override
    public void onGpsStatusChanged(int event) {
        //Event sent periodically to report GPS satellite status
        if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
            try {
                mGpsStatus = mLocationManager.getGpsStatus(mGpsStatus);
                Iterable<GpsSatellite> satellites = mGpsStatus.getSatellites();
                int availableCount = 0;
                for (GpsSatellite satellite : satellites) {
                    if (satellite.usedInFix()) {
                        availableCount++;
                    }
                }
                if(mGpsLocationListener != null) {
                    mGpsLocationListener.onGpsSatellite(availableCount);
                }
                logV("Available satellite count is:" + availableCount);
                //LocationManager 注册的LocationListener接口在gps状态无法使用时，没有回调
                //必须使用这里的卫星使用数量来判断
                if(availableCount < 3) {
                    notifyGpsOutOfService();
                }
            } catch (SecurityException e) {
                e.printStackTrace();
                //LocationManager.getGpsStatus may throw null pointer exception
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void logV(String message) {
        if(BuildConfig.DEBUG) {
            Log.v(TAG, message);
        }
    }


    public void setIsOnce(boolean onceLocation) {
        this.onceLocation = onceLocation;
    }

    public interface GpsLocationListener {
        void onGpsLocationChanged(Location location);
        void onGpsOutOfService();
        void onGpsSatellite(int availableCount);
    }


    private GpsLocator(Context context) throws SecurityException {
        mApplication = (Application) context.getApplicationContext();
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void setGpsLocationListener(GpsLocationListener listener) {
        mGpsLocationListener = listener;
    }


    public void startLocation(long minTime, long minDistance) throws SecurityException {
        mLastMinTime = minTime;
        mLastMinDistance = minDistance;

        if(mLocationManager == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                mApplication.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //mLocationManager.addGpsStatusListener
        //必须要在有Looper.myLooper()不为空的线程环境中初始化
        //这里我们直接选用MainLooper来做初始化
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if(mGpsLocationStart) {
                        //已经开始
                        return;
                    }
                    mLocationManager.removeGpsStatusListener(GpsLocator.this);
                    mLocationManager.addGpsStatusListener(GpsLocator.this);
                    mLocationManager.addNmeaListener(new GpsStatus.NmeaListener() {
                        @Override
                        public void onNmeaReceived(long timestamp, String nmea) {
                            Log.i(TAG,"timestamp:"+timestamp+ "转换后：" + Utils.getGpsLoaalTime(timestamp) + "\nnmea:\n"+nmea);
                            Utils.saveNmea(nmea);
                        }
                    });

                    //某些机型必须要通过先requestUpdate调用之后才有卫星状态监听回调
                    if (onceLocation) {
                        mLocationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER,
                                GpsLocator.this,
                                Looper.getMainLooper()
                        );
                    } else {
                        mLocationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                mLastMinTime,
                                mLastMinDistance,
                                GpsLocator.this
                        );
                    }
                    mGpsLocationStart = true;
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void stopLocation() {
        if(mGpsLocationStart) {
            mGpsLocationStart = false;
            mLocationManager.removeUpdates(this);
            mLocationManager.removeGpsStatusListener(this);
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        mGpsOutOfService = false;
        if(mGpsLocationListener != null) {
            mGpsLocationListener.onGpsLocationChanged(location);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                notifyGpsOutOfService();
                break;
        }
    }


    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        notifyGpsOutOfService();
    }

    private void notifyGpsOutOfService() {
        if(mGpsOutOfService) {
            return;
        }

        logV("notifyGpsOutOfService()");

        if (mGpsLocationListener != null) {
            mGpsOutOfService = true;
            mGpsLocationListener.onGpsOutOfService();
        }
    }

    public static GpsLocator singleton(Context context) {
        if(sGpsLocator != null) {
            return sGpsLocator;
        }

        synchronized (GpsLocator.class) {
            if(sGpsLocator == null) {
                sGpsLocator = new GpsLocator(context);
            }
            return sGpsLocator;
        }
    }
}
