package com.sfmap.api.maps.offlinemap;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 省属性的相关类。
 */
public class Province
  implements Parcelable
{
    /**
     * 省名称
     */
  private String provinceName;
    /**
     *省名称简拼拼音。
     */
  private String jianpin;
    /**
     *省名称拼音。
     */
  private String pinyin;
    /**
     *省的行政编码。
     */
  private String provinceCode;

    /**
     * 构造一个Province对象。
     */
  public Province() {}

    /**
     * 返回省名称。
     * @return 省名称。
     */
  public String getProvinceName()
  {
    return this.provinceName;
  }

    /**
     *返回省名称简拼拼音。
     * @return 省名称简拼拼音。
     */
  
  public String getJianpin()
  {
    return this.jianpin;
  }

    /**
     *返回省名称拼音。
     * @return 省名称拼音。
     */
  public String getPinyin()
  {
    return this.pinyin;
  }

    /**
     *设置省名称。
     * @param paramString - 省名称。
     */
  
  public void setProvinceName(String paramString)
  {
    this.provinceName = paramString;
  }

    /**
     *设置省名称简拼拼音。
     * @param jianpin - 省名称简拼拼音。
     */
  
  public void setJianpin(String jianpin)
  {
    this.jianpin = jianpin;
  }

    /**
     *设置省名称拼音。
     * @param pinyin - 省名称拼音。
     */
  
  public void setPinyin(String pinyin)
  {
    this.pinyin = pinyin;
  }

    /**
     *设置省行政编码。
     * @param provinceCode - 省行政编码。
     */
  
  public void setProvinceCode(String provinceCode)
  {
    this.provinceCode = provinceCode;
  }

    /**
     *得到省的行政编码。
     * @return 省的行政编码。
     */
  
  public String getProvinceCode()
  {
    return this.provinceCode;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.provinceName);
    paramParcel.writeString(this.jianpin);
    paramParcel.writeString(this.pinyin);
    paramParcel.writeString(this.provinceCode);
  }
  
  public Province(Parcel paramParcel)
  {
    this.provinceName = paramParcel.readString();
    this.jianpin = paramParcel.readString();
    this.pinyin = paramParcel.readString();
    this.provinceCode = paramParcel.readString();
  }
  
  public static final Parcelable.Creator<Province> CREATOR = new ProvinceCreator();
}
