package com.sfmap.api.mapcore.util;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.sfmap.api.maps.MapException;
import com.sfmap.api.maps.offlinemap.OfflineMapCity;
import com.sfmap.api.maps.offlinemap.OfflineMapProvince;
import com.sfmap.api.maps.offlinemap.OfflineMapStatus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONException;

public class OfflineDownloadManager//h
{
  private Context context;
  CopyOnWriteArrayList<CityObject> cityList = new CopyOnWriteArrayList<CityObject>();
  private static OfflineDownloadManager offlineDownloadManager;
  private DownloadListener listener;
  private TaskManager taskManager;
  public static String dataPath = "";
  public static String version = "";

  private OfflineDBOperation offlineDBOperation;
  private ExecutorService executorService = null;
  private ExecutorService executorService1 = null;
  OfflineMapHandler offlineMapHandler = null;
  public OfflineMapDownloadList downloadList;
  OfflineMapRemoveTask removeTask;
  OfflineMapDataVerify offlineMapDataVerify = null;

  private OfflineDownloadManager(Context context)
  {
    this.context = context;
    init();
  }

  public static synchronized OfflineDownloadManager getInstance(Context context)
  {
    if (offlineDownloadManager == null) {
      offlineDownloadManager = new OfflineDownloadManager(context.getApplicationContext());
    }
    return offlineDownloadManager;
  }



  private void init()
  {
    //提前获取数据路径，提高遍历效率
    dataPath = Util.getOfflineMapDataTempPath(this.context);
    this.offlineDBOperation = OfflineDBOperation.getInstance(this.context.getApplicationContext());
    if(offlineMapHandler==null) {
      Looper localLooper = this.context.getMainLooper();
      this.offlineMapHandler = new OfflineMapHandler(localLooper);
    }
    this.downloadList = new OfflineMapDownloadList(this.context, this.offlineMapHandler);
    //一个下载线程
    this.taskManager = TaskManager.getInstance(1);

    paseCityListJson();//初始化省份列表
    this.offlineMapDataVerify = new OfflineMapDataVerify(this.context);
    this.offlineMapDataVerify.start();
    //遍历所有城市,添加到cityList
    if(cityList.size()>0){
      cityList.clear();
    }
    ArrayList<OfflineMapProvince> provinceList = downloadList.getOfflineMapProvinceList();
    for (OfflineMapProvince province : provinceList) {
      for (OfflineMapCity localOfflineMapCity : province.getCityList()) {
        this.cityList.add(new CityObject(this.context, localOfflineMapCity));
      }
    }
    sestUploadlist();
  }

  private void paseCityListJson()
  {
    if (Util.getOfflineMapDataTempPath(this.context).equals("")) {
      return;
    }
    File localFile = new File(Util.getOfflineMapDataTempPath(this.context) + "offlineMap.data");

    String json_cityinfo = null;
    if (!localFile.exists()) {
      //读取asset下的JSON
      json_cityinfo = Utility.readOfflineAsset(this.context, "offlineMap.data");
    } else {
      //读取SD卡JSON
      json_cityinfo = Utility.readOfflineSD(localFile);
    }
    if (json_cityinfo != null) {
      try
      {
        parseCityList(json_cityinfo);
      }
      catch (JSONException localJSONException)
      {
        SDKLogHandler.exception(localJSONException, "MapDownloadManager", "paseJson io");

        localJSONException.printStackTrace();
      }
    }
  }

  /**
   * 解析全国数据列表
   * @param jsonString
   * @throws JSONException
   */
  private void parseCityList(String jsonString)
          throws JSONException
  {
    List<OfflineMapProvince> localList = Utility.parseCityList(jsonString);
    if ((localList == null) || (localList.size() == 0)) {
      return;
    }
    this.downloadList.updateAllCity(localList);
  }

