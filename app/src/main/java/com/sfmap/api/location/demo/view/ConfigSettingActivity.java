package com.sfmap.api.location.demo.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sfmap.api.location.demo.BaseFgActivity;
import com.sfmap.api.location.demo.R;
import com.sfmap.api.location.demo.constants.CodeConst;
import com.sfmap.api.location.demo.constants.KeyConst;
import com.sfmap.api.location.demo.utils.SPUtils;
import com.sfmap.api.location.demo.utils.TextUtil;
import com.sfmap.api.location.demo.utils.ToastUtil;

public class ConfigSettingActivity extends BaseFgActivity {

    private TextView infoTv;
    private EditText urlEt, sha1Et, pkgNameEt, akEt;
    private ConfigSettingActivity context;
    private int TYPE;
    private EditText lngEt, latEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        initStatusBar();
        setContentView(R.layout.activity_config_setting);
        initTitleBackBt(getString(R.string.location));
        TYPE = getIntent().getIntExtra(KeyConst.type, 0);
        initView();

    }

    String netLocationUrl = "";
    String urlSuffix = "";

    private void initView() {
        urlEt = findViewById(R.id.et_url);
        sha1Et = findViewById(R.id.et_sha1);
        akEt = findViewById(R.id.et_ak);
        pkgNameEt = findViewById(R.id.et_pkg_name);

        lngEt = findViewById(R.id.et_lng);
        latEt = findViewById(R.id.et_lat);
        if (TYPE == CodeConst.map_req_code) {
            SPUtils.setSPFileName(SPUtils.FILE_NAME_MAP);
            urlSuffix="/mms/ds";
            netLocationUrl = com.sfmap.api.mapcore.util.AppInfo.getSfMapURL(context);
            sha1Et.setText(com.sfmap.api.mapcore.util.AppInfo.getSHA1(context));
            akEt.setText(com.sfmap.api.mapcore.util.AppInfo.getAppMetaApikey(context));
            pkgNameEt.setText(com.sfmap.api.mapcore.util.AppInfo.getPackageName(context));

            lngEt.setVisibility(View.VISIBLE);
            latEt.setVisibility(View.VISIBLE);

            latEt.setText(com.sfmap.api.mapcore.util.AppInfo.getLat() + "");
            lngEt.setText(com.sfmap.api.mapcore.util.AppInfo.getLng() + "");
        } else {
            SPUtils.setSPFileName(SPUtils.FILE_NAME_LOC);
            urlSuffix= "/nloc/locationapi";
            //定位
            netLocationUrl = com.sfmap.api.location.client.util.AppInfo.getNetLocationUrl(context);

            sha1Et.setText(com.sfmap.api.location.client.util.AppInfo.getSHA1(context));
            akEt.setText(com.sfmap.api.location.client.util.AppInfo.getSystemAk(context));
            pkgNameEt.setText(com.sfmap.api.location.client.util.AppInfo.getPackageName(context));
        }
        int httpsIndex = netLocationUrl.indexOf("//") + 2;
        int sufIndex = netLocationUrl.indexOf(urlSuffix);

        if (httpsIndex < 0 || sufIndex < 0) {
            httpsIndex = 0;
            sufIndex = 0;
        }

        urlEt.setText(netLocationUrl.substring(httpsIndex, sufIndex));
    }

    public void onSureClick(View view) {
        String url = urlEt.getText().toString().trim();
        String sha1 = sha1Et.getText().toString().trim();
        String apiKey = akEt.getText().toString().trim();
        String pkgName = pkgNameEt.getText().toString().trim();

        String lng = lngEt.getText().toString().trim();
        String lat = latEt.getText().toString().trim();
        if (ToastUtil.showCannotEmpty(context, url, "Api地址")) {
            return;
        }
        if (ToastUtil.showCannotEmpty(context, sha1, "sha1值")) {
            return;
        }
        if (ToastUtil.showCannotEmpty(context, apiKey, "AK值")) {
            return;
        }
        if (ToastUtil.showCannotEmpty(context, pkgName, "包名")) {
            return;
        }

        if (TYPE == CodeConst.map_req_code) {
            if (!TextUtil.checkLngLat(lng)) {
                ToastUtil.show(context, "经度数据格式不正确");
                return;
            }
            if (!TextUtil.checkLngLat(lat)) {
                ToastUtil.show(context, "纬度数据格式不正确");
                return;
            }

            //杭州西湖
            //latEt.setText("30.222719");
            //lngEt.setText("120.121288");
            //故宫
            /*latEt.setText("39.917834");
            lngEt.setText("116.397036");*/

            SPUtils.put(context, KeyConst.SP_LNG, lng);
            SPUtils.put(context, KeyConst.SP_LAT, lat);

        }
        String urlPre = url.equals("gis.sf-express.com") ? "https://" : "http://";
        url = urlPre + url + urlSuffix;


        SPUtils.put(context, KeyConst.SP_SHA1, sha1);
        SPUtils.put(context, KeyConst.SP_URL, url);
        SPUtils.put(context, KeyConst.SP_AK, apiKey);
        SPUtils.put(context, KeyConst.SP_PKG_NAME, pkgName);
        //这三个值传回去
        Intent intent = new Intent();
        int RES_CODE = (TYPE == CodeConst.map_req_code ? CodeConst.map_res_code
                : CodeConst.loc_res_code);
        setResult(RES_CODE, intent);
        finish();

    }


}



