package com.sfmap.api.maps.offlinemap;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 城市属性的相关类。
 */
public class City
  implements Parcelable
{
    /**
     * 城市名称。
     */
  private String city;
    /**
     * 城市代码。
     */
  private String code;
    /**
     * 城市名称简拼拼音。
     */
  private String jianpin;
    /**
     * 城市名称拼音。
     */
  private String pinyin;
    /**
     * 行政编码。
     */
  private String adcode;

    /**
     * 构造一个City对象。
     */
  public City() {}

    /**
     * 设置城市名称。
     * @param city - 城市名称。
     */
  public void setCity(String city)
  {
    this.city = city;
  }

    /**
     * 返回城市名称。
     * @return 城市名称。
     */
  public String getCity()
  {
    return this.city;
  }

    /**
     * 设置城市代码。
     * @param code - 城市代码。
     */
  public void setCode(String code)
  {
    this.code = code;
  }

    /**
     * 返回城市代码。
     * @return 城市代码。
     */
  public String getCode()
  {
    return this.code;
  }

  /**
   * 返回城市名称简拼拼音。
   * @return 城市名称简拼拼音。
   */
  public String getJianpin()
  {
    return this.jianpin;
  }

    /**
     * 设置城市名称简拼拼音。
     * @param jianpin -  城市名称简拼拼音。
     */
  public void setJianpin(String jianpin)
  {
    this.jianpin = jianpin;
  }

    /**
     * 返回城市名称拼音。
     * @return 城市名称拼音。
     */
  public String getPinyin()
  {
    return this.pinyin;
  }

    /**
     *设置城市名称拼音。
     * @param pinyin - 城市名称拼音。
     */
  public void setPinyin(String pinyin)
  {
    this.pinyin = pinyin;
  }

    /**
     *返回城市的行政编码。
     * @return 城市的行政编码。
     */
  public String getAdcode()
  {
    return this.adcode;
  }

    /**
     *设置城市的行政编码。
     * @param adcode - 城市的行政编码。
     */
  public void setAdcode(String adcode)
  {
    this.adcode = adcode;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.city);
    paramParcel.writeString(this.code);
    paramParcel.writeString(this.jianpin);
    paramParcel.writeString(this.pinyin);
    
    paramParcel.writeString(this.adcode);
  }
  
  public City(Parcel paramParcel)
  {
    this.city = paramParcel.readString();
    this.code = paramParcel.readString();
    this.jianpin = paramParcel.readString();
    this.pinyin = paramParcel.readString();
    
    this.adcode = paramParcel.readString();
  }
  
  public static final Parcelable.Creator<City> CREATOR = new CityCreator();
}