  private void sestUploadlist()
  {
    Utility.b("sestUploadlist: ");

    ArrayList<UpdateItem> localArrayList = this.offlineDBOperation.getOfflineCityListFromDB();
    for (UpdateItem itemFromDB : localArrayList) {
      if ((itemFromDB != null) && (itemFromDB.getCity() != null) &&
              (itemFromDB.getAdcode().length() >= 1))
      {
        Utility.b("sestUploadlist: " + itemFromDB.getCity() + " > " + itemFromDB.state + "  list size:" + localArrayList.size());
        if ((itemFromDB.state != OfflineMapStatus.SUCCESS) &&
                (itemFromDB.state !=OfflineMapStatus.NEW_VERSION) && (itemFromDB.state >= OfflineMapStatus.LOADING)) {
          itemFromDB.state = OfflineMapStatus.PAUSE;
        }
        CityObject cityObject = getCity(itemFromDB.getCity());
        if (cityObject == null)
        {
          Utility.b("getCityObject is null, do not have this city： " + itemFromDB.getCity());
        }
        else
        {
          cityObject.a(itemFromDB.state);
          cityObject.setCompleteCode(itemFromDB.getCompleteCode());
          if(itemFromDB.state == OfflineMapStatus.PAUSE){
            cityObject.setVersion(itemFromDB.getVersion());
            cityObject.setUrl(itemFromDB.getUrl());
          }
          this.downloadList.a(cityObject);
        }
      }
    }
  }

  public void a(ArrayList<UpdateItem> paramArrayList)
  {
    for (UpdateItem localr : paramArrayList)
    {
      CityObject cityObject = getCity(localr.getCity());
      if (cityObject != null)
      {
        LogManager.writeLog(LogManager.productInfo, "Update from Verify: " + localr.getCity(), 111);
        cityObject.a(localr);

        b(cityObject);
      }
    }
  }

  public void updateOfflineMapByName(final String cityName)
  {
    if (cityName == null)
    {
      if (this.listener != null) {
        this.listener.postCheckUpdateRes(null);
      }
      return;
    }
    if (this.executorService == null) {
      this.executorService = Executors.newSingleThreadExecutor();
    }
    this.executorService.execute(new Runnable() {
      public void run() {
        CityObject cityObject = offlineDownloadManager.getCity(cityName);
        try {
          if (!cityObject.getCityStateImp().equals(cityObject.completeState)) {
            return;
          }
          String adcode = cityObject.getAdcode();
          if (adcode.length() > 0) {
            String version = offlineDBOperation.getDownloadMapVersion(adcode);
            if ((offlineDownloadManager.version.length() > 0) && (!version.equals(offlineDownloadManager.version))) {
              cityObject.i();
              return;
            }
          }
          checkConnection();

          Object offlineInitHandler = new OfflineInitHandler(context, offlineDownloadManager.version);
          OfflineInitBean newOffline = (OfflineInitBean) ((OfflineInitHandler) offlineInitHandler).getData();
          if (listener != null)
          {
            if (newOffline == null) {
              return;
            }
            if (newOffline.hasNewVersion()) {
              OfflineDownloadManager.this.getUpdateCityList(newOffline.getVersion());
              reInitManager();
              cityObject = offlineDownloadManager.getCity(cityName);
              cityObject.i();
            }
          }
        } catch (Exception localException) {
          LogManager.writeLog(LogManager.productInfo, localException.getMessage(), 115);
        } finally {
          if (listener != null){
            listener.postCheckUpdateRes(cityObject);
          }
        }
      }
    });
  }
  private void reInitManager(){
    clearList();
    init();
    if (listener != null){
      listener.reLoadCityList();
    }
  }
  private void clearList(){
    if (this.offlineMapDataVerify != null)
    {
      if (this.offlineMapDataVerify.isAlive()) {
        this.offlineMapDataVerify.interrupt();
      }
      this.offlineMapDataVerify = null;
    }
    this.taskManager.b();
    this.downloadList.g();
    downloadList = null;
  }
  private void checkConnection()
          throws MapException
  {
    if (!Util.checkNet(this.context)) {
      throw new MapException("http连接失败 - ConnectionException");
    }
  }

  protected void getUpdateCityList(String version)
          throws MapException
  {
    OfflineUpdateCityHandler updateCityHandler = new OfflineUpdateCityHandler(this.context,version);

    updateCityHandler.setContext(this.context);

    List localList = (List)updateCityHandler.getData();
    if (localList != null) {
      this.downloadList.updateAllCity(localList);
    }
  }

  public boolean b(String name)
  {
    CityObject localg = getCity(name);
    if (localg == null) {
      return false;
    }
    return true;
  }

