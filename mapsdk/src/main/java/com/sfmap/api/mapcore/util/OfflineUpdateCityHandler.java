package com.sfmap.api.mapcore.util;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import com.sfmap.api.mapcore.ConfigableConstDecode;
import com.sfmap.api.maps.MapException;
import com.sfmap.api.maps.MapsInitializer;
import com.sfmap.api.maps.offlinemap.OfflineMapProvince;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class OfflineUpdateCityHandler//o
        extends ProtocalHandler<String, List<OfflineMapProvince>>
{
  private Context context;

  public OfflineUpdateCityHandler(Context paramContext, String paramString)
  {
    super(paramContext, paramString);
    setConnTimeout(5000);
    setReadTimeout(50000);
  }

  public void setContext(Context context)
  {
    this.context = context;
  }
  @Override
  protected List<OfflineMapProvince> parseJson(String paramString)
          throws MapException
  {
    String str1 = paramString;
    List<OfflineMapProvince> mapProvinceList = new ArrayList<OfflineMapProvince>();
    try
    {
      //str1 = new String(paramString, "utf-8");
      Util.paseAuthFailurJson(str1);
      if ((str1 == null) || ("".equals(str1))) {
        return mapProvinceList;
      }
      JSONObject jsonObject = new JSONObject(str1);
      String status = jsonObject.optString("status");
      if ((status == null) || (status.equals("")) || (!status.equals("0"))) {
        return (List<OfflineMapProvince>)mapProvinceList;
      }
      mapProvinceList = parseNetJson(str1);
    }
    catch (Throwable localThrowable)
    {
      SDKLogHandler.exception(localThrowable, "OfflineUpdateCityHandler", "loadData jsonInit");

      localThrowable.printStackTrace();
    }
    return (List<OfflineMapProvince>)mapProvinceList;
  }

  private void saveOfflinemapJson(String json)
  {
    if (Util.getOfflineMapDataTempPath(this.context).equals("")) {
      return;
    }
    File file = new File(Util.getOfflineMapDataTempPath(this.context) + "offlineMap.data");
    if (!file.exists()) {
      try
      {
        file.createNewFile();
      }
      catch (IOException e)
      {
        SDKLogHandler.exception(e, "OfflineUpdateCityHandler", "writeSD dirCreate");

        e.printStackTrace();
      }
    }
    if (getBlockSize() > 1048576L)
    {
      FileOutputStream outputStream = null;
      try
      {
        outputStream = new FileOutputStream(file);
        outputStream.write(json.getBytes("utf-8"));
      }
      catch (FileNotFoundException e)
      {
        SDKLogHandler.exception(e, "OfflineUpdateCityHandler", "writeSD filenotfound");

        e.printStackTrace();
      }
      catch (IOException e1)
      {
        SDKLogHandler.exception(e1, "OfflineUpdateCityHandler", "writeSD io");

        e1.printStackTrace();
      }
      finally
      {
        if (outputStream != null) {
          try
          {
            outputStream.close();
          }
          catch (IOException e)
          {
            e.printStackTrace();
          }
        }
      }
    }
  }

  @SuppressWarnings("deprecation")
  public long getBlockSize()
  {
    if (Environment.getExternalStorageState().equals("mounted"))
    {
      File localFile = Environment.getExternalStorageDirectory();
      StatFs localStatFs = new StatFs(localFile.getPath());
      long l1 = localStatFs.getBlockSize();
      long l2 = localStatFs.getAvailableBlocks();
      long l3 = l2 * l1;
      return l3;
    }
    return 0L;
  }

  public String getUrl()
  {
    String url = null;
    return AppInfo.getSfMapURL(context)+"/config/resource";
  }

  protected List<OfflineMapProvince> parseNetJson(String json)
          throws MapException
  {
    List<OfflineMapProvince> mapProvinceList = null;
    try
    {
      //将城市列表缓存到本地
      if (this.context != null) {
        saveOfflinemapJson(json);
      }
    }
    catch (Throwable throwable)
    {
      SDKLogHandler.exception(throwable, "OfflineUpdateCityHandler", "loadData jsonInit");

      throwable.printStackTrace();
    }
    try
    {
      //解析城市列表
      mapProvinceList = Utility.parseCityList(json);
    }
    catch (JSONException e)
    {
      SDKLogHandler.exception(e, "OfflineUpdateCityHandler", "loadData parseJson");

      e.printStackTrace();
    }
    return mapProvinceList;
  }

  public Map<String, String> getRequestParam()
  {
    Map<String,String> hashMap = new HashMap<String,String>();
    hashMap.put("ak", AppInfo.getKey(this.context));
    hashMap.put("mapver",this.task);
    hashMap.put("opertype", "offlinemap_with_province_vone");
    hashMap.put("plattype", "android");
    hashMap.put("product", ConfigableConstDecode.product);
    hashMap.put("version", MapsInitializer.getVersion());
    hashMap.put("ext", "standard");
    hashMap.put("output", "json");
    String str3 = ClientInfo.getTS();
    hashMap.put("ts", str3);
    hashMap.put("scode", ClientInfo.Scode(this.context, str3, ""));
    return hashMap;
  }
}
