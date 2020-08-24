package com.sfmap.api.location.demo.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sfmap.api.location.client.util.AppInfo;
import com.sfmap.api.location.demo.R;
import com.sfmap.api.location.demo.constants.CodeConst;
import com.sfmap.api.location.demo.constants.KeyConst;
import com.sfmap.api.location.demo.controllor.BaseFgActivity;
import com.sfmap.api.location.demo.utils.ToastUtil;

public class ConfigSettingActivity extends BaseFgActivity {

    private TextView infoTv;
    private EditText urlEt, sha1Et, pkgNameEt, akEt;
    private ConfigSettingActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        initStatusBar();
        setContentView(R.layout.activity_config_setting);
        initTitleBackBt(getString(R.string.location));
        initView();

    }

    private void initView() {
        urlEt = findViewById(R.id.et_url);
        sha1Et = findViewById(R.id.et_sha1);
        akEt = findViewById(R.id.et_ak);
        pkgNameEt = findViewById(R.id.et_pkg_name);

        String netLocationUrl = AppInfo.getNetLocationUrl(context);
        int httpsIndex= netLocationUrl.indexOf("//")+2;
        int sufIndex = netLocationUrl.indexOf("/nloc/");

        urlEt.setText(netLocationUrl.substring(httpsIndex,sufIndex));
        sha1Et.setText(AppInfo.getSHA1(context));
        akEt.setText(AppInfo.getSystemAk(context));
        pkgNameEt.setText(AppInfo.getPackageName(context));
    }

    public void onSubmitClick(View view) {
        String url = urlEt.getText().toString().trim();
        String sha1 = sha1Et.getText().toString().trim();
        String apiKey = akEt.getText().toString().trim();
        String pkgName = pkgNameEt.getText().toString().trim();

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
        //这三个值保存到本地SP
        Intent intent = new Intent();
        intent.putExtra(KeyConst.api_url, url);

        intent.putExtra(KeyConst.sha1, sha1);
        intent.putExtra(KeyConst.apiKey, apiKey);
        intent.putExtra(KeyConst.pkgName, pkgName);
        setResult(CodeConst.RESULT_CODE_CONFIG_SET, intent);
        finish();

    }
}



