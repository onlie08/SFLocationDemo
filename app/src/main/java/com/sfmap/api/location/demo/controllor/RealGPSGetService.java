package com.sfmap.api.location.demo.controllor;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/*
 *  @author Dylan
 */
public class RealGPSGetService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("获取GPS", "数据");
        //设置定时操作
    /*    AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        //1分钟请求一次更新
        int elapseTime = 1 * 1000;
        long interval = SystemClock.elapsedRealtime() + elapseTime;
        //传递userId参数给MyNewTaskReceiver,为了到时候回传回来
        Intent i = new Intent(this, GPSActivity.class);
        i.setAction(KeyConst.GPS_GET_SERVICE_RESTART);
        i.putExtra(KeyConst.LAT, "30.5554656");
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
        am.set(AlarmManager.ELAPSED_REALTIME, interval, pi);*/

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
     /*   Intent intent = new Intent();
        intent.setAction(KeyConst.GPS_GET_SERVICE_RESTART);
        this.sendBroadcast(intent);*/
    }
}
