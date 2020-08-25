package com.sfmap.api.maps.offlinemap;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

/**
 * 下载省属性的相关类。
 */
public class OfflineMapProvince
  extends Province
{
    /**
     * 下载省的数据地址。
     */
  private String url;
    /**
     * 省份下载状态。
     */
  private int state = 6;
    /**
     * 返回下载省数据的大小，单位字节。
     */
  private long size;
  private String version;
    /**
     * 下载百分比。
     */
  private int code = 0;
  private ArrayList<OfflineMapCity> cityList;
  
  public OfflineMapProvince() {}

  /**
   * 返回所下载省的数据地址。
   * @return 下载省的数据地址。
   */
  public String getUrl()
  {
    return this.url;
  }

    /**
     *设置省份下载数据地图。
     * @param url - url地址。
     */
  public void setUrl(String url)
  {
    this.url = url;
  }

    /**
     * 返回省下载状态。
     * @return 省下载状态。
     */
  
  public int getState()
  {
    return this.state;
  }

  /**
   * 设置省份下载状态。
   * @param state - 下载状态。
   */
  public void setState(int state)
  {
    this.state = state;
  }

    /**
     *返回下载省数据的大小，单位字节。
     * @return 下载省数据的大小，单位字节。
     */
  
  public long getSize()
  {
    return this.size;
  }

    /**
     *设置省份大小。
     * @param size - 省份大小。
     */
  
  public void setSize(long size)
  {
    this.size = size;
  }

    /**
     *返回下载省的数据版本。
     * @return 下载省的数据版本。
     */
  
  public String getVersion()
  {
    return this.version;
  }

    /**
     *设置下载省的数据版本。
     * @param version - 版本号。
     */
  
  public void setVersion(String version)
  {
    this.version = version;
  }

    /**
     *返回省下载完成的百分比，100表示下载完成。
     * @return 省下载完成的百分比，100表示下载完成。
     */
  
  public int getcompleteCode()
  {
    return this.code;
  }

    /**
     *设置省下载完成的百分比，100表示下载完成。
     * @param code - 省下载完成的百分比，100表示下载完成。
     */
  
  public void setCompleteCode(int code)
  {
    this.code = code;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void writeToParcel(Parcel dest, int arg1)
  {
    super.writeToParcel(dest, arg1);
    dest.writeString(this.url);
    dest.writeInt(this.state);
    dest.writeLong(this.size);
    dest.writeString(this.version);
    dest.writeInt(this.code);
    dest.writeTypedList(this.cityList);
  }

    /**
     * 得到当前省下所有的城市。
     * @return 当前省下所有的城市。
     */
  public ArrayList<OfflineMapCity> getCityList()
  {
    if (this.cityList == null) {
      return new ArrayList();
    }
    return this.cityList;
  }

    /**
     *设置省下面的城市。
     * @param cityList - 省下面的城市。
     */
  public void setCityList(ArrayList<OfflineMapCity> cityList)
  {
    this.cityList = cityList;
  }
  
  public OfflineMapProvince(Parcel dest)
  {
    super(dest);
    this.url = dest.readString();
    this.state = dest.readInt();
    this.size = dest.readLong();
    this.version = dest.readString();
    this.code = dest.readInt();
    this.cityList = dest.createTypedArrayList(OfflineMapCity.CREATOR);
  }
  
  public static final Parcelable.Creator<OfflineMapProvince> CREATOR = new OfflineMapProvinceCreator();
}
