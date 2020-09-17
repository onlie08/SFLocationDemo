package com.sfmap.api.mapcore.util;

import android.content.Context;
import android.text.TextUtils;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.mapcore.CoordUtil;
import com.sfmap.mapcore.DPoint;
import java.io.File;
import java.math.BigDecimal;

public class OffsetUtil//a
{
  private static boolean b = false;
  
  public static LatLng aaaaa(Context context, LatLng paramLatLng)
  {
    if (null == context) {
      return null;
    }
    String str = SDKCoordinatorDownload.a(context, "libwgs2gcj.so");
    if (!TextUtils.isEmpty(str))
    {
      File localObject = new File(str);
      if (((File)localObject).exists()) {
        if (!b) {
          try
          {
            System.load(str);
            b = true;
          }
          catch (Throwable localThrowable)
          {
            localThrowable.printStackTrace();
          }
        } else {
          LogManager.writeLog("OffsetUtil", "group has loaded", 111);
        }
      }
    }
    Object localObject = aaaaa(new DPoint(paramLatLng.longitude, paramLatLng.latitude), b);
    return new LatLng(((DPoint)localObject).y, ((DPoint)localObject).x, false);
  }
  
  private static DPoint aaaaa(DPoint paramDPoint, boolean paramBoolean)
  {
    try
    {
      double[] arrayOfDouble = new double[2];
      Object localObject;
      if (paramBoolean)
      {
        localObject = new double[] { paramDPoint.x, paramDPoint.y };
        try
        {
          LogManager.writeLog("OffsetUtil", "use group offset", 111);
          
          int i = CoordUtil.convertToGcj((double[])localObject, arrayOfDouble);
          if (i != 0) {
            arrayOfDouble = dx.a(paramDPoint.x, paramDPoint.y);
          }
        }
        catch (Throwable localThrowable2)
        {
          LogManager.writeLog("OffsetUtil", "use group offset error, use default offset", 111);
          
          localThrowable2.printStackTrace();
          arrayOfDouble = dx.a(paramDPoint.x, paramDPoint.y);
        }
      }
      else
      {
        LogManager.writeLog("OffsetUtil", "use default offset", 111);
        arrayOfDouble = dx.a(paramDPoint.x, paramDPoint.y);
      }
      return new DPoint(arrayOfDouble[0], arrayOfDouble[1]);
    }
    catch (Throwable localThrowable1) {}
    return paramDPoint;
  }
  
  static double PI = Math.PI;
  
  public static LatLng b(Context paramContext, LatLng paramLatLng)
  {
    try
    {
      DPoint localDPoint = c(paramLatLng.longitude, paramLatLng.latitude);
      return aaaaa(paramContext, new LatLng(localDPoint.y, localDPoint.x, false));
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
    return paramLatLng;
  }
  
  public static double aaaaa(double paramDouble1, double paramDouble2)
  {
    return Math.cos(paramDouble2 / 100000.0D) * (paramDouble1 / 18000.0D) + Math.sin(paramDouble1 / 100000.0D) * (paramDouble2 / 9000.0D);
  }
  
  public static double b(double paramDouble1, double paramDouble2)
  {
    return Math.sin(paramDouble2 / 100000.0D) * (paramDouble1 / 18000.0D) + Math.cos(paramDouble1 / 100000.0D) * (paramDouble2 / 9000.0D);
  }
  
  private static DPoint c(double paramDouble1, double paramDouble2)
  {
    double d1 = (paramDouble1 * 100000.0D) % 36000000L;
    double d2 = (paramDouble2 * 100000.0D) % 36000000L;
    int i = (int)(-aaaaa(d1, d2) + d1);
    int j = (int)(-b(d1, d2) + d2);
    i = (int)(-aaaaa(i, j) + d1 + (d1 > 0.0D ? 1 : -1));
    j = (int)(-b(i, j) + d2 + (d2 > 0.0D ? 1 : -1));
    paramDouble1 = i / 100000.0D;
    paramDouble2 = j / 100000.0D;
    DPoint localDPoint = new DPoint(paramDouble1, paramDouble2);
    return localDPoint;
  }
  
  public static LatLng aaaaa(LatLng paramLatLng)
  {
    try
    {
      if (paramLatLng != null)
      {
        DPoint localDPoint = aaaaa(new DPoint(paramLatLng.longitude, paramLatLng.latitude), 2);
        return new LatLng(localDPoint.y, localDPoint.x, false);
      }
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
    return paramLatLng;
  }
  
  private static double aaaaa(double paramDouble)
  {
    return Math.sin(paramDouble * 3000.0D * (PI / 180.0D)) * 2.0E-5D;
  }
  
  private static double b(double paramDouble)
  {
    return Math.cos(paramDouble * 3000.0D * (PI / 180.0D)) * 3.0E-6D;
  }
  
  private static DPoint d(double paramDouble1, double paramDouble2)
  {
    DPoint localDPoint = new DPoint();
    
    double d1 = Math.cos(b(paramDouble1) + Math.atan2(paramDouble2, paramDouble1)) * (aaaaa(paramDouble2) + Math.sqrt(paramDouble1 * paramDouble1 + paramDouble2 * paramDouble2)) + 0.0065D;
    
    double d2 = Math.sin(b(paramDouble1) + Math.atan2(paramDouble2, paramDouble1)) * (aaaaa(paramDouble2) + Math.sqrt(paramDouble1 * paramDouble1 + paramDouble2 * paramDouble2)) + 0.006D;
    localDPoint.x = aaaaa(d1, 8);
    localDPoint.y = aaaaa(d2, 8);
    return localDPoint;
  }
  
  private static double aaaaa(double paramDouble, int paramInt)
  {
    BigDecimal localBigDecimal = new BigDecimal(paramDouble).setScale(paramInt, 4);
    
    return localBigDecimal.doubleValue();
  }
  
  private static DPoint aaaaa(DPoint paramDPoint, int paramInt)
  {
    double d1 = 0.006401062D;
    double d2 = 0.0060424805D;
    DPoint localDPoint = null;
    for (int i = 0; i < paramInt; i++)
    {
      localDPoint = aaaaa(paramDPoint.x, paramDPoint.y, d1, d2);
      d1 = paramDPoint.x - localDPoint.x;
      d2 = paramDPoint.y - localDPoint.y;
    }
    return localDPoint;
  }
  
  private static DPoint aaaaa(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    DPoint localDPoint1 = new DPoint();
    double d1 = paramDouble1 - paramDouble3;
    double d2 = paramDouble2 - paramDouble4;
    DPoint localDPoint2 = d(d1, d2);
    localDPoint1.x = aaaaa(paramDouble1 + d1 - localDPoint2.x, 8);
    localDPoint1.y = aaaaa(paramDouble2 + d2 - localDPoint2.y, 8);
    return localDPoint1;
  }
}
