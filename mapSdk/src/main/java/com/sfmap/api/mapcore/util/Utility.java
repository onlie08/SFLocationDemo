package com.sfmap.api.mapcore.util;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import com.sfmap.api.maps.offlinemap.OfflineMapCity;
import com.sfmap.api.maps.offlinemap.OfflineMapProvince;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility//ag
{
  public static void log(String paramString)
  {
    LogManager.writeLog(LogManager.productInfo, paramString, 111);
  }
  
  public static void b(String paramString)
  {
    LogManager.writeLog(LogManager.productInfo, paramString, 115);
  }
  
  @SuppressWarnings("deprecation")
  public static long a()
  {
    File localFile = Environment.getExternalStorageDirectory();
    StatFs localStatFs = new StatFs(localFile.getPath());
    
	long l1 = localStatFs.getBlockSize();
    long l2 = localStatFs.getFreeBlocks();
    return l2 * l1;
  }
  
  public static List<OfflineMapProvince> parseCityList(String jsonString)
    throws JSONException
  {
    ArrayList<OfflineMapProvince> localArrayList = new ArrayList<OfflineMapProvince>();
    if ((jsonString == null) || ("".equals(jsonString))) {
      return localArrayList;
    }
    JSONObject cityObj = new JSONObject(jsonString);
    JSONObject resultObj = cityObj.optJSONObject("result");
    if (resultObj == null) {
      return localArrayList;
    }
    if (resultObj.has("version")) {
      OfflineDownloadManager.version = resultObj.optString("version");
    }
    JSONArray provinces = resultObj.optJSONArray("provinces");
    if (provinces == null) {
      return localArrayList;
    }
    int size = provinces.length();
    for (int i = 0; i < size; i++)
    {
      JSONObject localJSONObject5 = provinces.optJSONObject(i);
      if (localJSONObject5 != null) {
        localArrayList.add(a(localJSONObject5));
      }
    }
    JSONArray others = resultObj.optJSONArray("others");
    JSONObject localJSONObject5 = null;
    if ((others != null) && (others.length() > 0)) {
      localJSONObject5 = others.getJSONObject(0);
    }
    if (localJSONObject5 == null) {
      return localArrayList;
    }
    localArrayList.add(a(localJSONObject5));
    return localArrayList;
  }
  
  public static OfflineMapProvince a(JSONObject provinceJson)
  {
    if (provinceJson == null) {
      return null;
    }
    OfflineMapProvince offlineMapProvince = new OfflineMapProvince();
    offlineMapProvince.setUrl(provinceJson.optString("url"));
    offlineMapProvince.setProvinceName(provinceJson.optString("name"));
    offlineMapProvince.setJianpin(provinceJson.optString("jianpin"));
    offlineMapProvince.setPinyin(provinceJson.optString("pinyin"));
    offlineMapProvince.setProvinceCode(provinceJson.optString("adcode"));
    offlineMapProvince.setVersion(provinceJson.optString("version"));
    offlineMapProvince.setSize(Long.parseLong(provinceJson.optString("size")));
    offlineMapProvince.setCityList(b(provinceJson));
    return offlineMapProvince;
  }
  
  public static ArrayList<OfflineMapCity> b(JSONObject paramJSONObject)
  {
    JSONArray localJSONArray = paramJSONObject.optJSONArray("cities");
    ArrayList<OfflineMapCity> localArrayList = new ArrayList<OfflineMapCity>();
    if (localJSONArray == null) {
      return localArrayList;
    }
    if (localJSONArray.length() == 0) {
      localArrayList.add(c(paramJSONObject));
    }
    for (int i = 0; i < localJSONArray.length(); i++)
    {
      JSONObject localJSONObject = localJSONArray.optJSONObject(i);
      if (localJSONObject != null) {
        localArrayList.add(c(localJSONObject));
      }
    }
    return localArrayList;
  }
  
  public static OfflineMapCity c(JSONObject cityJSON)
  {
    OfflineMapCity offlineMapCity = new OfflineMapCity();
    offlineMapCity.setAdcode(cityJSON.optString("adcode"));
    offlineMapCity.setUrl(cityJSON.optString("url"));
    offlineMapCity.setCity(cityJSON.optString("name"));
    offlineMapCity.setCode(cityJSON.optString("citycode"));
    offlineMapCity.setPinyin(cityJSON.optString("pinyin"));
    offlineMapCity.setJianpin(cityJSON.optString("jianpin"));
    offlineMapCity.setVersion(cityJSON.optString("version"));
    offlineMapCity.setSize(Long.parseLong(cityJSON.optString("size")));
    offlineMapCity.setMd5(cityJSON.optString("md5"));
    return offlineMapCity;
  }
  
  public static long a(File paramFile)
  {
    if (!paramFile.isDirectory()) {
      return paramFile.length();
    }
    long l = 0L;
    File[] arrayOfFile1 = paramFile.listFiles();
    if (arrayOfFile1 == null) {
      return l;
    }
    for (File localFile : arrayOfFile1) {
      if (localFile.isDirectory()) {
        l += a(localFile);
      } else {
        l += localFile.length();
      }
    }
    return l;
  }
  
  public static boolean deleteFiles(File vmapFileDir)
    throws IOException, Exception
  {
    if ((vmapFileDir == null) || (!vmapFileDir.exists())) {
      return false;
    }
    File[] fileArray = vmapFileDir.listFiles();
    if (fileArray != null) {
      for (int i = 0; i < fileArray.length; i++) {
        if (fileArray[i].isFile())
        {
          if (!fileArray[i].delete()) {
            return false;
          }
        }
        else if (!deleteFiles(fileArray[i])) {
          return false;
        }
      }
    }
    return vmapFileDir.delete();
  }
  
  public static String readOfflineAsset(Context context, String fileName)
  {
    try
    {
      InputStream localInputStream = ResourcesUtilDecode.getSelfAssets(context).open(fileName);
      return Util.decodeAssetResData(localInputStream);
    }
    catch (Throwable localThrowable)
    {
      SDKLogHandler.exception(localThrowable, "MapDownloadManager", "readOfflineAsset");
      
      localThrowable.printStackTrace();
    }
    return null;
  }
  
  public static String readOfflineSD(File file)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    FileInputStream inputStream = null;
    BufferedReader br = null;
    try
    {
      inputStream = new FileInputStream(file);
      br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
      String str1 = null;
      while ((str1 = br.readLine()) != null) {
        localStringBuffer.append(str1);
      }
      return localStringBuffer.toString();
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      SDKLogHandler.exception(localFileNotFoundException, "MapDownloadManager", "readOfflineSD filenotfound");
      
      localFileNotFoundException.printStackTrace();
    }
    catch (IOException localIOException3)
    {
      SDKLogHandler.exception(localIOException3, "MapDownloadManager", "readOfflineSD io");
      
      localIOException3.printStackTrace();
    }
    finally
    {
      if (br != null) {
        try
        {
          br.close();
        }
        catch (IOException localIOException8)
        {
          localIOException8.printStackTrace();
        }
      }
      if (br != null) {
        try
        {
          br.close();
        }
        catch (IOException localIOException9)
        {
          localIOException9.printStackTrace();
        }
      }
    }
    return null;
  }
  
  public static void a(String adcode, Context context)
    throws IOException, Exception
  {
    File[] arrayOfFile1 = new File(Util.getOfflineMapDataTempPath(context)).listFiles();
    if (arrayOfFile1 == null) {
      return;
    }
    for (File localFile : arrayOfFile1) {
      if ((localFile.exists()) && (localFile.getName().contains(adcode))) {
        deleteFiles(localFile);
      }
    }
    d(Util.getOfflineMapDataTempPath(context));
  }
  
  public static void d(String paramString)
  {
    File localFile1 = new File(paramString);
    if ((localFile1.exists()) && (localFile1.isDirectory()))
    {
      File[] arrayOfFile1 = localFile1.listFiles();
      if (arrayOfFile1 == null) {
        return;
      }
      for (File localFile2 : arrayOfFile1) {
        if ((localFile2.exists()) && (localFile2.isDirectory()))
        {
          String[] arrayOfString = localFile2.list();
          if (arrayOfString == null) {
            localFile2.delete();
          } else if (arrayOfString.length == 0) {
            localFile2.delete();
          }
        }
      }
    }
  }
}
