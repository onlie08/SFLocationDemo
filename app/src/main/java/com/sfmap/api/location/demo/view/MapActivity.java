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
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.sfmap.api.location.SfMapLocation;
import com.sfmap.api.location.SfMapLocationClient;
import com.sfmap.api.location.SfMapLocationClientOption;
import com.sfmap.api.location.SfMapLocationListener;
import com.sfmap.api.location.client.util.AppInfo;
import com.sfmap.api.location.demo.BaseFgActivity;
import com.sfmap.api.location.demo.R;
import com.sfmap.api.location.demo.constants.CodeConst;
import com.sfmap.api.location.demo.constants.KeyConst;
import com.sfmap.api.location.demo.utils.LogcatFileManager;
import com.sfmap.api.location.demo.utils.SPUtils;
import com.sfmap.api.location.demo.utils.ToastUtil;
import com.sfmap.api.maps.CameraUpdateFactory;
import com.sfmap.api.maps.LocationSource;
import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.model.LatLng;

public class MapActivity extends BaseFgActivity {
    private final String TAG = MapActivity.class.getSimpleName();
    private MapView mMapView;
    private MapController mMap;
    private MapActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_map);
        initStatusBar();
        initTitleBackBt(getIntent().getStringExtra(KeyConst.title));

        mMapView = findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        initView();

        initMapSetting();
    }

    private void initView() {
        getTitleRightBt(getString(R.string.location)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ConfigSettingActivity.class);
                i.putExtra(KeyConst.type, CodeConst.REQ_CODE_MAP);
                context.startActivityForResult(i, CodeConst.REQ_CODE_LOC);
            }
        });
    }

    private void initMapSetting() {
        mMap = mMapView.getMap();
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.setTrafficEnabled(false);
        // mMap.moveCamera(CameraUpdateFactory.zoomTo(11));

        double lng = AppInfo.getLng();
        double lat = AppInfo.getLat();
        mMap.setMapCenter(new LatLng(lat, lng));
        Log.d("经纬度", lat + "经纬度定位:" + lng);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
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


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == CodeConst.RES_CODE_MAP) {
            if (mMap != null) {
                double lng = AppInfo.getLng();
                double lat = AppInfo.getLat();

                initMapSetting();
            }
        }
    }

}
