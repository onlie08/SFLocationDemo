package com.sfmap.api.location.demo.controllor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sfmap.api.location.demo.utils.ToastUtil;


/**
 *Gool
 */
public class ConnectionChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
                ToastUtil.show(context, "当前网络不可用");
            } else {
                //    ToastShow.Toast(context, "网络可用");
                //    Log.d("ConnectionChangeReceiver","网络可用");
                //  JPushInterface.init(context); //重新调用jpush,得到消息
            }

        }
    }

}
