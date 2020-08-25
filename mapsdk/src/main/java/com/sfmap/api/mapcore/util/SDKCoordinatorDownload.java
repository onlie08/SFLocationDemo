package com.sfmap.api.mapcore.util;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import com.sfmap.api.maps.MapsInitializer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;

public class SDKCoordinatorDownload
  extends Thread
  implements DownloadManager.DownloadListener
{
  private DownloadManager a;
  private a b;
  private RandomAccessFile c;
  private String d;
  private String e;
  private String f;
  private Context g;
  private static String h = "sodownload";
  private static String i = "sofail";
  
  public SDKCoordinatorDownload(Context paramContext, String paramString1, String paramString2, String paramString3)
  {
    this.g = paramContext;
    this.f = paramString3;
    this.d = a(paramContext, paramString1 + "temp.so");
    this.e = a(paramContext, "libwgs2gcj.so");
    
    this.b = new a(paramString2);
    this.a = new DownloadManager(this.b);
  }
  
  public static String a(Context paramContext, String paramString)
  {
    String str = paramContext.getFilesDir().getAbsolutePath() + File.separator + "libso" + File.separator + paramString;
    
    return str;
  }
  
  private static String b(Context paramContext, String paramString)
  {
    return a(paramContext, paramString);
  }
  
  public void a()
  {
    if ((this.b == null) || (TextUtils.isEmpty(this.b.getUrl())) || (!this.b.getUrl().contains("libJni_wgs2gcj.so")) || (!this.b.getUrl().contains(Build.CPU_ABI))) {
      return;
    }
    File localFile = new File(this.e);
    if (localFile.exists()) {
      return;
    }
    start();
  }
  
  public void run()
  {
    try
    {
      File localFile = new File(b(this.g, "tempfile"));
      if (localFile.exists())
      {
        SDKInfo localbv = new SDKInfo.createSDKInfo(i, MapsInitializer.getVersion(), "sodownload_"+MapsInitializer.getVersion(),"").setPackageName(new String[0]).a();
        
        AuthManager.a(this.g, localbv);
        localFile.delete();
      }
      this.a.makeGetRequest(this);
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
      b();
    }
  }
  
  public void a(byte[] paramArrayOfByte, long paramLong)
  {
    try
    {
      if (this.c == null)
      {
        File localFile1 = new File(this.d);
        File localFile2 = localFile1.getParentFile();
        if (!localFile2.exists()) {
          localFile2.mkdirs();
        }
        try
        {
          this.c = new RandomAccessFile(localFile1, "rw");
        }
        catch (FileNotFoundException localFileNotFoundException)
        {
          b();
          localFileNotFoundException.printStackTrace();
        }
      }
      try
      {
        this.c.seek(paramLong);
        this.c.write(paramArrayOfByte);
      }
      catch (IOException localIOException)
      {
        b();
        localIOException.printStackTrace();
      }
    }
    catch (Throwable localThrowable)
    {
      b();
      localThrowable.printStackTrace();
    }
  }
  
  public void d()
  {
    b();
  }
  
  public void e()
  {
    try
    {
      if (this.c != null) {
        this.c.close();
      }
      String str = MD5.getFileMD5(this.d);
      if (str.equalsIgnoreCase(this.f))
      {
        File localObject1 = new File(this.e);
        if (((File)localObject1).exists())
        {
          b();
          return;
        }
        File localObject2 = new File(this.d);
        ((File)localObject2).renameTo(new File(this.e));
        SDKInfo localbv = new SDKInfo.createSDKInfo(h, MapsInitializer.getVersion(), "sodownload_"+MapsInitializer.getVersion(),"").setPackageName(new String[0]).a();
        
        AuthManager.a(this.g, localbv);
      }
      else
      {
        b();
        SDKInfo localObject1 = new SDKInfo.createSDKInfo(i,MapsInitializer.getVersion(), "sodownload_"+MapsInitializer.getVersion(),"").setPackageName(new String[0]).a();
        
        AuthManager.a(this.g, (SDKInfo) localObject1);
      }
    }
    catch (Throwable localThrowable)
    {
      Object localObject2;
      b();
      Object localObject1 = new File(this.e);
      if (((File)localObject1).exists()) {
        ((File)localObject1).delete();
      }
      try
      {
        localObject2 = new SDKInfo.createSDKInfo(i,MapsInitializer.getVersion(), "sodownload_"+MapsInitializer.getVersion(),"").setPackageName(new String[0]).a();
        
        AuthManager.a(this.g, (SDKInfo) localObject2);
      }
      catch (IMMapCoreException localbl)
      {
        localbl.printStackTrace();
      }
      localThrowable.printStackTrace();
    }
  }
  
  public void a(Throwable paramThrowable)
  {
    try
    {
      if (this.c != null) {
        this.c.close();
      }
      b();
      File localFile1 = new File(b(this.g, "tempfile"));
      if (!localFile1.exists()) {
        try
        {
          File localFile2 = localFile1.getParentFile();
          if (!localFile2.exists()) {
            localFile2.mkdir();
          }
          localFile1.createNewFile();
        }
        catch (IOException localIOException)
        {
          BasicLogHandler.a(localIOException, "SDKCoordinatorDownload", "onException");
        }
      }
    }
    catch (Throwable localThrowable)
    {
      BasicLogHandler.a(localThrowable, "SDKCoordinatorDownload", "onException");
    }
  }
  
  private void b()
  {
    File localFile = new File(this.d);
    if (localFile.exists()) {
      localFile.delete();
    }
  }
  
  private static class a   extends Request
  {
    private String a;
    
    a(String paramString)
    {
      this.a = paramString;
    }
    
    public Map<String, String> getHeadMaps()
    {
      return null;
    }
    
    public Map<String, String> getRequestParam()
    {
      return null;
    }
    
    public String getUrl()
    {
      return this.a;
    }
  }
}
