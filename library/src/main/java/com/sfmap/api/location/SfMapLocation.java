package com.sfmap.api.location;

import android.location.Location;
import android.location.LocationManager;
import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings("unused")
public class SfMapLocation extends Location {
    /**
     * 定位错误码：定位成功
     */
    public static final int LOCATION_SUCCESS = 0;
    /**
     *定位错误码：一些重要参数为空,如context,可以通过AMapLocation.getLocationDetail()获取详细信息
     */
    public static final int ERROR_CODE_INVALID_PARAMETER = 1;
    /**
     *定位错误码：定位失败，由于设备仅扫描到单个wifi，不能精准的计算出位置信息。
     */
    public static final int ERROR_CODE_FAILURE_WIFI_INFO = 2;
    /**
     *定位错误码：获取到的请求参数为空，可能获取过程中出现异常,可以通过AMapLocation.getLocationDetail()获取详细信息
     */
    public static final int ERROR_CODE_FAILURE_LOCATION_PARAMETER = 3;
    /**
     *定位错误码：网络连接异常,可以通过AMapLocation.getLocationDetail()获取详细信息
     */
    public static final int ERROR_CODE_FAILURE_CONNECTION = 4;
    /**
     *定位错误码：解析XML出错,可以通过AMapLocation.getLocationDetail()获取详细信息
     */
    public static final int ERROR_CODE_FAILURE_PARSER = 5;
    /**
     *定位错误码：定位结果错误,可以通过AMapLocation.getLocationDetail()获取详细信息
     */
    public static final int ERROR_CODE_FAILURE_LOCATION = 6;
    /**
     *定位错误码：KEY错误,可以通过AMapLocation.getLocationDetail()获取详细信息来跟注册的KEY信息进行对照
     */
    public static final int ERROR_CODE_FAILURE_AUTH = 7;
    /**
     *定位错误码：其他错误,可以通过AMapLocation.getLocationDetail()获取详细信息
     */
    public static final int ERROR_CODE_UNKNOWN = 8;
    /**
     *定位错误码：初始化异常,可以通过AMapLocation.getLocationDetail()获取详细信息
     */
    public static final int ERROR_CODE_FAILURE_INIT = 9;
    /**
     *定位错误码：定位服务启动失败，请检查是否配置service并且manifest中service标签是否配置在application标签内
     */
    public static final int ERROR_CODE_SERVICE_FAIL = 10;
    /**
     *定位错误码：错误的基站信息，请检查是否安装SIM卡
     */
    public static final int ERROR_CODE_FAILURE_CELL = 11;
    /**
     *定位错误码：缺少定位权限,请检查是否配置定位权限,并在安全软件和设置中给应用打开定位权限
     */
    public static final int ERROR_CODE_FAILURE_LOCATION_PERMISSION = 12;
    /**
     *定位错误码：网络定位失败，请检查设备是否插入sim卡、开启移动网络或开启了wifi模块
     */
    public static final int ERROR_CODE_FAILURE_NOWIFIANDAP = 13;
    /**
     *定位错误码：卫星定位失败，可用卫星数不足
     */
    public static final int ERROR_CODE_FAILURE_NOENOUGHSATELLITES = 14;
    /**
     *定位错误码：定位位置可能被模拟
     */
    public static final int ERROR_CODE_FAILURE_SIMULATION_LOCATION = 15;
    /**
     *定位错误码：定位失败，飞行模式下关闭了WIFI开关，请关闭飞行模式或者打开WIFI开关
     */
    public static final int ERROR_CODE_AIRPLANEMODE_WIFIOFF = 18;
    /**
     *定位错误码：定位失败，没有检查到SIM卡，并且关闭了WIFI开关，请打开WIFI开关或者插入SIM卡
     */
    public static final int ERROR_CODE_NOCGI_WIFIOFF = 19;

    private int mErrorCode = LOCATION_SUCCESS;
    private boolean mFromCache;
    private String mAddress;
    private String mCountry;
    private String mRegion;
    private String mCity;
    private String mCounty;
    private String mStreet;
    private String mStreetNumber;
    private int mAdcode;
    private int mSatellites;

    public SfMapLocation() {
        super(LocationManager.NETWORK_PROVIDER);
    }

    public SfMapLocation(String provider) {
        super(provider);
    }

    public SfMapLocation(Location l) {
        super(l);
    }

