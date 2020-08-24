package com.sfmap.api.location.demo.view;

import android.Manifest;
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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.sfmap.api.location.SfMapLocation;
import com.sfmap.api.location.SfMapLocationClient;
import com.sfmap.api.location.SfMapLocationClientOption;
import com.sfmap.api.location.SfMapLocationListener;
import com.sfmap.api.location.client.util.AppInfo;
import com.sfmap.api.location.demo.R;
import com.sfmap.api.location.demo.constants.CodeConst;
import com.sfmap.api.location.demo.constants.KeyConst;
import com.sfmap.api.location.demo.controllor.BaseFgActivity;
import com.sfmap.api.location.demo.utils.LogcatFileManager;
import com.sfmap.api.location.demo.utils.SPUtils;
import com.sfmap.api.maps.CameraUpdateFactory;
import com.sfmap.api.maps.LocationSource;
import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SFLocationActivity extends BaseFgActivity {
    private final String TAG = SFLocationActivity.class.getSimpleName();
    private MapView mMapView;
    private MapController mMap;
    private SfMapLocationClient mSfMapLocationClient;
    private TextView tv_time, tv_lat, tv_lon, tv_address, tv_accuracy,
            tv_cell_info, tv_wifi_count, tv_gps_count;
    private LocationSource.OnLocationChangedListener mLocationChangedListener;
    private SFLocationActivity context;
    private boolean isFirstFocus = false;
    private static String[] PERMISSIONS_REQUEST = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private TextView infoTv;
    private MaterialDialog.Builder infoShowDiloag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Android 6.0 之后版本需要动态申请定位权限和存储权限
        context = this;
        requestPermission();
        initSpConfig();//初始读取sp
        setContentView(R.layout.activity_location);
        initStatusBar();
        initTitleBackBt(getIntent().getStringExtra(KeyConst.title));

        mMapView = findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        initView();

        initMapSetting();
        initLocation();
        registerReceiver(locationRequestReceiver,
                new IntentFilter(SfMapLocationClient.ACTION_NAME_NETWORK_LOCATION_REQUEST));
        try {
            LogcatFileManager.getInstance().start(Environment
                    .getExternalStorageDirectory().getAbsolutePath() + "/sflocation");
        } catch (Exception e) {
        }
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

        getTitleRightBt(getString(R.string.location)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                context.startActivityForResult(new Intent(context, ConfigSettingActivity.class),
                        CodeConst.REQUEST_CODE_CONFIG_SET);
            }
        });
        initShowInfoDialog();
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.checkPermission(Manifest.permission.READ_PHONE_STATE, Process.myPid(), Process.myUid())
                    != PackageManager.PERMISSION_GRANTED ||
                    this.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Process.myPid(), Process.myUid())
                            != PackageManager.PERMISSION_GRANTED ||
                    this.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, Process.myPid(), Process.myUid())
                            != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(PERMISSIONS_REQUEST, 1);
            }
        }
    }

    private void initMapSetting() {
        mMap = mMapView.getMap();
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.setTrafficEnabled(false);
        mMap.setLocationSource(new LocationSource() {
            @Override
            public void activate(OnLocationChangedListener onLocationChangedListener) {
                mLocationChangedListener = onLocationChangedListener;
                if (mSfMapLocationClient == null) {
                    mSfMapLocationClient = new SfMapLocationClient(context.getApplicationContext());
                    // 设置定位监听
                    //初始化定位参数
                    SfMapLocationClientOption locationOption = new SfMapLocationClientOption();

                    //设置定位间隔 或者设置单次定位
                    locationOption.setInterval(1000);
                    locationOption.setOnceLocation(false);
//            locationOption.setTraceEnable(true);

                    locationOption.setLocationMode(SfMapLocationClientOption.SfMapLocationMode.Battery_Saving);
                    locationOption.setUseGjc02(true);
                    locationOption.setNeedAddress(true);

                    //设置参数
                    mSfMapLocationClient.setLocationOption(locationOption);
                    mSfMapLocationClient.setLocationListener(new SfMapLocationListener() {
                        @Override
                        public void onLocationChanged(SfMapLocation location) {

                            tv_time.setText("");
                            tv_lat.setText("");
                            tv_lon.setText("");
                            tv_address.setText("");
                            tv_accuracy.setText("");
                            Log.i(TAG, location.toString());
                            if (mLocationChangedListener != null && location != null) {
                                if (location.isSuccessful() && location.getLatitude() > 0) {

                                    String loca = "Provider:" + location.getProvider() + " Longitude:" + location.getLongitude() + " Latitude:" + location.getLatitude() +
                                            " Time:" + getGpsLocalTime(location.getTime()) + " Altitude:" + location.getAltitude() + " adcode:" + location.getmAdcode() + " Satellites:" + location.getmSatellites() + "\n";

                                    mLocationChangedListener.onLocationChanged(location);
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
                                    tv_time.setText(getGpsLocalTime(location.getTime()) + " (" + location.getProvider() + ")");
                                    tv_lat.setText(location.getLatitude() + "");
                                    tv_lon.setText(location.getLongitude() + "");
                                    tv_address.setText(location.getAddress());
                                    tv_accuracy.setText(location.getAccuracy() + "米");

                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.location_failed_with_errorcode + location.getErrorCode(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
                    mSfMapLocationClient.startLocation();
                }
            }

            @Override
            public void deactivate() {
                mLocationChangedListener = null;
                if (mSfMapLocationClient != null) {
                    mSfMapLocationClient.stopLocation();
                }
            }
        });
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(11));//115.844441,
        mMap.setMapCenter(new LatLng(30.523451, 114.328784));//114.328784,30.523451
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
            // TODO:是否需要   ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.addGpsStatusListener(statusListener);
        //1000位最小的时间间隔，1为最小位移变化；也就是说每隔1000ms会回调一次位置信息
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1,
                new LocationListener() {
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
            if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
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
            tv_gps_count.setText(availableCount + "颗");
        }
    };

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

    private String getGpsLocalTime(long gpsTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(gpsTime);
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String datestring = df.format(calendar.getTime());
        return datestring;
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private String msgTotal;
    private String msgTotalTag;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMsgEvent(String msgStr) {
        if (msgStr.contains("请求参数")) {
            msgTotalTag = msgTotal;

            msgTotal = msgStr;
        } else {
            msgTotal = msgTotal + "\n" + msgStr;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == CodeConst.REQUEST_CODE_CONFIG_SET &&
                resultCode == CodeConst.RESULT_CODE_CONFIG_SET) {
            if (mSfMapLocationClient != null) {
                resetConfigData(intent);
                mSfMapLocationClient.startLocation();
            }
        }
    }

    private void resetConfigData(Intent intent) {
        String api_url = intent.getStringExtra(KeyConst.api_url);
        String urlPre = api_url.equals("gis.sf-express.com") ? "https://" : "http://";

        String sha1 = intent.getStringExtra(KeyConst.sha1);
        String apiKey = intent.getStringExtra(KeyConst.apiKey);
        String pkgName = intent.getStringExtra(KeyConst.pkgName);

        api_url=urlPre + api_url + "/nloc/locationapi";
        AppInfo.setSpUrl(api_url);
        AppInfo.setSha1(sha1);
        AppInfo.setApiKey(apiKey);
        AppInfo.setPackageName(pkgName);

        SPUtils.put(context, KeyConst.SP_SHA1, sha1);
        SPUtils.put(context, KeyConst.SP_URL, api_url);
        SPUtils.put(context, KeyConst.SP_AK, apiKey);
        SPUtils.put(context, KeyConst.SP_PKG_NAME, pkgName);
    }

    public void onInfoShowClick(View view) {
        showInfoDialog();
    }

    private void initShowInfoDialog() {
        View layout = LayoutInflater.from(context).inflate(R.layout.layout_dialog_show_info, null);
        infoTv = layout.findViewById(R.id.info_tv);
        infoTv.setMovementMethod(ScrollingMovementMethod.getInstance());
        infoTv.setTextIsSelectable(true);
        infoShowDiloag = new MaterialDialog.Builder(context)
                .positiveText(R.string.sure)
                .positiveColorRes(R.color.mainColor)
                .negativeColorRes(R.color.mainColor).title(getString(
                        R.string.info_show_dialog_title)).titleGravity(GravityEnum.CENTER)
                .customView(layout, false);

    }

    private void showInfoDialog() {
        if (infoShowDiloag != null) {
            infoTv.setText(msgTotalTag);
            infoShowDiloag.show();
        }

    }

    private void initSpConfig() {
        String SP_URL = (String) SPUtils.get(this, KeyConst.SP_URL, "");
        String SP_AK = (String) SPUtils.get(this, KeyConst.SP_AK, "");
        String SP_SHA1 = (String) SPUtils.get(this, KeyConst.SP_SHA1, "");
        String SP_PKG_NAME = (String) SPUtils.get(this, KeyConst.SP_PKG_NAME, "");

        if (!TextUtils.isEmpty(SP_AK)) {
            AppInfo.setSpUrl(SP_URL);
            AppInfo.setApiKey(SP_AK);
            AppInfo.setSha1(SP_SHA1);
            AppInfo.setPackageName(SP_PKG_NAME);
        }

    }
}
