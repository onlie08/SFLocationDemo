package com.sfmap.api.location.demo.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.sfmap.api.location.demo.BaseFgActivity;
import com.sfmap.api.location.demo.R;
import com.sfmap.api.location.demo.constants.KeyConst;
import com.sfmap.api.location.demo.controllor.GpsService;
import com.sfmap.api.location.demo.utils.TextUtil;
import com.sfmap.api.location.demo.utils.ToastUtil;
import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class GPSActivity extends BaseFgActivity implements GpsService.addLocationListener,
        GpsService.addGPSListener {
    private MapView mMapView;
    private MapController mMap;
    private GPSActivity context;
    private TextView infoTv;
    private Context mContext;
    private Location location;
    LayoutInflater mInflater = null;
    LocationManager mLocationManager = null;
    final int OUT_TIME = 150 * 1000;
    final int MIN_SAT_NUM = 4;

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: ");
            gpsService = ((GpsService.LocalBinder) service).getService();
            if (gpsService != null) {
                gpsService.setOnAddLocationListener(context);
                gpsService.setAddGPSListener(context);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //toast("bind fail!");
        }
    };
    private GpsService gpsService;
    private ArrayList<String> satelliteList;

    private String infoTag = "";
    private String file_name_date = "/sf_gps_loc_info.txt";
    private String gpsFilePath;
    private PowerManager.WakeLock wakeLock;

    //保存文件到sd卡
    public void createGpsInfoFile(String content) {
        BufferedWriter out = null;
        //获取SD卡状态
        String state = Environment.getExternalStorageState();
        //判断SD卡是否就绪
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            ToastUtil.show(context, "请检查是否有SD卡");
            return;
        }
        //取得SD卡根目录
        File file = Environment.getExternalStorageDirectory();
        try {
            if (!file.exists()) {
                return;
            }
            //参数1：可以是File对象 也可以是文件路径;参数2：默认为False=>覆盖内容； true=>追加内容
            gpsFilePath = file.getCanonicalPath()
                    + file_name_date;
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(gpsFilePath, true)));
            out.newLine();
            out.write(content);

            Log.d(TAG, "文件写入成功：");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static int getFileSize(File file) {
        long size = 0;
        try {
            size = new FileInputStream(file).available();
        } catch (Exception e) {
        }
        return (int) size / 1048576;
    }

    /**
     * 使用FileWriter进行文本内容的追加
     *
     * @param content
     */
    private void appendFile(String content) {
        File file = new File(gpsFilePath);
        if (!file.exists()) {
            return;
        }
        int fileSize = getFileSize(file);
        FileWriter writer = null;
        try {
            //true是追加内容，false是覆盖
            writer = new FileWriter(file, fileSize > 1 ? false : true);
            writer.write("\r\n");//换行
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //申请存储权限
    private void checkWritePermission(String info) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0以上
            int permission = ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.RECEIVE_SMS);
            int permission1 = ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int permission2 = ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.READ_EXTERNAL_STORAGE);
            int granted = PackageManager.PERMISSION_GRANTED;
            if (permission != granted && permission1 != granted && permission2 != granted) {
                //没有获取权限
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.RECEIVE_SMS) &&
                        ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.READ_EXTERNAL_STORAGE) &&
                        ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.RECEIVE_SMS,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                } else {
                    //已禁止，
                    Log.d(TAG, "已禁止权限");
                    ToastUtil.show(context, "您已禁止手机存储权限");
                    return;
                }
            }
        }
        if (TextUtils.isEmpty(gpsFilePath)) {
            createGpsInfoFile(info);
        } else {
            appendFile(info);
        }
    }

    @Override
    public void onAddLocation(Location location) {
        if (location != null && context != null) {
            String info = String.format("时间:%1$s\n坐标:%2$s,%3$s\n\n", TextUtil.getFormatTime(
                    location.getTime()), location.getLatitude() + "", location.getLongitude() + "");
            infoTag = info + infoTag;
            int lineCount = infoTv.getLineCount();
            if (lineCount > 100) {
                infoTag = "";
            }
            infoTv.setText(infoTag);

            checkWritePermission(info);
        }
    }

    @Override
    public void onAddGPS(ArrayList<String> gpsList) {
        Log.d(TAG, ",数据回调OnAddGPS");
        for (String info : gpsList) {
        }
        satelliteList = gpsList;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_gps);
        initStatusBar();
        initTitleBackBt(getIntent().getStringExtra(KeyConst.title));

        checkGPS();


        registerOnOffReceiver();
        initView();
        // startService();
        bindService();
    }



   /* *//**
     * 使用JobScheduler进行保活
     *//*
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void useJobServiceForKeepAlive() {
        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler == null) {
            return;
        }
        jobScheduler.cancelAll();
        JobInfo.Builder builder = new JobInfo.Builder(1024, new ComponentName(getPackageName(),
                GpsService.class.getName()));
        //周期设置为了2s
        builder.setPeriodic(1000 * 10);
        builder.setPersisted(true);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        int schedule = jobScheduler.schedule(builder.build());
        if (schedule <= 0) {

        }
    }*/
    private void checkGPS() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //判断GPS是否正常启动
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            ToastUtil.show(context, "请开启GPS导航");
            //返回开启GPS导航设置界面
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 0);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    ToastUtil.show(context, "GPS功能未打开");
                }
            }
            finish();
        } else if (requestCode == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "获取到了权限");
            } else {
                // 没有获取到权限，做特殊处理
                Log.e(TAG, "没有获取到权限");
                ToastUtil.show(context, "手机存储权限开启");
            }
        }

        bindService();

    }
    protected final String TAG = "我的定位activity";
    private void bindService() {
        Intent intent = new Intent(this, GpsService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "界面进入onStart: ");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "界面进入onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection); //this is a callback to the locationlisteneragent class to bind it to a service
        stopService(new Intent(this, GpsService.class));
    }

    BroadcastReceiver gpsReciever = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(KeyConst.GPS_GET_SERVICE_RESTART)) {
                Log.d(TAG, "接收到数据:" + intent.getStringExtra(KeyConst.LAT));
            }
        }
    };


    private void registerOnOffReceiver() {
        IntentFilter iF = new IntentFilter();
        iF.addAction(KeyConst.GPS_GET_SERVICE_RESTART);
        registerReceiver(gpsReciever, iF);

        //startService();
    }

    private void startService() {
        startService(new Intent(this, GpsService.class));
    }

    private void initView() {
        infoTv = findViewById(R.id.info_show_tv);
        infoTv.setMovementMethod(ScrollingMovementMethod.getInstance());
        infoTv.setText("正在获取GPS定位...");
    }

    private String msgTotal;
    private String msgTotalShow;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void postGPSMsgEventBus(String msgStr) {
        msgTotal = msgTotal + "\n" + msgStr;
    }
}
