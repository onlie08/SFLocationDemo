package com.sfmap.api.location.demo.controllor;
/*
 * Copyright (c) 2013-2014, Qualcomm Technologies, Inc. All Rights Reserved.
 * Qualcomm Technologies Proprietary and Confidential.
 */

import java.util.ArrayList;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import android.os.Binder;
import android.widget.Toast;

public class GpsService extends Service {

    protected final String TAG = "我的定位Service";
    LocationManager locManager = null;
    final int OUT_TIME = 6000 * 1000;
    private GpsService context;
    private PowerManager.WakeLock wakeLock;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onCreate() {
        context = this;
        Log.d(TAG, "服务:onCreate");
        startGPS();
        //mCountDownTimer.start();
        keepCPUAlive();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "服务:onDestroy");
        finish(false);
        wakeLock.release();
    }

    //定时获取GPS ,间隔1秒
    CountDownTimer mCountDownTimer = new CountDownTimer(OUT_TIME, 1000) {
        @Override
        public void onTick(long arg0) {
            Log.d(TAG, "--------服务在打印------");
            startGPS();
        }

        @Override
        public void onFinish() {
            //finish();//todo 停止gps
            //fail(getString(R.string.time_out));
        }
    };

    private static final String GPS_LOCATION_NAME = android.location.LocationManager.GPS_PROVIDER;

    private void startGPS() {
        //判断是否开启GPS定位功能
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locManager == null) {
            return;
        }
        boolean isGpsEnabled = locManager.isProviderEnabled(GPS_LOCATION_NAME);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //请求权限
            return;
        }
        locManager.addGpsStatusListener(gpsStatusListener);
        //参数2，位置信息更新周期，单位毫秒
        //参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息
        //备注：参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                200, 0, mLocationListener);

    }

    private LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location loc) {
            Log.d(TAG, "onLocationChanged:" + loc.getLatitude());
            if (loc != null) {
                if (onAddLocationListener != null) {
                    onAddLocationListener.onAddLocation(loc);
                }
                //pass();
            }
        }

        public void onProviderDisabled(String provider) {
            Log.d(TAG, "当前GPS:onProviderDisabled");
        }

        public void onProviderEnabled(String provider) {
            // 当GPS LocationProvider可用时
            Log.d(TAG, "当前GPS:onProviderEnabled");
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d(TAG, "当前GPS为可用状态:");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d(TAG, "当前GPS不在服务内:");

                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d(TAG, "当前GPS为暂停服务状态:");
                    break;

            }
        }
    };

    private GpsStatus mGpsStatus;
    private Iterable<GpsSatellite> mSatellites;
    GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            switch (event) {
                //第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Log.d(TAG, "第一次定位:");
                    break;
                //卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    Log.d(TAG, "卫星状态改变:");
                    if (ActivityCompat.checkSelfPermission(GpsService.this, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                            (GpsService.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        //请求权限

                        return;
                    }
                    mGpsStatus = locManager.getGpsStatus(mGpsStatus);
                    Iterable<GpsSatellite> satellites = mGpsStatus.getSatellites();
                    int availableCount = 0;
                    for (GpsSatellite satellite : satellites) {
                        if (satellite.usedInFix()) {
                            availableCount++;
                        }
                    }
                    //availableCount + "颗";
                    break;
                //定位启动
                case GpsStatus.GPS_EVENT_STARTED:
                    Log.d(TAG, "定位启动:");
                    break;
                //定位结束
                case GpsStatus.GPS_EVENT_STOPPED:
                    Log.d(TAG, "定位结束");
                    break;
                default:
                    Log.d(TAG, "定位停止:其他" + event);
                    break;
            }

        }


    };

    private void stopGPS() {
        Log.d(TAG, "服务关闭GPS stopGPS: ");
        try {
            if (mLocationListener != null)
                locManager.removeUpdates(mLocationListener);
            if (gpsStatusListener != null)
                locManager.removeGpsStatusListener(gpsStatusListener);
        } catch (Exception e) {
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
        //result = false;
        // finish();   do thing for this service
    }

    void pass() {
        //result = true;
        //finish();  do thing for this service
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.d(TAG, "进入onBind方法,返回GPSService给activity");
        return new LocalBinder();
    }

    public final class LocalBinder extends Binder {
        public GpsService getService() {
            return GpsService.this;
        }
    }


    public interface addLocationListener {
        void onAddLocation(Location location);
    }

    private addLocationListener onAddLocationListener;

    public void setOnAddLocationListener(addLocationListener aListener) {
        this.onAddLocationListener = aListener;
    }

    public void unregistenerOnAddLocationListener() {
        this.onAddLocationListener = null;
    }

    public interface addGPSListener {
        void onAddGPS(ArrayList<String> gpslist);
    }

    private GpsService.addGPSListener addGPSListener;

    public void setAddGPSListener(GpsService.addGPSListener aListener) {
        this.addGPSListener = aListener;
    }

    @SuppressLint("InvalidWakeLockTag")
    private void keepCPUAlive() {
        //创建PowerManager对象
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //保持cpu一直运行，不管屏幕是否黑屏
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "CPUKeepRunning");

        wakeLock.acquire();

        //wakeLock.release();
    }
}
