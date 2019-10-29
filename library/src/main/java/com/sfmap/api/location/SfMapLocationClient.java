package com.sfmap.api.location;

import android.content.Context;

public class SfMapLocationClient {
    public static final String ACTION_NAME_NETWORK_LOCATION_REQUEST =
            "com.sfmap.api.location.SfMapLocationClient.ACTION_NAME_NETWORK_LOCATION_REQUEST";

    private final SfMapLocationClientImpl mInternalImpl;
    private SfMapLocationClientOption mLocationOption;
    private SfMapLocationListener mSfMapLocationListener;
    private boolean mStarted;

    /**
     * 构造方法
     * @param context - Android上下文
     */
    public SfMapLocationClient(Context context) {
        mInternalImpl = SfMapLocationClientImpl.singleton(context);
    }

    /**
     * 定位参数设置
     */
    public void setLocationOption(SfMapLocationClientOption option) {
        mLocationOption = option;
    }

    /**
     * 开始定位
     */
    public void startLocation() {
        //使用当前客户端配置
        mInternalImpl.addClient(this, mLocationOption);
        mInternalImpl.startLocation();
        mStarted = true;
    }

    /**
     * 停止定位
     */
    public void stopLocation() {
        mInternalImpl.removeClient(this);
        mStarted = false;
    }

    /**
     * 设置定位回调监听
     */
    public void setLocationListener(SfMapLocationListener listener) {
        mSfMapLocationListener = listener;
    }

    /**
     * 本地定位服务是否已经启动，用于用户检查服务是否已经启动
     */
    public boolean isStarted() {
        return mStarted;
    }

    /**
     * 销毁定位,释放定位资源, 当不再需要进行定位时调用此方法 该方法会释放所有定位资源，调用后再进行定位需要重新实例化SfMapLocationClient
     */
    public void destroy() {
        mInternalImpl.destroyClient(this);
    }

    /**
     *定位回调
     */
    public void onLocationChanged(SfMapLocation location) {
        if(mSfMapLocationListener != null) {
            mSfMapLocationListener.onLocationChanged(location);
        }
    }
}