  public void c(String paramString)
  {
    final CityObject localg = getCity(paramString);
    if (localg == null)
    {
      if (this.listener != null) {
        this.listener.c(localg);
      }
      return;
    }
    if (this.removeTask == null) {
      this.removeTask = new OfflineMapRemoveTask(this.context);
    }
    if (this.executorService1 == null) {
      this.executorService1 = Executors.newSingleThreadExecutor();
    }
    this.executorService1.execute(new Runnable() {
      public void run() {
        if (localg.getCityStateImp().equals(localg.defaultState)) {
          listener.c(localg);
          return;
        }
        OfflineDownloadManager.this.removeTask.a(localg);
      }
    });
  }

  public void addDownloadTask(CityObject paramg)
  {
    try
    {
      this.taskManager.addTask(paramg, this.context, null);
    }
    catch (IMMapCoreException localbl)
    {
      localbl.printStackTrace();
    }
  }

  public void b(CityObject cityObject)
  {
    this.downloadList.a(cityObject);

    Message message = this.offlineMapHandler.obtainMessage();
    message.obj = cityObject;
    this.offlineMapHandler.sendMessage(message);
  }

  public void stop()
  {
    for (CityObject localg : this.cityList) {
      if ((localg.getCityStateImp().equals(localg.loadingState)) || (localg.getCityStateImp().equals(localg.waitingState))) {
        localg.getCityStateImp().pause();
      }
    }
  }

  public void pause()
  {
    for (CityObject localg : this.cityList) {
      if (localg.getCityStateImp().equals(localg.loadingState))
      {
        localg.pauseDownloadTask();
        break;
      }
    }
  }

  public void destroy()
  {
    if ((this.executorService != null) && (!this.executorService.isShutdown())) {
      this.executorService.shutdownNow();
    }
    if (this.offlineMapDataVerify != null)
    {
      if (this.offlineMapDataVerify.isAlive()) {
        this.offlineMapDataVerify.interrupt();
      }
      this.offlineMapDataVerify = null;
    }
    this.taskManager.b();

    this.downloadList.g();

    removeListener();

    offlineDownloadManager = null;
  }

  private CityObject getCity(String city)
  {
    for (CityObject cityObject : this.cityList) {
      if (city.equals(cityObject.getCity())) {
        return cityObject;
      }
    }
    return null;
  }

  private CityObject getCityByCitycode(String citycode)
  {
    for (CityObject city : this.cityList) {
      if (citycode.equals(city.getCode())) {
        return city;
      }
    }
    return null;
  }

  public void downloadByCityName(String cityName)
  {
    CityObject localg = getCity(cityName);
    if (localg != null) {
      localg.pauseDownloadTask();
    } else {
      Utility.b("getCityObject is null, do not have this city： " + cityName);
    }
  }

  public void e(String paramString)
  {
    CityObject localg = getCityByCitycode(paramString);
    if (localg != null) {
      localg.pauseDownloadTask();
    } else {
      Utility.b("getCityObject is null, do not have this city： " + paramString);
    }
  }

  public void stopDownloadCity(CityObject cityObject)
  {
    this.taskManager.stopTask(cityObject);
  }

  public void removeTask(CityObject cityObject)
  {
    this.taskManager.removeTask(cityObject);
  }

  public void setDownloadListener(DownloadListener listener)
  {
    this.listener = listener;
  }

  public void removeListener()
  {
    this.listener = null;
  }

  public static abstract interface DownloadListener
  {
    public abstract void updateState(CityObject paramg);

    public abstract void postCheckUpdateRes(CityObject paramg);

    public abstract void c(CityObject paramg);

    public abstract void reLoadCityList();
  }

  class OfflineMapHandler
          extends Handler
  {
    public OfflineMapHandler(Looper paramLooper)
    {
      super();
    }

    public void handleMessage(Message paramMessage)
    {
      try
      {
        Bundle localBundle = paramMessage.getData();
        Object localObject = paramMessage.obj;
        if ((localObject instanceof CityObject))
        {
          CityObject localg = (CityObject)localObject;
          Utility.log("OfflineMapHandler handleMessage CitObj  name: " + localg.getCity() + " complete: " + localg.getcompleteCode() + " status: " + localg.getState());

          if (listener != null) {
            listener.updateState(localg);
          }
        }
        else
        {
          Utility.log("Do not callback by CityObject! ");
        }
      }
      catch (Throwable localThrowable)
      {
        localThrowable.printStackTrace();
      }
    }
  }
}
