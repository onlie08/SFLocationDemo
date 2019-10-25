package com.sfmap.api.location.demo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sfmap.api.location.SfMapLocation;
import com.sfmap.api.location.SfMapLocationClient;
import com.sfmap.api.location.SfMapLocationClientOption;
import com.sfmap.api.location.SfMapLocationListener;
import com.sfmap.api.maps.CameraUpdateFactory;
import com.sfmap.api.maps.LocationSource;
import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.model.LatLng;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SFLocationActivity extends AppCompatActivity implements SfMapLocationListener, LocationSource {
    private final String TAG = SFLocationActivity.class.getSimpleName();
    private MapView mMapView;
    private MapController mMap;
    private SfMapLocationClient mSfMapLocationClient;
    private TextView tv_time, tv_lat, tv_lon, tv_address, tv_accuracy, tv_cell_info, tv_wifi_count, tv_gps_count;
    private OnLocationChangedListener mListener;
    private boolean isFirstFocus = false;
    private static String[] PERMISSIONS_REQUEST = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private String gpsLogPath;

    private BroadcastReceiver locationRequestReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SfMapLocationClient.ACTION_NAME_NETWORK_LOCATION_REQUEST.equals(intent.getAction())) {
                String requestString = intent.getStringExtra("cellIds");
                int wifiApCount = intent.getIntExtra("wifiApCount", 0);
                tv_cell_info.setText(requestString.trim());
                tv_wifi_count.setText(String.format(Locale.CHINA, "扫描到%d个WiFi AP", wifiApCount));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Android 6.0 之后版本需要动态申请定位权限和存储权限
        requestPermission();

        setContentView(R.layout.activity_main);

        mMapView = findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        initView();

        initMapSetting();
        initLocation();
        registerReceiver(locationRequestReceiver, new IntentFilter(SfMapLocationClient.ACTION_NAME_NETWORK_LOCATION_REQUEST));

    }

    private void initView() {
        tv_time = findViewById(R.id.tv_time);
        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_address = findViewById(R.id.tv_address);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_cell_info = findViewById(R.id.tv_cell_info);
        tv_wifi_count = findViewById(R.id.tv_wifi_count);
        tv_gps_count = findViewById(R.id.tv_gps_count);
        gpsLogPath = Environment
                .getExternalStorageDirectory().getAbsolutePath() + "/sflocation/" + getTimeStampFormat("yyyy-MM-dd") +"_"+"_GpsLog.txt";
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.checkPermission(Manifest.permission.READ_PHONE_STATE, Process.myPid(), Process.myUid())
                    != PackageManager.PERMISSION_GRANTED || this.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Process.myPid(), Process.myUid())
                    != PackageManager.PERMISSION_GRANTED || this.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, Process.myPid(), Process.myUid())
                    != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(PERMISSIONS_REQUEST, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "软件退出，运行权限被禁止", Toast.LENGTH_SHORT).show();
                    System.exit(0);
                }
            }
            if (mSfMapLocationClient != null) {
                mSfMapLocationClient.startLocation();
            }
        }
    }

    private void initMapSetting() {
        mMap = mMapView.getMap();
        this.mMap.getUiSettings().setZoomControlsEnabled(false);
        this.mMap.getUiSettings().setCompassEnabled(false);
        this.mMap.setTrafficEnabled(false);
        this.mMap.setLocationSource(this);
        this.mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(11));
        mMap.setMapCenter(new LatLng(30.573123, 114.299945));
//        mMap.setMinZoomLevel(10);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mSfMapLocationClient.destroy();
        unregisterReceiver(locationRequestReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onLocationChanged(SfMapLocation location) {
        tv_time.setText("");
        tv_lat.setText("");
        tv_lon.setText("");
        tv_address.setText("");
        tv_accuracy.setText("");
        Log.i(TAG, location.toString());
        if (mListener != null && location != null) {
            if (location.isSuccessful() && location.getLatitude() > 0) {
                String loca = "Provider:"+location.getProvider()+" Longitude:"+location.getLongitude()+" Latitude:"+location.getLatitude()+ " Time:"+getGpsLoaalTime(location.getTime())+ " Altitude:"+location.getAltitude()+ " adcode:"+location.getmAdcode()+ " Satellites:"+location.getmSatellites() +"\n";
                saveGpsInfo(gpsLogPath,loca);
                mListener.onLocationChanged(location);
                LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
                float r = location.getAccuracy();
                if (!isFirstFocus) {
                    mMap.moveCamera(CameraUpdateFactory.changeLatLng(position));
                    if (r > 200) {
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                    } else {
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(18));
                    }
                    isFirstFocus = true;
                }
                tv_time.setText(getTime() + " (" + location.getProvider() + ")");
                tv_lat.setText(location.getLatitude() + "");
                tv_lon.setText(location.getLongitude() + "");
                tv_address.setText(location.getAddress());
                tv_accuracy.setText(location.getAccuracy() + "米");
            } else {
                Toast.makeText(getApplicationContext(), R.string.location_failed_with_errorcode + location.getErrorCode(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private String getTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mSfMapLocationClient == null) {
            mSfMapLocationClient = new SfMapLocationClient(this.getApplicationContext());
            // 设置定位监听
            //初始化定位参数
            SfMapLocationClientOption locationOption = new SfMapLocationClientOption();

            //设置定位间隔 或者设置单词定位
            locationOption.setInterval(1500);
            locationOption.setOnceLocation(false);

            locationOption.setLocationMode(SfMapLocationClientOption.SfMapLocationMode.High_Accuracy);

            locationOption.setNeedAddress(true);

            //设置参数
            mSfMapLocationClient.setLocationOption(locationOption);
            mSfMapLocationClient.setLocationListener(this);
            mSfMapLocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mSfMapLocationClient != null) {
            mSfMapLocationClient.stopLocation();
        }
    }

    //位置管理器
    private LocationManager locationManager;

    private void initLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //判断GPS是否正常启动
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "请开启GPS导航", Toast.LENGTH_SHORT).show();
            //返回开启GPS导航设置界面

            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 0);
            return;
        }
        //添加卫星状态改变监听
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.addGpsStatusListener(statusListener);
        //1000位最小的时间间隔，1为最小位移变化；也就是说每隔1000ms会回调一次位置信息
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1500, 1, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
    }

    /**
     * 卫星状态监听器
     */
    private GpsStatus mGpsStatus;

    private final GpsStatus.Listener statusListener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) { // GPS状态变化时的回调，如卫星数
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mGpsStatus = locationManager.getGpsStatus(mGpsStatus);
            Iterable<GpsSatellite> satellites = mGpsStatus.getSatellites();
            int availableCount = 0;
            for (GpsSatellite satellite : satellites) {
                if (satellite.usedInFix()) {
                    availableCount++;
                }
            }
            tv_gps_count.setText(availableCount+"颗");
        }
    };

    private void saveGpsInfo(String path,String infos){
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "rw");
            raf.seek(file.length());
            raf.write(infos.getBytes());
            raf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getTimeStampFormat(String format) {
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(dt);
    }

    private String getGpsLoaalTime(long gpsTime){
        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(gpsTime);
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String datestring = df.format(calendar.getTime());

        return datestring;
    }
}