    public SfMapLocation(int errorCode) {
        super(LocationManager.NETWORK_PROVIDER);
        mErrorCode = errorCode;
    }

    @Override
    public String toString() {
        String superString = super.toString();
        return "ErrorCode[" + mErrorCode + "]," +
                "FormCached[" + mFromCache + "]," +
                superString;
    }

    /**
     * 返回定位错误码
     * @return 错误码
     */
    public int getErrorCode() {
        return mErrorCode;
    }

    /**
     * 是否定位成功
     * @return true 成功， false 失败
     */
    public boolean isSuccessful() {
        return mErrorCode == LOCATION_SUCCESS;
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(mErrorCode);
        out.writeByte((byte) (mFromCache ? 1 : 0));
        out.writeString(mAddress);
        out.writeString(mCountry);
        out.writeString(mRegion);
        out.writeString(mCity);
        out.writeString(mCounty);
        out.writeString(mStreet);
        out.writeString(mStreetNumber);
        out.writeInt(mAdcode);
        out.writeInt(mSatellites);
    }

    public static final Parcelable.Creator<SfMapLocation> CREATOR = new Parcelable.Creator<SfMapLocation>() {

        @Override
        public SfMapLocation createFromParcel(Parcel source) {
            Location location = Location.CREATOR.createFromParcel(source);
            SfMapLocation sfMapLocation = new SfMapLocation(location);
            sfMapLocation.mErrorCode = source.readInt();
            sfMapLocation.mFromCache = source.readByte() == 1;
            sfMapLocation.mAddress = source.readString();
            sfMapLocation.mCountry = source.readString();
            sfMapLocation.mRegion = source.readString();
            sfMapLocation.mCity = source.readString();
            sfMapLocation.mCounty = source.readString();
            sfMapLocation.mStreet = source.readString();
            sfMapLocation.mStreetNumber = source.readString();
            sfMapLocation.mAdcode = source.readInt();
            sfMapLocation.mSatellites = source.readInt();
            return sfMapLocation;
        }

        @Override
        public SfMapLocation[] newArray(int size) {
            return new SfMapLocation[size];
        }
    };

    /**
     * 是否是缓存数据
     * @return 是否是缓存数据
     */
    public boolean isFromCache() {
        return mFromCache;
    }

    public void setFromCache(boolean fromCache) {
        this.mFromCache = fromCache;
    }

    /**
     * 设置地址信息
     */
    public void setAddress(String address) {
        mAddress = address;
    }

    /**
     * 返回地址信息(需要网络通畅，第一次有可能没有地址信息返回）
     * @return 地址
     */
    public String getAddress() {
        return mAddress;
    }

    /**
     * 设置国家名称
     */
    public void setCountry(String country) {
        mCountry = country;
    }

    /**
     * 获取国家名称
     * @return 国家名称
     */
    public String getCountry() {
        return mCountry;
    }

    /**
     * 设置区域
     */
    public void setRegion(String region) {
        mRegion = region;
    }

    /**
     * 获取区域
     * @return 区域
     */
    public String getRegion() {
        return mRegion;
    }

    /**
     * 设置省城市名称
     */
    public void setCity(String city) {
        mCity = city;
    }

    /**
     * 返回省城市名称
     * @return 省城市名称
     */
    public String getCity() {
        return mCity;
    }

    public void setCounty(String county) {
        mCounty = county;
    }

    public String getCounty() {
        return mCounty;
    }

    public void setStreet(String street) {
        mStreet = street;
    }

    /**
     * 返回街道名称
     * @return 街道名称
     */
    public String getStreet() {
        return mStreet;
    }

    /**
     * 设置门牌号
     */
    public void setStreetNumber(String streetNumber) {
        mStreetNumber = streetNumber;
    }

    /**
     * 返回门牌号
     * @return 门牌号
     */
    public String getStreetNumber() {
        return mStreetNumber;
    }

    /**
     * 返回城市编码
     * @return 城市编码
     */
    public int getmAdcode() {
        return mAdcode;
    }

    /**
     * 设置城市编码
     */
    public void setmAdcode(int mAdcode) {
        this.mAdcode = mAdcode;
    }

    /**
     * 获取当前可用卫星数量, 仅在卫星定位时有效,
     * @return 可用卫星数量
     */
    public int getmSatellites() {
        return mSatellites;
    }

    /**
     * 设置可用卫星数量
     */
    public void setmSatellites(int mSatellites) {
        this.mSatellites = mSatellites;
    }
}