package com.sfmap.api.location.demo.controllor;
/*
 * Copyright (c) 2013-2014, Qualcomm Technologies, Inc. All Rights Reserved.
 * Qualcomm Technologies Proprietary and Confidential.
 */

import java.util.ArrayList;
import java.util.Iterator;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import android.os.Binder;

public class GpsService extends Service {

    static String TAG = "sfmap7";

    LocationManager locManager = null;
    final int OUT_TIME = 600 * 1000; // 10 mins, then service die

    int index = -1;

    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "开始");
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locManager == null) {
            return;
        }
        //Utils.enableGps(getApplicationContext(), true);
        startGPS();
    }

    @Override
    public void onDestroy() {
        logd("onDestroy");
        finish(false);
        super.onDestroy();
    }

    CountDownTimer mCountDownTimer = new CountDownTimer(OUT_TIME, 5000) {

        @Override
        public void onTick(long arg0) {
            logd("onTick from " + TAG);
        }

        @Override
        public void onFinish() {
            //finish();//todo 停止gps
            //fail(getString(R.string.time_out));
        }
    };

    private static final String GPS_LOCATION_NAME = android.location.LocationManager.GPS_PROVIDER;

    private void startGPS() {
        String provider = android.location.LocationManager.GPS_PROVIDER;
        //判断是否开启GPS定位功能
        boolean isGpsEnabled = locManager.isProviderEnabled(GPS_LOCATION_NAME);
        Log.d(TAG, "startGPS: " + isGpsEnabled);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //请求权限

            return;
        }
        locManager.requestLocationUpdates(provider, 500, 0, mLocationListener);
        locManager.addGpsStatusListener(gpsStatusListener);
        Log.d(TAG, "获取GPS消息:" );
    }

    LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            Log.d(TAG, "获取GPS消息1:" + location);
            if (location != null) {
                if (onAddLocationListener != null) {
                    onAddLocationListener.OnAddLocation(location);
                }
                pass();

            }
        }

        public void onProviderDisabled(String provider) {

        }

        public void onProviderEnabled(String provider) {

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    };

    private GpsStatus mGpsStatus;
    private Iterable<GpsSatellite> mSatellites;
    ArrayList<String> satelliteList = new ArrayList<String>();
    GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener() {

        public void onGpsStatusChanged(int arg0) {
            switch (arg0) {
                case GpsStatus.GPS_EVENT_STARTED:
                    logd("GPS_EVENT_STARTED");
                    mCountDownTimer.start();
                    break;
                case GpsStatus.GPS_EVENT_STOPPED:
                    logd("GPS_EVENT_STOPPED");
                    break;
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    logd("GPS_EVENT_FIRST_FIX");
                    break;
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    logd("GPS_EVENT_SATELLITE_STATUS");
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                            (getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        //请求权限

                        return;
                    }
                    mGpsStatus = locManager.getGpsStatus(null);
                    mSatellites = mGpsStatus.getSatellites();
                    Iterator<GpsSatellite> iterator = mSatellites.iterator();
                    int count = 0;
                    satelliteList.clear();
                    while (iterator.hasNext()) {
                        GpsSatellite gpsSatel = (GpsSatellite) iterator.next();
                        //Log.d(TAG, gpsSatel.getSnr() + ",数据进来," + gpsSatel.getPrn());
                        //有作用的卫星
                        if (gpsSatel.getSnr() > 5) {
                            satelliteList.add(count++, "Prn" + gpsSatel.getPrn() + " Snr:" + gpsSatel.getSnr());
                            logd("Prn" + gpsSatel.getPrn() + " Snr:" + gpsSatel.getSnr());
                        }
                    }

                    if (onAddGPSListener != null) {
                        onAddGPSListener.OnAddGPS(satelliteList);
                    }


                    break;
                default:
                    break;
            }

        }

    };

    private void stopGPS() {
        try {
            if (mLocationListener != null)
                locManager.removeUpdates(mLocationListener);
            if (gpsStatusListener != null)
                locManager.removeGpsStatusListener(gpsStatusListener);
        } catch (Exception e) {
            loge(e);
        }
    }

    public void finish() {
        finish(true);
    }

    public void finish(boolean writeResult) {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            stopGPS();

        }

    }

    void fail(Object msg) {
        loge(msg);
        //result = false;
        // finish();   do thing for this service
    }

    void pass() {
        //result = true;
        //finish();  do thing for this service
    }

    private void loge(Object e) {

        if (e == null)
            return;
        Thread mThread = Thread.currentThread();
        StackTraceElement[] mStackTrace = mThread.getStackTrace();
        String mMethodName = mStackTrace[3].getMethodName();
        e = "[" + mMethodName + "] " + e;
        Log.e(TAG, e + "");
    }

    private void logd(Object s) {

        Thread mThread = Thread.currentThread();
        StackTraceElement[] mStackTrace = mThread.getStackTrace();
        String mMethodName = mStackTrace[3].getMethodName();

        s = "[" + mMethodName + "] " + s;
        Log.d(TAG, s + "");
    }

    //bind return the inner bind object
    @Override
    public IBinder onBind(Intent arg0) {
        logd("onBind");
        return new LocalBinder();
    }

    public final class LocalBinder extends Binder {
        public GpsService getService() {
            logd("LocalBinder getService");
            return GpsService.this;
        }
    }


    public interface OnAddLocationListener {
        void OnAddLocation(Location location);
    }

    private OnAddLocationListener onAddLocationListener;

    public void registenerOnAddLocationListener(OnAddLocationListener aListener) {
        this.onAddLocationListener = aListener;
    }

    public void unregistenerOnAddLocationListener() {
        this.onAddLocationListener = null;
    }


    public interface OnAddGPSListener {
        void OnAddGPS(ArrayList<String> gpslist);
    }

    private OnAddGPSListener onAddGPSListener;

    public void
    registenerOnAddGPSListener(OnAddGPSListener aListener) {
        this.onAddGPSListener = aListener;
    }

    public void unregistenerOnAddGPSListener() {
        this.onAddGPSListener = null;
    }
}
