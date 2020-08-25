package com.sfmap.api.location.demo;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import com.sfmap.api.location.client.util.AppInfo;
import com.sfmap.api.location.demo.constants.KeyConst;
import com.sfmap.api.location.demo.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Dylan
 */
public class App extends Application {

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onCreate() {
        super.onCreate();
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
