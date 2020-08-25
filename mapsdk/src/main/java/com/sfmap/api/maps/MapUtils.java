package com.sfmap.api.maps;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.sfmap.api.mapcore.ConfigableConstDecode;
import com.sfmap.api.mapcore.util.IMMapCoreException;
import com.sfmap.api.mapcore.util.AuthManager;
import com.sfmap.api.mapcore.util.SDKInfo;
import com.sfmap.api.mapcore.util.Util;
import com.sfmap.api.maps.model.LatLng;

import java.util.ArrayList;

/**
 * 定义了一个实现Map地图其他功能的类。
 */
public class MapUtils
{
  private static final Double COEF = 0.00000896861111;//(32.287/3600000)的结果;
//    /**
//     * 速度优先 : 0
//     */
//  public static final int DRIVING_DEFAULT = 0;
//    /**
//     * 费用优先 : 1
//     */
//  public static final int DRIVING_SAVE_MONEY = 1;
//    /**
//     * 距离优先 : 2
//     */
//  public static final int DRIVING_SHORT_DISTANCE = 2;
//    /**
//     * 不走高速 : 3
//     */
//  public static final int DRIVING_NO_HIGHWAY = 3;
//    /**
//     * 避免拥堵 : 4
//     */
//  public static final int DRIVING_AVOID_CONGESTION = 4;
//    /**
//     * 不走高速且避免收费 : 5
//     */
//  public static final int DRIVING_NO_HIGHWAY_AVOID_SHORT_MONEY = 5;
//    /**
//     * 不走高速且躲避拥堵 : 6
//     */
//  public static final int DRIVING_NO_HIGHWAY_AVOID_CONGESTION = 6;
//    /**
//     * 躲避收费和拥堵 : 7
//     */
//  public static final int DRIVING_SAVE_MONEY_AVOID_CONGESTION = 7;
//    /**
//     * 不走高速躲避收费和拥堵 : 8
//     */
//  public static final int DRIVING_NO_HIGHWAY_SAVE_MONEY_AVOID_CONGESTION = 8;
//    /**
//     * 最快捷 : 0
//     */
//  public static final int BUS_TIME_FIRST = 0;
//    /**
//     * 费用优先 : 1
//     */
//  public static final int BUS_MONEY_LITTLE = 1;
//    /**
//     * 最少换乘 : 2
//     */
//  public static final int BUS_TRANSFER_LITTLE = 2;
//    /**
//     * 最少步行 : 3
//     */
//  public static final int BUS_WALK_LITTLE = 3;
//    /**
//     * 最舒适 : 4
//     */
//  public static final int BUS_COMFORT = 4;
//    /**
//     * 不乘地铁 : 5
//     */
//  public static final int BUS_NO_SUBWAY = 5;

    /**
     * 根据用户的起点和终点经纬度计算两点间距离，此距离为相对较短的距离，单位米。
     * @param startLatlng  - 起点的坐标。
     * @param endLatlng - 终点的坐标。
     * @return 两点间相对较短的距离，单位米。
     */
  public static float calculateLineDistance(LatLng startLatlng, LatLng endLatlng)
  {
    double d1 = 0.01745329251994329D;
    double d2 = startLatlng.longitude;
    double d3 = startLatlng.latitude;
    double d4 = endLatlng.longitude;
    double d5 = endLatlng.latitude;
    d2 *= 0.01745329251994329D;
    d3 *= 0.01745329251994329D;
    d4 *= 0.01745329251994329D;
    d5 *= 0.01745329251994329D;
    double d6 = Math.sin(d2);
    double d7 = Math.sin(d3);
    double d8 = Math.cos(d2);
    double d9 = Math.cos(d3);
    double d10 = Math.sin(d4);
    double d11 = Math.sin(d5);
    double d12 = Math.cos(d4);
    double d13 = Math.cos(d5);
    double[] arrayOfDouble1 = new double[3];
    double[] arrayOfDouble2 = new double[3];
    arrayOfDouble1[0] = (d9 * d8);
    arrayOfDouble1[1] = (d9 * d6);
    arrayOfDouble1[2] = d7;
    arrayOfDouble2[0] = (d13 * d12);
    arrayOfDouble2[1] = (d13 * d10);
    arrayOfDouble2[2] = d11;
    double d14 = Math.sqrt((arrayOfDouble1[0] - arrayOfDouble2[0]) * (arrayOfDouble1[0] - arrayOfDouble2[0]) + (arrayOfDouble1[1] - arrayOfDouble2[1]) * (arrayOfDouble1[1] - arrayOfDouble2[1]) + (arrayOfDouble1[2] - arrayOfDouble2[2]) * (arrayOfDouble1[2] - arrayOfDouble2[2]));
    
    return (float)(Math.asin(d14 / 2.0D) * 1.27420015798544E7D);
  }

    /**
     * 计算地图上矩形区域的面积，单位平方米。
     * @param leftTopLatlng - 矩形区域左上角点坐标。
     * @param rightBottomLatlng - 矩形区域右下角点坐标。
     * @return 地图上矩形区域的面积，单位平方米。
     */
  public static float calculateArea(LatLng leftTopLatlng, LatLng rightBottomLatlng)
  {
    double d1 = 6378137.0D;
    
    double d2 = Math.sin(leftTopLatlng.latitude * 3.141592653589793D / 180.0D) - Math.sin(rightBottomLatlng.latitude * 3.141592653589793D / 180.0D);
    double d3 = (rightBottomLatlng.longitude - leftTopLatlng.longitude) / 360.0D;
    if (d3 < 0.0D) {
      d3 += 1.0D;
    }
    return (float)(6.283185307179586D * d1 * d1 * d2 * d3);
  }

