package com.sfmap.api.location.demo.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sfmap.api.location.demo.R;
import com.sfmap.api.location.demo.controllor.BaseFgActivity;

public class RequestInfoActivity extends BaseFgActivity {

    private TextView infoTv;
    private EditText sha1Et, pkgEt, skEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_request_info);
        initTitleBackBt(getString(R.string.location));
        initView();

    }

    private void initView() {
        sha1Et = findViewById(R.id.et_sha1);
        pkgEt = findViewById(R.id.et_kg_name);
        skEt = findViewById(R.id.et_sk);
        infoTv = findViewById(R.id.info_tv);
    }

    public void onSubmitClick(View view) {

    }
}



