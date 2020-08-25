package com.sfmap.api.mapcore.util;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

public class OfflineDBOperation//z
{
  private static OfflineDBOperation instance;
  private static DBOperation dbOperation;
  private Context context;
  
  public static OfflineDBOperation getInstance(Context context)
  {
    if (instance == null) {
      instance = new OfflineDBOperation(context);
    }
    return instance;
  }
  
  private OfflineDBOperation(Context context)
  {
    this.context = context;
    dbOperation = getDB(this.context);
  }
  
  private DBOperation getDB(Context context)
  {
    DBOperation dbOperation = null;
    try
    {
      dbOperation = new DBOperation(context, OfflineDBCreator.getInstance());
    }
    catch (Throwable localThrowable)
    {
      SDKLogHandler.exception(localThrowable, "OfflineDB", "getDB");
      localThrowable.printStackTrace();
    }
    return dbOperation;
  }
  
  private boolean isInit()
  {
    if (dbOperation == null) {
      dbOperation = getDB(this.context);
    }
    if (dbOperation == null) {
      return false;
    }
    return true;
  }
  
  public ArrayList<UpdateItem> getOfflineCityListFromDB()
  {
    ArrayList<UpdateItem> citylist = new ArrayList<UpdateItem>();
    if (!isInit()) {
      return citylist;
    }
    String str = "";
    List<DTInfo> localList = dbOperation.searchListData(str, new DTEntity());
    for (DTInfo localx : localList) {
      citylist.add(localx.a());
    }
    return citylist;
  }
  
  public synchronized void a(UpdateItem item)
  {
    if (!isInit()) {
      return;
    }
    DTEntity entity = new DTEntity();
    entity.setLogInfo(new DTInfo(item));
    String str1 = DTEntity.getAdcodeStr(item.getAdcode());
    dbOperation.searchListData(entity, str1);
    
    String str2 = item.getVMapFileName();
    a(item.getAdcode(), str2);
  }
  
  private void a(String paramString1, String paramString2)
  {
    if ((paramString2 != null) && (paramString2.length() > 0))
    {
      String str1 = DTFileEntity.adcodeStr(paramString1);
      List<DTFileInfo> localList = dbOperation.searchListData(str1, new DTFileEntity());
      if (localList.size() > 0) {
        dbOperation.deleteData(str1, new DTFileEntity());
      }
      String[] arrayOfString1 = paramString2.split(";");
      List<SQlEntity<DTFileInfo>> localArrayList = new ArrayList<SQlEntity<DTFileInfo>>();
      for (String str2 : arrayOfString1)
      {
        DTFileEntity localv = new DTFileEntity();
        localv.setLogInfo(new DTFileInfo(paramString1, str2));
        localArrayList.add(localv);
      }
      this.dbOperation.insertListData(localArrayList);
    }
  }
  
  public synchronized List<String> a(String adcode)
  {
    ArrayList<String> localArrayList = new ArrayList<String>();
    if (!isInit()) {
      return localArrayList;
    }
    String adcodeStr = DTFileEntity.adcodeStr(adcode);
    List<DTFileInfo> localList = dbOperation.searchListData(adcodeStr, new DTFileEntity());
    
    localArrayList.addAll(a(localList));
    return localArrayList;
  }
  
  public synchronized List<String> b(String paramString)
  {
    ArrayList<String> localArrayList = new ArrayList<String>();
    if (!isInit()) {
      return localArrayList;
    }
    String str = DTFileEntity.b(paramString);
    List<DTFileInfo> localList = dbOperation.searchListData(str, new DTFileEntity());
    
    localArrayList.addAll(a(localList));
    return localArrayList;
  }
  
  private List<String> a(List<DTFileInfo> paramList)
  {
    ArrayList<String> localArrayList = new ArrayList<String>();
    if (paramList.size() > 0) {
      for (DTFileInfo localw : paramList) {
        localArrayList.add(localw.getFile());
      }
    }
    return localArrayList;
  }
  
  public synchronized void c(String paramString)
  {
    if (!isInit()) {
      return;
    }
    dbOperation.deleteData(DTEntity.getAdcodeStr(paramString), new DTEntity());
    
    dbOperation.deleteData(DTFileEntity.adcodeStr(paramString), new DTFileEntity());
    
    dbOperation.deleteData(DTDownloadEntity.a(paramString), new DTDownloadEntity());
  }
  
  public void a(String paramString, int paramInt, long paramLong1, long paramLong2, long paramLong3)
  {
    if (!isInit()) {
      return;
    }
    long[] arrayOfLong1 = { paramLong2, 0L, 0L, 0L, 0L };
    long[] arrayOfLong2 = { paramLong3, 0L, 0L, 0L, 0L };
    a(paramString, paramInt, paramLong1, arrayOfLong1, arrayOfLong2);
  }
  
  public synchronized void a(String paramString, int paramInt, long paramLong, long[] paramArrayOfLong1, long[] paramArrayOfLong2)
  {
    if (!isInit()) {
      return;
    }
    DTDownloadEntity locals = new DTDownloadEntity();
    
    locals.setLogInfo(new DTDownloadInfo(paramString, paramLong, paramInt, paramArrayOfLong1[0], paramArrayOfLong2[0]));
    
    String str = DTDownloadEntity.a(paramString);
    dbOperation.searchListData(locals, str);
  }
  
  public synchronized long[] a(String paramString, int paramInt)
  {
    long l1 = 0L;
    long l2 = 0L;
    if (!isInit()) {
      return new long[] { l1, l2 };
    }
    DTDownloadEntity locals = new DTDownloadEntity();
    String str = DTDownloadEntity.a(paramString);
    List<DTDownloadInfo> localList = dbOperation.searchListData(str, locals);
    if (localList.size() > 0)
    {
      l1 = ((DTDownloadInfo)localList.get(0)).a(paramInt);
      l2 = ((DTDownloadInfo)localList.get(0)).b(paramInt);
    }
    return new long[] { l1, l2 };
  }
  
  public synchronized int d(String paramString)
  {
    if (!isInit()) {
      return 0;
    }
    DTDownloadEntity locals = new DTDownloadEntity();
    String str = DTDownloadEntity.a(paramString);
    List<DTDownloadInfo> localList = dbOperation.searchListData(str, locals);
    long l = 0L;
    if (localList.size() > 0) {
      l = ((DTDownloadInfo)localList.get(0)).getFileLength();
    }
    return (int)l;
  }

  /*
   * 获取数据库内纪录的下载过的数据版本
   * @param adcode
   * @return
   */
  public synchronized String getDownloadMapVersion(String adcode)
  {
    String version = null;
    if (!isInit()) {
      return version;
    }
    DTEntity localu = new DTEntity();
    String adcodeStr = DTEntity.getAdcodeStr(adcode);
    List<DTInfo> localList = dbOperation.searchListData(adcodeStr, localu);
    if (localList.size() > 0) {
      version = ((DTInfo)localList.get(0)).getVersion();
    }
    return version;
  }
  
  public synchronized boolean f(String paramString)
  {
    if (!isInit()) {
      return false;
    }
    DTDownloadEntity locals = new DTDownloadEntity();
    String str = DTEntity.getAdcodeStr(paramString);
    List<DTDownloadInfo> localList = dbOperation.searchListData(str, locals);
    if (localList.size() > 0) {
      return true;
    }
    return false;
  }
}
