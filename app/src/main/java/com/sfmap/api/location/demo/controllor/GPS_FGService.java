package com.sfmap.api.location.demo.controllor;
/*
 * Copyright (c) 2013-2014, Qualcomm Technologies, Inc. All Rights Reserved.
 * Qualcomm Technologies Proprietary and Confidential.
 */

import java.util.ArrayList;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
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
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import android.os.Binder;

import com.sfmap.api.location.demo.R;
import com.sfmap.api.location.demo.constants.ConfConst;
import com.sfmap.api.location.demo.utils.ToastUtil;

public class GPS_FGService extends Service {

    protected final String TAG = "GPS_FGService";
    LocationManager locManager = null;
    final int OUT_TIME = 6000 * 1000;
    private GPS_FGService context;
    private PowerManager.WakeLock wakeLock;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onCreate() {
        context = this;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setForegroundService();
        }
        startGPS();
        //keepCPUAlive();
        //mCountDownTimer.start();
    }

    /**
     * 通过通知启动服务
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setForegroundService() {
        //设定的通知渠道名称
        //设置通知的重要程度
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        //构建通知渠道
        NotificationChannel channel = new NotificationChannel(ConfConst.CHANNEL_ID, ConfConst.CHANNEL_NAME, importance);
        //在创建的通知渠道上发送通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this.getApplicationContext(),ConfConst.CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_back) //设置通知图标
                .setContentTitle(ConfConst.TITLE)//设置通知标题
                .setContentText(ConfConst.CONTENT)//设置通知内容
                .setAutoCancel(true) //用户触摸时，自动关闭
                .setOngoing(true);//设置处于运行状态
        //向系统注册通知渠道，注册后不能改变重要性以及其他通知行为
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
        //将服务置于启动状态 NOTIFICATION_ID指的是创建的通知的ID
        startForeground(ConfConst.NOTIF_ID, builder.build());
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return new LocalBinder();
    }
    public final class LocalBinder extends Binder {
        public GPS_FGService getService() {
            return GPS_FGService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopGPS();
    }

    //定时获取GPS ,间隔1秒
    private CountDownTimer mCountDownTimer = new CountDownTimer(OUT_TIME, 1000) {
        @Override
        public void onTick(long arg0) {
            //startGPS();
        }

        @Override
        public void onFinish() {
        }
    };

    /*
     * 开启Gps
     */
    private void startGPS() {
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locManager == null) {
            return;
        }
        boolean isGpsEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isGpsEnabled) {
            ToastUtil.show(context, "请开启GPS定位权限");
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //请求权限
            return;
        }

        locManager.addGpsStatusListener(gpsStatusListener);
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, ConfConst.MIN_TIME_GPS,
                0, mLocationListener);

    }

    /*
     * 定位监听,定位数据返回这里
     */
    private LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location loc) {
            Log.d(TAG, "onLocationChanged:" + loc.getLatitude());
            if (loc != null) {
                if (onAddLocationListener != null) {
                    onAddLocationListener.onAddLocation(loc);
                }
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
    /*
     * GPS运行状态监听
     */
    private GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            switch (event) {
                //第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Log.d(TAG, "第一次定位:");
                    break;
                //卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    Log.d(TAG, "卫星状态改变:");
                    if (ActivityCompat.checkSelfPermission(GPS_FGService.this, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                            (GPS_FGService.this, Manifest.permission.ACCESS_COARSE_LOCATION)
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

    /*
     * 停止GPS定位功能
     */
    private void stopGPS() {
        Log.d(TAG, "GPS定位关闭");
        try {
            if (mLocationListener != null)
                locManager.removeUpdates(mLocationListener);
            if (gpsStatusListener != null)
                locManager.removeGpsStatusListener(gpsStatusListener);
        } catch (Exception e) {
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

    private GPS_FGService.addGPSListener addGPSListener;

    public void setAddGPSListener(GPS_FGService.addGPSListener aListener) {
        this.addGPSListener = aListener;
    }

    @SuppressLint("InvalidWakeLockTag")
    private void keepCPUAlive() {
        //创建PowerManager对象
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //保持cpu一直运行，不管屏幕是否黑屏
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "CPUKeepRunning");

        wakeLock.acquire();

        //wakeLock.release();
    }
}
