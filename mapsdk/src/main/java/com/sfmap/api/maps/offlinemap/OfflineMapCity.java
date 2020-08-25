package com.sfmap.api.maps.offlinemap;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 下载城市属性的相关类。
 */
public class OfflineMapCity
  extends City
{
    /**
     * 下载城市的数据地址。
     */
  private String url = "";
    /**
     * 返回下载城市数据的大小，单位字节。
     */
  private long size = 0L;
    /**
     * 城市下载状态。
     */
  private int state = OfflineMapStatus.CHECKUPDATES;
  private String version = "";
    /**
     * 下载百分比。
     */
  private int code = 0;
  /**
   * 地图数据md5值
   */
  private String md5="";
    /**
     * 构造一个OfflineMapCity对象。
     */
  public OfflineMapCity() {}

    /**
     * 返回所下载城市的数据地址。
     * @return 所下载城市的数据地址。
     */
  public String getUrl()
  {
    return this.url;
  }

    /**
     * 设置所下载城市的数据地址。
     * @param url - 所下载城市的数据地址。
     */
  public void setUrl(String url)
  {
    this.url = url;
  }

    /**
     *返回下载城市数据的大小，单位字节。
     * @return 下载城市数据的大小，单位字节。
     */
  public long getSize()
  {
    return this.size;
  }

    /**
     * 设置下载城市数据的大小，单位字节。
     * @param size - 下载城市数据的大小，单位字节。
     */
  public void setSize(long size)
  {
    this.size = size;
  }

    /**
     * 返回城市下载状态。
     * @return 城市下载状态。
     */
  public int getState()
  {
    return this.state;
  }

    /**
     * 设置返回城市下载状态。
     * @param state - 城市下载状态。
     */
  public void setState(int state)
  {
    this.state = state;
  }

    /**
     * 返回下载城市的数据版本。
     * @return 下载城市的数据版本。
     */
  public String getVersion()
  {
    return this.version;
  }

    /**
     * 设置下载城市的数据版本。
     * @param version - 下载城市的数据版本。
     */
  public void setVersion(String version)
  {
    this.version = version;
  }

    /**
     * 返回城市下载完成的百分比，100表示下载完成。
     * @return 城市下载完成的百分比，100表示下载完成。
     */
  public int getcompleteCode()
  {
    return this.code;
  }

  /**
   * 获取城市数据的MD5值。
   * @return 城市数据的MD5值
     */
  public String getMd5() {
    return md5;
  }

  /**
   * 设置城市数据的MD5值。
   * @param md5 城市数据的MD5值。
     */
  public void setMd5(String md5) {
    this.md5 = md5;
  }

  /**
     *设置下载完成的百分比，100表示下载完成。
     * @param code - 下载完成的百分比，100表示下载完成。
     */
  public void setCompleteCode(int code)
  {
    this.code = code;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    super.writeToParcel(paramParcel, paramInt);
    paramParcel.writeString(this.url);
    paramParcel.writeLong(this.size);
    paramParcel.writeInt(this.state);
    paramParcel.writeString(this.version);
    paramParcel.writeInt(this.code);
    paramParcel.writeString(md5);
  }
  
  public OfflineMapCity(Parcel paramParcel)
  {
    super(paramParcel);
    this.url = paramParcel.readString();
    this.size = paramParcel.readLong();
    this.state = paramParcel.readInt();
    this.version = paramParcel.readString();
    this.code = paramParcel.readInt();
    this.md5 = paramParcel.readString();
  }
  
  public static final Parcelable.Creator<OfflineMapCity> CREATOR = new OfflineMapCityCreator();
}
