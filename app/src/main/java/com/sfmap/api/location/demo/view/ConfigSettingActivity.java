package com.sfmap.api.location.demo.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sfmap.api.location.client.util.AppInfo;
import com.sfmap.api.location.demo.R;
import com.sfmap.api.location.demo.constants.CodeConst;
import com.sfmap.api.location.demo.constants.KeyConst;
import com.sfmap.api.location.demo.controllor.BaseFgActivity;
import com.sfmap.api.location.demo.utils.SPUtils;
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

    private void initView() {
        urlEt = findViewById(R.id.et_url);
        sha1Et = findViewById(R.id.et_sha1);
        akEt = findViewById(R.id.et_ak);
        pkgNameEt = findViewById(R.id.et_pkg_name);

        lngEt = findViewById(R.id.et_lng);
        latEt = findViewById(R.id.et_lat);

        String netLocationUrl = AppInfo.getNetLocationUrl(context);
        int httpsIndex = netLocationUrl.indexOf("//") + 2;
        int sufIndex = netLocationUrl.indexOf("/nloc/");


        if (httpsIndex < 0 || sufIndex < 0) {
            httpsIndex = 0;
            sufIndex = 0;
        }


        urlEt.setText(netLocationUrl.substring(httpsIndex, sufIndex));
        sha1Et.setText(AppInfo.getSHA1(context));
        akEt.setText(AppInfo.getSystemAk(context));
        pkgNameEt.setText(AppInfo.getPackageName(context));

        if (TYPE == CodeConst.REQ_CODE_MAP_SET) {
            lngEt.setVisibility(View.VISIBLE);
            latEt.setVisibility(View.VISIBLE);

            lngEt.setText(" 114.401543");
            latEt.setText("30.507086");
        }
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

        if (TYPE == CodeConst.REQ_CODE_MAP_SET) {
            if (ToastUtil.showCannotEmpty(context, lng, "经度")) {
                return;
            }
            if (ToastUtil.showCannotEmpty(context, lat, "纬度")) {
                return;
            }


        }
        String urlPre = url.equals("gis.sf-express.com") ? "https://" : "http://";
        url = urlPre + url + "/nloc/locationapi";


        AppInfo.setSpUrl(url);
        AppInfo.setSha1(sha1);
        AppInfo.setApiKey(apiKey);
        AppInfo.setPackageName(pkgName);

        AppInfo.setLng(lng);
        AppInfo.setLat(lat);

        SPUtils.put(context, KeyConst.SP_SHA1, sha1);
        SPUtils.put(context, KeyConst.SP_URL, url);
        SPUtils.put(context, KeyConst.SP_AK, apiKey);
        SPUtils.put(context, KeyConst.SP_PKG_NAME, pkgName);
        SPUtils.put(context, KeyConst.SP_LNG, lng);
        SPUtils.put(context, KeyConst.SP_LAT, lat);


        //这三个值传回去
        Intent intent = new Intent();
        int RES_CODE = (TYPE == CodeConst.REQ_CODE_MAP_SET ? CodeConst.RES_CODE_MAP : CodeConst.RES_CODE_LOC);
        setResult(RES_CODE, intent);
        finish();

    }


}



