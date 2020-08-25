package com.sfmap.api.location;

public interface SfMapLocationListener  {
    /**
     * 定位回调监听，当定位完成后调用此方法
     * @param location - 定位结果信息
     */
    void onLocationChanged(SfMapLocation location);
}
