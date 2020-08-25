package com.sfmap.api.mapcore.util;

import android.content.Context;
import com.sfmap.api.maps.offlinemap.OfflineMapCity;
import com.sfmap.api.maps.offlinemap.OfflineMapProvince;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import org.json.JSONException;
import org.json.JSONObject;
import com.sfmap.api.maps.offlinemap.OfflineMapStatus;

public class UpdateItem//r
{
  public int state = OfflineMapStatus.CHECKUPDATES;
  private String city = null;
  private String url = null;
  private String adcode = null;
  private String dataFileTempPath = null;
  private String version = "";
  private long localLength = 0L;
  private long size = 0L;
  private String vMapFileName = "";
  private String localPath;
  private int index;
  private boolean isSheng = false;
  private int completeCode;
  private String code = "";
  private Context context;
  
  public UpdateItem(OfflineMapCity offlineMapCity, Context context)
  {
    this.context = context;
    this.city = offlineMapCity.getCity();
    this.adcode = offlineMapCity.getAdcode();
    this.url = offlineMapCity.getUrl();
    this.size = offlineMapCity.getSize();
    a();//数据缓存路径
    this.version = offlineMapCity.getVersion();
    this.code = offlineMapCity.getCode();
    this.isSheng = false;
    this.state = offlineMapCity.getState();
    this.completeCode = offlineMapCity.getcompleteCode();
  }
  
  public UpdateItem(OfflineMapProvince paramOfflineMapProvince, Context paramContext)
  {
    this.context = paramContext;
    this.city = paramOfflineMapProvince.getProvinceName();
    this.adcode = paramOfflineMapProvince.getProvinceCode();
    this.url = paramOfflineMapProvince.getUrl();
    this.size = paramOfflineMapProvince.getSize();
    a();
    this.version = paramOfflineMapProvince.getVersion();
    this.isSheng = true;
    this.state = paramOfflineMapProvince.getState();
    this.completeCode = paramOfflineMapProvince.getcompleteCode();
  }
  
  protected void a()
  {
    String vmap2path = Util.getOfflineMapDataTempPath(this.context);
    this.dataFileTempPath = (vmap2path + this.adcode + ".zip" + ".tmp");
  }
  
  public void reset()
  {
    this.state = 6;
    setCompleteCode(0);
    setLocalLength(0L);
  }
  
  public UpdateItem() {}
  
  public String getCity()
  {
    return this.city;
  }
  
  public void setCity(String city)
  {
    this.city = city;
  }
  
  public String getVersion()
  {
    return this.version;
  }
  
  public void setVersion(String version)
  {
    this.version = version;
  }
  
  public String getDataFileTempPath()
  {
    return this.dataFileTempPath;
  }
  
  public void setDataFileTempPath(String path)
  {
    this.dataFileTempPath = path;
  }
  
  public String getAdcode()
  {
    return this.adcode;
  }
  
  public void setAdcode(String adcode)
  {
    this.adcode = adcode;
  }
  
  public String getUrl()
  {
    return this.url;
  }
  
  public void setUrl(String url)
  {
    this.url = url;
  }
  
  public long getSize()
  {
    return this.size;
  }
  
  public void setLocalLength(long localLength)
  {
    this.localLength = localLength;
  }
  
  public void setIndex(int index)
  {
    this.index = index;
  }
  
  public int getIndex()
  {
    return this.index;
  }
  
  public long getLocalSize()
  {
    return this.localLength;
  }
  
  public void setLocalSize(long localLength)
  {
    this.localLength = localLength;
  }
  
  public long getRemoteLength()
  {
    return this.size;
  }
  
  public void setSize(long size)
  {
    this.size = size;
  }
  
  public String getVMapFileName()
  {
    return this.vMapFileName;
  }
  
  public void setVMapFileName(String vMapFileName)
  {
    this.vMapFileName = vMapFileName;
  }
  
  public String getLocalPath()
  {
    return this.localPath;
  }
  
