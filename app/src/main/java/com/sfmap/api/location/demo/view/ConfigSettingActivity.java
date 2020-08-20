package com.sfmap.api.location.demo.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sfmap.api.location.client.util.AppInfo;
import com.sfmap.api.location.demo.R;
import com.sfmap.api.location.demo.constants.KeyConst;
import com.sfmap.api.location.demo.controllor.BaseFgActivity;
import com.sfmap.api.location.demo.utils.ToastUtil;

public class ConfigSettingActivity extends BaseFgActivity {

    private TextView infoTv;
    private EditText sha1Et, pkgNameEt, akEt;
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
        sha1Et = findViewById(R.id.et_sha1);
        akEt = findViewById(R.id.et_ak);
        pkgNameEt = findViewById(R.id.et_pkg_name);

        sha1Et.setText(AppInfo.getSHA1(context));
        akEt.setText(AppInfo.getSystemAk(context));
        pkgNameEt.setText(AppInfo.getPackageName(context));
    }

    public void onSubmitClick(View view) {
        String sha1 = sha1Et.getText().toString().trim();
        String apiKey = akEt.getText().toString().trim();
        String pkgName = pkgNameEt.getText().toString().trim();
        if (ToastUtil.showCannotEmpty(context, sha1, "sha1值")) {
            return;
        }
        if (ToastUtil.showCannotEmpty(context, apiKey, "AK值")) {
            return;
        }
        if (ToastUtil.showCannotEmpty(context, pkgName, "包名")) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(KeyConst.sha1, sha1);
        intent.putExtra(KeyConst.apiKey, apiKey);
        intent.putExtra(KeyConst.pkgName, pkgName);
        setResult(RESULT_OK, intent);
        finish();

    }
}



