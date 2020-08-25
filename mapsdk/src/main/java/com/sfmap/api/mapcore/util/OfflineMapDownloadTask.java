package com.sfmap.api.mapcore.util;

import android.content.Context;
import android.os.Bundle;
import com.sfmap.api.maps.MapController;

import java.io.IOException;

public class OfflineMapDownloadTask//m
  extends ThreadTask
  implements NetFileFetch.a
{
  private NetFileFetch netFileFetch;
  private UnZipFile unZipFile;
  private CityObject cityObject;
  private Context context;
  private Bundle bundle = new Bundle();
  private MapController mMap;
  private boolean isStoped = false;
  
  public OfflineMapDownloadTask(TaskItem taskItem, Context context)
  {
    this.cityObject = ((CityObject)taskItem);
    
    this.context = context;
  }
  
  public OfflineMapDownloadTask(TaskItem taskItem, Context context, MapController mMap)
  {
    this(taskItem, context);
    this.mMap = mMap;
  }
  
  public void myRun()
  {
    if (this.cityObject.u())
    {
      this.cityObject.onError(IDownloadListener.ExceptionType.file_io_exception);
      
      return;
    }
    try
    {
      g();
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }
  public void canDownload(){
    this.isStoped = false;
  }
  public void stopTask()
  {
    this.isStoped = true;
    if (this.netFileFetch != null)
    {
      this.netFileFetch.c();
    }
    else
    {
      cancelTask();
      Utility.b("DownloadTask stopTask filefetch is null !!  Maybe cancal a wating task");
    }
    if (this.unZipFile != null) {
      this.unZipFile.a();
    }
  }
  
  private String fileParents()
  {
    return Util.getOfflineMapDataTempPath(this.context);
  }
  
  private void g()
    throws IOException
  {
    String adcode = this.cityObject.getAdcode();
    String zipFileName = adcode + ".zip";
    String cityinfo = zipFileName + ".tmp";
    
    SiteInfoBean site = new SiteInfoBean(this.cityObject.getUrl(), fileParents(), cityinfo, 1, adcode,cityObject.getMd5(),cityObject.getSize());
    // TODO WCB offlinemap download
    this.netFileFetch = new NetFileFetch(site, this.cityObject.getUrl(), this.context, this.cityObject);
    
    this.netFileFetch.a(this);
    
    this.unZipFile = new UnZipFile(this.cityObject.s(), this.cityObject.t(), this.cityObject);
    if (!this.isStoped) {
      this.netFileFetch.start();
    }
  }
  
  public void clear()
  {
    this.mMap = null;
    if (this.bundle != null)
    {
      this.bundle.clear();
      this.bundle = null;
    }
  }
  
  public void onNetfileFetchFinish()
  {
    Utility.b("onNetfileFetchFinish");
    if (this.unZipFile != null) {
      this.unZipFile.unzipFileTaskItem();
    } else {
      Utility.b("OfflineMapDownloadTask UnZipFile is null!!");
    }
  }
}
