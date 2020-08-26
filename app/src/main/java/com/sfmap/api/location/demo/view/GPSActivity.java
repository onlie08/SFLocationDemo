package com.sfmap.api.location.demo.view;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.sfmap.api.location.demo.BaseFgActivity;
import com.sfmap.api.location.demo.R;
import com.sfmap.api.location.demo.constants.KeyConst;
import com.sfmap.api.location.demo.controllor.GpsService;
import com.sfmap.api.location.demo.utils.ToastUtil;
import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class GPSActivity extends BaseFgActivity implements GpsService.OnAddLocationListener,
        GpsService.OnAddGPSListener {
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
                gpsService.registenerOnAddLocationListener(context);
                gpsService.registenerOnAddGPSListener(context);

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //toast("bind fail!");
        }
    };
    private GpsService gpsService;
    private ArrayList<String> satelliteList;

    @Override
    public void OnAddLocation(Location location) {
        Log.d(TAG, "获取GPS消息2:" + location);
        Log.d(TAG, "时间：" + location.getTime());
        Log.d(TAG, "经度：" + location.getLongitude());
        Log.d(TAG, "纬度：" + location.getLatitude());
        Log.d(TAG, "海拔：" + location.getAltitude());
    }


    @Override
    public void OnAddGPS(ArrayList<String> gpsList) {
        Log.d(TAG, ",数据回调OnAddGPS");
        for (String info : gpsList) {
        }
        satelliteList = gpsList;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_real_gps);
        initStatusBar();
        initTitleBackBt(getIntent().getStringExtra(KeyConst.title));

        checkGPS();


        registerOnOffReceiver();
        initView();
        // startService();
        bindService();
    }

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
        }

        bindService();
    }

    private void bindService() {
        Intent intent = new Intent(this, GpsService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        // 标志位BIND_AUTO_CREATE :onCreate得到执行,onStartCommand不执行
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        //EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        unbindService(connection); //this is a callback to the locationlisteneragent class to bind it to a service
        stopService(new Intent(this, GpsService.class));
        super.onStop();
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
        infoTv.setText("经纬度,时间");
    }

    private String msgTotal;
    private String msgTotalShow;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void postGPSMsgEventBus(String msgStr) {
        msgTotal = msgTotal + "\n" + msgStr;
    }
}
