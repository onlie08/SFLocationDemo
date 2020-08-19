package com.sfmap.api.location.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RequestInfoActivity extends BaseActivity {

    private TextView infoTv;
    private EditText sha1Et,pkgEt,skEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_info);

        initView();

    }

    private void initView() {
        sha1Et = findViewById(R.id.et_sha1);
        pkgEt = findViewById(R.id.et_kg_name);
        skEt = findViewById(R.id.et_sk);
        infoTv = findViewById(R.id.info_tv);
    }

}



