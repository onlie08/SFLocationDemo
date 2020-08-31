package com.sfmap.api.location;

public class SfMapLocationClientOption {

    private SfMapLocationMode mLocationMode = SfMapLocationMode.High_Accuracy;
    private static final int MIN_PERMITTED_INTERVAL = 1000;
    //定位间隔
    private long mIntervalMs = 2000;
    //是否单次定位
    private boolean mIsOnce;
    //是否需要地址（进行逆地址编码）
    private boolean mNeedAddress;
    private boolean mIsLocationCacheEnabled = true;
    private boolean mUseGcj02 = true;
    private boolean mTraceEnable = false;

    /**
     * 定位模式，目前支持三种定位模式
     高精度定位模式：
     在这种定位模式下，将同时使用顺丰网络定位和GPS定位,优先返回精度高的定位

     低功耗定位模式：
     在这种模式下，将只使用顺丰网络定位

     仅设备定位模式：
     在这种模式下，将只使用GPS定位。
     *
     */
    public enum SfMapLocationMode {
        /**
         * 低功耗定位模式
         */
        Battery_Saving,
        /**
         * 仅设备定位模式
         */
        Device_Sensors,
        /**
         * 高精度定位模式
         */
        High_Accuracy
    }


    /**
     * 设置定位模式
     * @param mode 定位模式
     */
    public SfMapLocationClientOption setLocationMode(SfMapLocationMode mode) {
        mLocationMode = mode;
        return this;
    }

    /**
     * 设置定位间隔, 不能小于1000毫秒，少于1000毫秒，按照1000毫秒设置
     * @param ms
     */
    public SfMapLocationClientOption setInterval(long ms) {
        if(ms < MIN_PERMITTED_INTERVAL) {
            mIntervalMs = MIN_PERMITTED_INTERVAL;
        } else  {
            mIntervalMs = ms;
        }

        return this;
    }


    /**
     * 是否单次定位
     * @param isOnce 是否单词定位
     */
    public SfMapLocationClientOption setOnceLocation(boolean isOnce) {
        mIsOnce = isOnce;
        return this;
    }

    /**
     * 获得定位模式
     * @return 定位模式
     */
    public SfMapLocationMode getLocationMode() {
        return mLocationMode;
    }

    /**
     * 返回定位间隔
     * @return 间隔时间，单位毫秒
     */
    public long getInterval() {
        return mIntervalMs;
    }

    /**
     * 是否单次定位
     * @return true 是 ，false 不是
     */
    public boolean isOnceLocation() {
        return mIsOnce;
    }

    /**
     * 网络定位时是否需要进行逆地址编码，获得经纬度所处位置的地址，
     * 注意，本设置只有网络定位结果有效，对GPS定位结果
     * {@link com.sfmap.api.location.SfMapLocationClientOption.SfMapLocationMode#Device_Sensors}
     * 无效
     * @param needAddress 是否在网络定位时进行逆地址编码
     */
    public SfMapLocationClientOption setNeedAddress(boolean needAddress) {
        mNeedAddress = needAddress;
        return this;
    }

    /**
     * 返回用户的网络定位逆地址编码选项
     * @return true or false
     */
    public boolean isNeedAddress() {
        return mNeedAddress;
    }


    /**
     * 是否使用默认的GJC02经纬度坐标系
     * @param useGcj02 true使用GCJ02坐标系，false 使用WGS84坐标系
     */
    public SfMapLocationClientOption setUseGjc02(boolean useGcj02) {
        mUseGcj02 = useGcj02;
        return this;
    }

    /**
     * 是否使用GCJ02坐标系
     * @return
     */
    public boolean isUseGcj02() {
        return mUseGcj02;
    }

    /**
     * 是否保存日志文件到本地
     * @return
     */
    public boolean isTraceEnable() {
        return mTraceEnable;
    }

    public void setTraceEnable(boolean traceEnable) {
        this.mTraceEnable = traceEnable;
    }

    /**
     * 是否使用缓存定位数据，默认true;
     * @param isLocationCacheEnabled
     * @return
     */
    public SfMapLocationClientOption setLocationCacheEnabled(boolean isLocationCacheEnabled) {
        mIsLocationCacheEnabled = isLocationCacheEnabled;
        return this;
    }

    public boolean isLocationCachedEnabled() {
        return mIsLocationCacheEnabled;
    }

}
