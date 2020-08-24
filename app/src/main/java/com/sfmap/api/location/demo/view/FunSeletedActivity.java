package com.sfmap.api.location.demo.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sfmap.api.location.demo.R;
import com.sfmap.api.location.demo.constants.KeyConst;
import com.sfmap.api.location.demo.controllor.BaseFgActivity;
import com.sfmap.api.location.demo.controllor.impl.OnMultiClickListener;

import java.util.Map;

public class FunSeletedActivity extends BaseFgActivity {

    private EditText urlEt, sha1Et, pkgNameEt, akEt;
    private FunSeletedActivity context;
    private LinearLayout fun_item_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        initStatusBar();
        setContentView(R.layout.activity_fun_seleted);
        initTitleBackBt(getString(R.string.appName));
        initView();
    }

    Class classArr[] = {LocationActivity.class, MapActivity.class, LocationActivity.class};

    private void initView() {
        String[] funTitleArr = getResources().getStringArray(R.array.fun_title_arr);

        fun_item_layout = ((LinearLayout) findViewById(R.id.fun_list_layout));
        fun_item_layout.removeAllViews();
        for (int i = 0; i < funTitleArr.length; i++) {
            final String title = funTitleArr[i];
            View inflate = LayoutInflater.from(this).inflate(R.layout.layout_fun_item, null);
            TextView tv = inflate.findViewById(R.id.fun_item_tv);
            tv.setText(title);

            final Class aClass = classArr[i];
            tv.setOnClickListener(new OnMultiClickListener() {
                @Override
                public void onMultiClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(context,aClass);
                        intent.putExtra(KeyConst.title, title);
                        context.startActivity(intent);
                }
            });
            fun_item_layout.addView(inflate);
        }

    }
}



