package com.sfmap.api.mapcore.util;

import android.content.Context;

import com.sfmap.api.maps.offlinemap.OfflineMapStatus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OfflineMapDataVerify//k
  extends Thread
{
  private Context context;
  private OfflineDBOperation offlineDBOperation;
  
  public OfflineMapDataVerify(Context context)
  {
    this.context = context;
    this.offlineDBOperation = OfflineDBOperation.getInstance(context);
  }
  
  public void run()
  {
    a();
  }
  
  private void a()
  {
    ArrayList<UpdateItem> localArrayList1 = new ArrayList<UpdateItem>();
    ArrayList<UpdateItem> cityList = this.offlineDBOperation.getOfflineCityListFromDB();
    if (cityList.size() < 1) {
      cityList = paseOfflineCityList(this.context);
    }
    for (UpdateItem cityItem : cityList) {
      if ((cityItem != null) && (cityItem.getCity() != null) &&
        (cityItem.getAdcode().length() >= 1))
      {
        if (Thread.interrupted()) {
          break;
        }
        if (cityItem.state == OfflineMapStatus.SUCCESS) {
          if (!a(cityItem.getAdcode()))
          {
            cityItem.reset();
            try
            {
              Utility.a(
                      cityItem.getAdcode(), this.context);
            }
            catch (Exception localException)
            {
              Utility.b("verify: " + cityItem.getCity() + "delete failed");
            }
            Utility.b("verify: " + cityItem.getCity() + "sdCard don't have");
            
            localArrayList1.add(cityItem);
          }
        }
      }
    }
    OfflineDownloadManager.getInstance(this.context).a(localArrayList1);
  }

  private ArrayList<UpdateItem> paseOfflineCityList(Context context)
  {
    ArrayList<UpdateItem> itemList = new ArrayList<UpdateItem>();
    String mapDataPath = Util.getOfflineMapDataTempPath(context);
    File mapdataDir = new File(mapDataPath);
    if (!mapdataDir.exists()) {
      return itemList;
    }
    File[] listFiles = mapdataDir.listFiles();
    if (listFiles == null) {
      return itemList;
    }
    //遍历地图数据文件夹，获取已下载城市数据列表
    for (File file : listFiles) {
      if (file.getName().endsWith(".zip.tmp.dt"))
      {
        UpdateItem item = jsonToUpdateItem(file);
        if ((item != null) && (item.getCity() != null)) {
          itemList.add(item);
        }
      }
    }
    return itemList;
  }

  /**
   * 解析File为城市信息。
   * @param file
   * @return 城市信息。
   */
  private UpdateItem jsonToUpdateItem(File file)
  {
    String jsonstr = Util.readFile(file);
    UpdateItem item = new UpdateItem();
    
    item.readJSONFileToObject(jsonstr);
    return item;
  }
  
  private boolean a(String adcode)
  {
    List<String> localList = this.offlineDBOperation.a(adcode);
    StringBuilder sb = new StringBuilder();
    sb.append(Util.getMapRoot(this.context));
    sb.append("data/vmap/");
    int i = sb.length();
    for (String str : localList)
    {
      sb.replace(i, sb.length(), str);
      File localFile = new File(sb.toString());
      if (!localFile.exists()) {
        return false;
      }
    }
    sb = null;
    return true;
  }
  
  public void destroy()
  {
    this.context = null;
    this.offlineDBOperation = null;
  }
}
