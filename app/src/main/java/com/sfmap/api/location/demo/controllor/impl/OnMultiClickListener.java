package com.sfmap.api.location.demo.controllor.impl;

import android.view.View;

/*
 *  @author Dylan
 *
 */
public abstract class OnMultiClickListener implements View.OnClickListener {
    // 间隔要大于1秒 才触发第二次
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    public abstract void onMultiClick(View v);

    @Override
    public void onClick(View v) {
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            // 超过点击间隔后再将lastClickTime重置为当前点击时间
            lastClickTime = curClickTime;
            onMultiClick(v);
        }
    }
}
