package com.sfmap.api.mapcore.util;

import android.content.Context;
import com.sfmap.api.mapcore.ConfigableConstDecode;
import com.sfmap.api.maps.MapException;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetFileFetch//ad
  implements DownloadManager.DownloadListener
{
  SiteInfoBean siteInfo = null;
  long b = 0L;
  long myFileLength = 0L;
  long fileLength;
  boolean e = true;
  private Context context;
  OfflineDBOperation dbOperation;
  private IDownloadListener listener;
  private String k;
  private DownloadManager downloadManager;
  private FileAccessI access;
  long g = 0L;
  a h;
  
  public NetFileFetch(SiteInfoBean paramae, String paramString, Context context, IDownloadListener listener)
    throws IOException
  {
    this.dbOperation = OfflineDBOperation.getInstance(context
            .getApplicationContext());
    this.siteInfo = paramae;
    
    this.context = context;
    this.k = paramString;
    this.listener = listener;
    
    g();
  }
  
  private void f()
    throws IOException
  {
    OfflineDownloadRequest downloadRequest = new OfflineDownloadRequest(this.k);
    downloadRequest.setConnTimeout(1800000);
    downloadRequest.setReadTimeout(1800000);
    this.downloadManager = new DownloadManager(downloadRequest, this.b, this.myFileLength);
    
    this.access = new FileAccessI(this.siteInfo.getFileRootPath() + File.separator + this.siteInfo.getFileTempName(), this.b);
  }
  
  private void g()
  {
    if (this.dbOperation.f(this.siteInfo.getAdcode()))
    {
      this.e = false;
      l();
    }
    else
    {
      this.b = 0L;
      this.myFileLength = 0L;
    }
  }
  
  public void start()
  {
    try
    {
      Utility.b("start FileFetch " + this.k);
      boolean bool = Util.checkNet(this.context);
      if (bool)
      {
        authOffLineDownLoad();
      }
      else
      {
        Utility.b("start filefetch  network exception");
        if (this.listener != null) {
          this.listener.onError(IDownloadListener.ExceptionType.network_exception);
        }
        return;
      }
      if (AuthManager.authResult != 0 && AuthManager.authResult != -1) //gaoyh 原来判断条件是 ||
      {
        Utility.b("start filefetch  auth exception");
        if (this.listener != null) {
          this.listener.onError(IDownloadListener.ExceptionType.map_exception);
        }
        return;
      }
      if (!tempFileExist()) {
        this.e = true;
      }
      if (this.e)
      {
//        this.fileLength = getDownloadFileLength();
        this.fileLength = siteInfo.getSize();
        if (this.fileLength == -1L) {
          Utility.log("File Length is not known!");
        } else if (this.fileLength == -2L) {
          Utility.log("File is not access!");
        } else {
          this.myFileLength = this.fileLength;
        }
        this.b = 0L;
      }
      if (this.listener != null) {
        this.listener.startDownload();
      }
      f();
      this.downloadManager.makeGetRequest(this);
    }
    catch (MapException localMapException)
    {
      SDKLogHandler.exception(localMapException, "SiteFileFetch", "download");
      if (this.listener != null) {
        this.listener.onError(IDownloadListener.ExceptionType.map_exception);
      }
    }
    catch (IOException localIOException)
    {
      if (this.listener != null) {
        this.listener.onError(IDownloadListener.ExceptionType.file_io_exception);
      }
    }
  }

  /**
   *
   * @return 存在 false,不存在 true;
     */
  private boolean tempFileExist()
  {
    String str1 = this.siteInfo.getFileRootPath() + File.separator + this.siteInfo.getFileTempName();
    File localFile1 = new File(str1);
    if (localFile1.length() < 10L)
    {
      String str2 = str1.substring(0, str1
        .indexOf(".tmp"));
      File localFile2 = new File(str2);
      if (!localFile2.exists()) {
        return false;
      }
    }
    return true;
  }
  
  private void authOffLineDownLoad()
    throws MapException
  {
    if (AuthManager.authResult != 0 || AuthManager.authResult == -1) {
      for (int n = 0; n < 3; n++) {
        try
        {
          boolean bool = AuthManager.getKeyAuth(this.context, Util.getSDKInfo());
          if (bool) {
            break;
          }
        }
        catch (Throwable localThrowable)
        {
          SDKLogHandler.exception(localThrowable, "SiteFileFetch", "authOffLineDownLoad");
          
          localThrowable.printStackTrace();
        }
      }
    }
  }
  
  public long getDownloadFileLength()
    throws IOException
  {
    int n = -1;
    URL localURL = new URL(this.siteInfo.getUrl());
    
    HttpURLConnection localHttpURLConnection = (HttpURLConnection)localURL.openConnection();
    localHttpURLConnection.setRequestProperty("User-Agent", ConfigableConstDecode.userAgent);
    
    int i1 = localHttpURLConnection.getResponseCode();
    if (i1 >= 400)
    {
      a(i1);
      return -2L;
    }
    for (int i2 = 1;; i2++)
    {
      String str = localHttpURLConnection.getHeaderFieldKey(i2);
      if (str == null) {
        break;
      }
      if (str.equalsIgnoreCase("Content-Length"))
      {
        n = Integer.parseInt(localHttpURLConnection
          .getHeaderField(str));
        break;
      }
    }
    return n;
  }
  
  private void j()
  {
    long l1 = System.currentTimeMillis();
    if ((this.siteInfo != null) && (l1 - this.g > 500L))
    {
      k();
      this.g = l1;
      a(this.b);
    }
  }
  
  private void k()
  {
    this.dbOperation.a(this.siteInfo.getAdcode(), this.siteInfo
      .d(), this.fileLength, this.b, this.myFileLength);
  }
  
  private void a(long paramLong)
  {
    if ((this.fileLength > 0L) && 
      (this.listener != null))
    {
      this.listener.onProgress(this.fileLength, paramLong);
      this.g = System.currentTimeMillis();
    }
  }
  
  private boolean l()
  {
    boolean bool = false;
    if (this.dbOperation.f(this.siteInfo.getAdcode()))
    {
      this.fileLength = this.dbOperation.d(this.siteInfo.getAdcode());
      long[] arrayOfLong = this.dbOperation.a(this.siteInfo.getAdcode(), 0);
      this.b = arrayOfLong[0];
      this.myFileLength = arrayOfLong[1];
      bool = true;
    }
    return bool;
  }
  
  private void a(int paramInt)
  {
    System.err.println("Error Code : " + paramInt);
  }
  
  public void c()
  {
    if (this.downloadManager != null) {
      this.downloadManager.a();
    } else {
      Utility.b("downlaodmnager is null when site stop");
    }
  }
  
  public void d()
  {
    if (this.listener != null) {
      this.listener.onStopDownload();
    }
    k();
  }
  
  public void e()
  {
    j();

    if (this.access != null) {
      this.access.close();
    }
    //MD5校验
    String filename = this.siteInfo.getFileRootPath() + this.siteInfo.getFileTempName();
    if(!checkMD5(this.siteInfo.getMD5(),filename)){
      File file = new File(filename);
      file.delete();
      if (this.listener != null) {
        this.listener.onError(IDownloadListener.ExceptionType.md5_exception);
      }
      return ;
    }

    if (this.listener != null) {
      this.listener.downloadFinish();
    }
    renameZipFile();
    if (this.h != null) {
      this.h.onNetfileFetchFinish();
    }
  }
  private boolean checkMD5(String md5,String filename){
    boolean res = false;
    String fileMD5 = MD5.getFileMD5(filename);
    md5 = md5.toLowerCase();
    fileMD5 = fileMD5.toLowerCase();
    if(md5.equals(fileMD5)){
      res = true;
    }
    return res;
  }
  private void renameZipFile()
  {
    String str1 = this.siteInfo.getFileRootPath() + File.separator + this.siteInfo.getFileTempName();
    File localFile1 = new File(str1);
    
    String str2 = str1.substring(0, str1
      .indexOf(".tmp"));
    File localFile2 = new File(str2);
    localFile1.renameTo(localFile2);
    Utility.log("NetFileFetch Done, Rename " + str1 + " ==> " + str2);
  }
  
  public void a(Throwable paramThrowable)
  {
    Utility.b("onException: " + paramThrowable.getMessage());
    if (this.listener != null) {
      this.listener.onError(IDownloadListener.ExceptionType.network_exception);
    }
    if ((paramThrowable instanceof IOException))
    {
      Utility.b("这里报 io excepiton 需要处理一下");
      return;
    }
    if (this.access != null) {
      this.access.close();
    }
  }
  
  public void a(byte[] paramArrayOfByte, long paramLong)
  {
    try
    {
      this.access.a(paramArrayOfByte);
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
      SDKLogHandler.exception(localIOException, "fileAccessI", "fileAccessI.write(byte[] data)");
      if (this.listener != null) {
        this.listener.onError(IDownloadListener.ExceptionType.file_io_exception);
      }
      if (this.downloadManager != null) {
        this.downloadManager.a();
      }
      return;
    }
    this.b = paramLong;
    j();
  }
  
  public void a(a parama)
  {
    this.h = parama;
  }
  
  public static abstract interface a
  {
    public abstract void onNetfileFetchFinish();
  }
}
