package com.sfmap.api.maps;

import android.content.Context;
import com.sfmap.api.mapcore.util.OffsetUtil;
import com.sfmap.api.mapcore.util.SDKLogHandler;
import com.sfmap.api.maps.model.LatLng;

/**
 * 坐标转换器。
 */
class CoordinateConverter
{
  private Context context;
  private CoordType type = null;
  private LatLng latLng = null;

    /**
     * 构造方法
     * @param context - 上下文。
     */
  public CoordinateConverter(Context context)
  {
    this.context = context;
  }

    /**
     * 设置需要转换的坐标系类型（源坐标类型）。
     * @param type - 源坐标类型，如baidu、GPS等。
     * @return 坐标转换的CoordinateConverter对象。
     */
  public CoordinateConverter from(CoordType type)
  {
    this.type = type;
    return this;
  }

    /**
     *设置需要转换的经纬度坐标（源坐标）。
     * @param latLng  - 源经纬度坐标。
     * @return 坐标转换的CoordinateConverter对象。
     */
  public CoordinateConverter coord(LatLng latLng)
  {
    this.latLng = latLng;
    return this;
  }

    /**
     * 执行坐标转换操作。
     * @return 返回国测局坐标。
     */
  public LatLng convert()
  {
    if (this.type == null) {
      return null;
    }
    if (null == this.latLng) {
      return null;
    }
    LatLng localLatLng = null;
    try
    {
      //switch (aaaab.aaaaa[this.b.ordinal()]) ????
      switch(this.type.ordinal())
      {
      case 1: 
        localLatLng = OffsetUtil.aaaaa(this.latLng);
        break;
      case 2: 
        localLatLng = OffsetUtil.b(this.context, this.latLng);
        break;
      case 3: 
      case 4: 
      case 5: 
      case 6: 
        localLatLng = this.latLng;
        break;
      case 7: 
        localLatLng = OffsetUtil.aaaaa(this.context, this.latLng);
      }
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
      SDKLogHandler.exception(localThrowable, "CoordinateConverter", "convert");
      localLatLng = this.latLng;
    }
    return localLatLng;
  }
  
  public static enum CoordType
  {
	  a,b,c,d,e,f,g
  }
}
