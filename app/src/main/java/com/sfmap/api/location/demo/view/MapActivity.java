package com.sfmap.api.location.demo.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.sfmap.api.mapcore.util.AppInfo;
import com.sfmap.api.location.demo.BaseFgActivity;
import com.sfmap.api.location.demo.R;
import com.sfmap.api.location.demo.constants.CodeConst;
import com.sfmap.api.location.demo.constants.KeyConst;
import com.sfmap.api.location.demo.utils.SPUtils;
import com.sfmap.api.maps.CameraUpdateFactory;
import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MapActivity extends BaseFgActivity {
    private final String TAG = MapActivity.class.getSimpleName();
    private MapView mMapView;
    private MapController mMap;
    private MapActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        initSPConfig();
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
        double lng = AppInfo.getLng();
        double lat = AppInfo.getLat();
        mMap.setMapCenter(new LatLng(lat, lng));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(10));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        initSPConfig();
        if (resultCode == CodeConst.RES_CODE_MAP) {
            if (mMapView != null) {
                initMapSetting();
            }
        }
    }


    public void onInfoShowClick(View view) {
        initShowInfoDialog();
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
    private String msgTotalShow;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void postMapMsgEventBus(String msgStr) {
        if (msgStr.contains("请求参数")) {
            msgTotalShow = msgTotal;
            msgTotal = msgStr;
        } else {
            if (!msgTotal.contains(msgStr)) {
                msgTotal = msgTotal + "\n" + msgStr;
            }
        }
    }

    /*    private String msgTotalShow="";
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void postMapMsgEventBus(String msgStr) {
            Log.OnAddGPSListener("MAP验证", "接受msgTotalTag:"+msgTotalShow);
            if (!msgTotalShow.contains(msgStr)) {
            Log.OnAddGPSListener("MAP验证", "接受msgStr:"+msgStr);
                msgTotalShow = msgTotalShow + msgStr;
            }
        }*/
    private void initShowInfoDialog() {
        View layout = LayoutInflater.from(context).inflate(R.layout.layout_dialog_show_info, null);
        TextView infoTv = layout.findViewById(R.id.info_tv);
        infoTv.setMovementMethod(ScrollingMovementMethod.getInstance());
        infoTv.setTextIsSelectable(true);
        infoTv.setText(msgTotalShow);
        new MaterialDialog.Builder(context)
                .positiveText(R.string.sure)
                .positiveColorRes(R.color.mainColor)
                .negativeColorRes(R.color.mainColor).title(getString(
                R.string.info_show_dialog_title)).titleGravity(GravityEnum.CENTER)
                .customView(layout, false).show();

    }

    protected void initSPConfig() {
        SPUtils.setSPFileName(SPUtils.FILE_NAME_MAP);

        String SP_URL = (String) SPUtils.get(this, KeyConst.SP_URL, "");
        String SP_AK = (String) SPUtils.get(this, KeyConst.SP_AK, "");
        String SP_SHA1 = (String) SPUtils.get(this, KeyConst.SP_SHA1, "");
        String SP_PKG_NAME = (String) SPUtils.get(this, KeyConst.SP_PKG_NAME, "");
        String SP_LNG = (String) SPUtils.get(this, KeyConst.SP_LNG, "0.0");
        String SP_LAT = (String) SPUtils.get(this, KeyConst.SP_LAT, "0.0");

        if (!TextUtils.isEmpty(SP_AK)) {
            AppInfo.setSpUrl(SP_URL);
            AppInfo.setAppMetaApikey(SP_AK);
            AppInfo.setSha1(SP_SHA1);
            AppInfo.setPackageName(SP_PKG_NAME);

            AppInfo.setLat(Double.valueOf(SP_LAT));
            AppInfo.setLng(Double.valueOf(SP_LNG));
        }
    }

}