  public void setLocalPath(String paramString)
  {
    this.localPath = paramString;
  }
  
  public boolean isSheng()
  {
    return this.isSheng;
  }
  
  public void setIsSheng(boolean isSheng)
  {
    this.isSheng = isSheng;
  }
  
  public void setCompleteCode(int completeCode)
  {
    this.completeCode = completeCode;
  }
  
  public int getCompleteCode()
  {
    return this.completeCode;
  }
  
  public void setCode(String code)
  {
    this.code = code;
  }
  
  public String getCode()
  {
    return this.code;
  }
  
  public void readJSONFileToObject(String jsonString)
  {
    try
    {
      if ((jsonString == null) || (jsonString.equals(""))) {
        return;
      }
      JSONObject jsonObject = new JSONObject(jsonString);
      JSONObject jsonObject1 = jsonObject.getJSONObject("file");
      if (jsonObject1 == null) {
        return;
      }
      this.city = jsonObject1.optString("title");
      this.adcode = jsonObject1.optString("code");
      this.url = jsonObject1.optString("url");
      this.dataFileTempPath = jsonObject1.optString("fileName");
      this.localLength = jsonObject1.optLong("lLocalLength");
      this.size = jsonObject1.optLong("lRemoteLength");
      this.state = jsonObject1.optInt("mState");
      
      this.version = jsonObject1.optString("version");
      this.localPath = jsonObject1.optString("localPath");
      this.vMapFileName = jsonObject1.optString("vMapFileNames");
      this.isSheng = jsonObject1.optBoolean("isSheng");
      this.completeCode = jsonObject1.optInt("mCompleteCode");
      this.code = jsonObject1.optString("mCityCode");
    }
    catch (JSONException localJSONException)
    {
      SDKLogHandler.exception(localJSONException, "UpdateItem", "readJSONFileToObject");
      
      localJSONException.printStackTrace();
    }
  }
  
  public void saveJSONObjectToFile()
  {
    JSONObject localJSONObject1 = new JSONObject();
    JSONObject localJSONObject2 = null;
    try
    {
      localJSONObject2 = new JSONObject();
      localJSONObject2.put("title", this.city);
      localJSONObject2.put("code", this.adcode);
      localJSONObject2.put("url", this.url);
      localJSONObject2.put("fileName", this.dataFileTempPath);
      localJSONObject2.put("lLocalLength", this.localLength);
      localJSONObject2.put("lRemoteLength", this.size);
      localJSONObject2.put("mState", this.state);
      
      localJSONObject2.put("version", this.version);
      localJSONObject2.put("localPath", this.localPath);
      if (this.vMapFileName != null) {
        localJSONObject2.put("vMapFileNames", this.vMapFileName);
      }
      localJSONObject2.put("isSheng", this.isSheng);
      localJSONObject2.put("mCompleteCode", this.completeCode);
      localJSONObject2.put("mCityCode", this.code);
      localJSONObject1.put("file", localJSONObject2);
      File localFile = new File(this.dataFileTempPath + ".dt");
      localFile.delete();
      OutputStreamWriter localOutputStreamWriter = null;
      try
      {
        localOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(localFile, true), "utf-8");
        
        localOutputStreamWriter.write(localJSONObject1.toString());
      }
      catch (IOException localIOException2)
      {
        SDKLogHandler.exception(localIOException2, "UpdateItem", "saveJSONObjectToFile");
        
        localIOException2.printStackTrace();
      }
      finally
      {
        try
        {
          if (localOutputStreamWriter != null) {
            localOutputStreamWriter.close();
          }
        }
        catch (IOException localIOException4)
        {
          localIOException4.printStackTrace();
        }
      }
      return;
    }
    catch (JSONException localJSONException)
    {
      SDKLogHandler.exception(localJSONException, "UpdateItem", "saveJSONObjectToFile parseJson");
      
      localJSONException.printStackTrace();
    }
  }
}