  /**
   * 求任意多边形的面积。
   * @param polygonPoints 多边形的顶点集合.
   * @return 多边形的面积，单位：m * m
   */
  public static double calPolygonArea(ArrayList<LatLng> polygonPoints){
    if(null == polygonPoints || polygonPoints.size() < 3)
    {
      return 0;
    }
    int size = polygonPoints.size();
    float polygonArea = 0;   //多边形面积
    double x1 = 0;
    double y1 = 0;
    double x2 = 0;
    double y2 = 0;//存放double坐标
    for(int i = 0; i < size;  i++)   //根据顶点坐标，求多边形的面积
    {
      if(i < size - 1)
      {
        //将整型的点坐标转换为浮点型的经纬坐标，并参与计算面积
        x1 = (double)(polygonPoints.get(i).longitude / COEF);
        y1 = (double)(polygonPoints.get(i).latitude / COEF);
        x2 = (double)(polygonPoints.get(i + 1 ).longitude / COEF);
        y2 = (double)(polygonPoints.get(i + 1 ).latitude / COEF);
        polygonArea += (x1 * y2 - x2 * y1);
      }
      else
      {
        x1 = (double)(polygonPoints.get(i).longitude / COEF);
        y1 = (double)(polygonPoints.get(i).latitude / COEF);
        x2 = (double)(polygonPoints.get(0).longitude / COEF);
        y2 = (double)(polygonPoints.get(0).latitude / COEF);
        polygonArea += (x1 * y2 - x2 * y1);
        break;
      }
    }
    return Math.abs(polygonArea) / 2;
  }
//
//  /**
//   * 以midPoint为起点，startPoint为终点的直线为line1,以midPoint为起点，endPoint为终点的直线为line2；
//   * <p>以midPoint为中心，从line1顺时针旋转到line2的夹角<p/>
//   * @param startPoint 夹角端点。
//   * @param midPoint 夹角顶点。
//   * @param endPoint 夹角端点。
//     * @return 夹角，单位弧度。
//     */
//// public static double calculateAngle(LatLng startPoint ,LatLng midPoint , LatLng endPoint){
////   double angle1 = calculateAngle(midPoint.longitude,midPoint.latitude,startPoint.longitude,startPoint.latitude);
////   double angle2 = calculateAngle(midPoint.longitude,midPoint.latitude,endPoint.longitude,endPoint.latitude);
////   return Math.abs(angle1-angle2);
//// }

  /**
   * 计算两条直接的夹角，射线line1顺时针旋转到射线line2的夹角。
   * @param line1Start 射线line1的起点。
   * @param line1end 射线line1的终点。
   * @param line2Start 射线line2的起点。
   * @param line2End 射线line2的终点。
     * @return 夹角，单位弧度。
     */
  public static double calculateAngle(LatLng line1Start,LatLng line1end,LatLng line2Start,LatLng line2End){
    double angle1 = calculateAngle(line1Start.longitude,line1Start.latitude,line1end.longitude,line1end.latitude);
    double angle2 = calculateAngle(line2Start.longitude,line2Start.latitude,line2End.longitude,line2End.latitude);
    double angle = angle2-angle1;
    angle = Math.toDegrees(angle);
    if(angle<0){
      angle = 360-angle;
    }
    angle = Math.toRadians(angle);
    return Math.abs(angle);
  }
  /**
   * 角度计算，相对与正北的顺时针夹角，单位弧度。
   * @param startLon 起点经度。
   * @param startLat 起点纬度。
   * @param endLon 终点经度。
   * @param endLat 终点纬度。
     * @return 夹角，单位弧度。
     */
  public static double calculateAngle(double startLon,  double startLat,  double endLon,  double endLat)
  {

    double dAngle;
    if(endLon != startLon)
    {
      double s = Math.cos((endLat + startLat) * 0.008726646);
      double dAtan = (endLat-startLat) / ((endLon-startLon) * s);
      dAngle = Math.atan(dAtan);
      if(endLon - startLon < 0)
      {
        dAngle += Math.PI;
      }
      else
      {
        if(dAngle < 0)
        {
          dAngle += 2*Math.PI;
        }
      }
    }
    else
    {
      if(endLat > startLat)
      {
        dAngle = Math.PI/2;
      }
      else
      {
        dAngle = Math.PI/2*3;
      }
    }

    dAngle = Math.PI * 5/2 - dAngle;
    if(dAngle > Math.PI * 2)
    {
      dAngle = dAngle - Math.PI * 2;
    }
    return dAngle;
  }

    /**
     * 使用默认浏览器跳转到地图app的下载页面
     * @param context - 上下文。
     */
  public static void getLatestMapApp(Context context)
  {
    try
    {
      String str = "http://you.com/";
      Intent localIntent = new Intent("android.intent.action.VIEW");
      localIntent.addFlags(276824064);
      
      localIntent.addCategory("android.intent.category.DEFAULT");
      localIntent.setData(Uri.parse(str));
      a locala = new a("glaa", context);
      locala.start();
        context.startActivity(localIntent);
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
  }


  static class a
    extends Thread
  {
    String a = "";
    Context b;
    
    public a(String paramString, Context paramContext)
    {
      this.a = paramString;
      if (paramContext != null) {
        this.b = paramContext.getApplicationContext();
      }
    }
    
    public void run()
    {
      if (this.b != null) {
        try
        {
          SDKInfo localbv = new SDKInfo.createSDKInfo(this.a, MapsInitializer.getVersion(), ConfigableConstDecode.userAgent,"").
                  setPackageName(new String[] { Util.PACKAGE_INFO_API_MAPS }).a();
          AuthManager.getKeyAuth(this.b, localbv);
          interrupt();
        }
        catch (IMMapCoreException localbl)
        {
          localbl.printStackTrace();
        }
      }
    }
  }
}
