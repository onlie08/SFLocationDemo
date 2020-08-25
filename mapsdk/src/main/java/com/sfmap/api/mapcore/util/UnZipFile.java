package com.sfmap.api.mapcore.util;

import android.text.TextUtils;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnZipFile//af
{
  private b a;
  
  public UnZipFile(String paramString1, String paramString2, IUnZipListener paramac)
  {
    this.a = new b(paramString1, paramString2, paramac);
  }
  
  public void a()
  {
    if (this.a != null) {
      this.a.f();
    }
  }
  
  public void unzipFileTaskItem()
  {
    if (this.a != null) {
      a(this.a);
    } else {
      Utility.b("UnzipFile unzipFileTaskItem is null!");
    }
  }
  
  private static void a(b paramb)
  {
    if (paramb == null) {
      return;
    }
    IUnZipListener localac = paramb.d();
    if (localac != null) {
      localac.onUnZipStart();
    }
    String str1 = paramb.a();
    
    String str2 = paramb.b();
    if ((TextUtils.isEmpty(str1)) || 
      (TextUtils.isEmpty(str2)))
    {
      if (paramb.e().a)
      {
        if (localac != null) {
          localac.q();
        }
      }
      else if (localac != null) {
        localac.p();
      }
      return;
    }
    File localFile1 = new File(str1);
    if (!localFile1.exists())
    {
      if (paramb.e().a)
      {
        if (localac != null) {
          localac.q();
        }
      }
      else if (localac != null) {
        localac.p();
      }
      return;
    }
    File localFile2 = new File(str2);
    if ((!localFile2.exists()) && 
      (!localFile2.mkdirs())) {}
    c local1 = new ddd(localac); //匿名类改为实际的类来实现
//    {
//    	ac
//      public void a(long paramAnonymousLong)
//      {
//        try
//        {
//          //if (this.a != null) {
//          //  this.a.a(paramAnonymousLong);
//          //}
//        }
//        catch (Exception localException)
//        {
//          ag.b("UnzipFile zip Exception: " + localException.getMessage());
//        }
//      }
//
//      public void a()
//      {
//        //if (this.a != null) {
//        //  this.a.p();
//        //}
//      }
//    };
    try
    {
      if ((paramb.e().a) && 
        (localac != null)) {
        localac.q();
      }
      a(localFile1, localFile2, local1, paramb);
      if (paramb.e().a)
      {
        if (localac != null) {
          localac.q();
        }
      }
      else if (localac != null) {
        localac.onUnzipFinish(paramb.c());
      }
    }
    catch (Exception e)
    {
      if (paramb.e().a)
      {
        if (localac != null) {
          localac.q();
        }
      }
      else if (localac != null) {
        localac.p();
      }
    }
  }
  
  private static void a(File paramFile1, File paramFile2, c paramc, b paramb)
    throws Exception
  {
    StringBuffer localStringBuffer = new StringBuffer();
    a locala = paramb.e();
    
    long l1 = 0L;
    if (paramc != null) {
      try
      {
        FileInputStream localFileInputStream1 = new FileInputStream(paramFile1);
        CheckedInputStream localCheckedInputStream = new CheckedInputStream(localFileInputStream1, new CRC32());
        
        ZipInputStream localZipInputStream = new ZipInputStream(localCheckedInputStream);
        
        ZipEntry localZipEntry = null;
        while ((localZipEntry = localZipInputStream.getNextEntry()) != null)
        {
          if ((locala != null) && (locala.a))
          {
            localZipInputStream.closeEntry();
            localZipInputStream.close();
            localCheckedInputStream.close();
            localFileInputStream1.close();
            break;
          }
          if (!localZipEntry.isDirectory())
          {
            String str = localZipEntry.getName();
            if (!a(str))
            {
              paramc.a();
              break;
            }
            localStringBuffer.append(localZipEntry.getName()).append(";");
          }
          long l2 = localZipEntry.getSize();
          l1 += l2;
          localZipInputStream.closeEntry();
        }
        paramb.a(localStringBuffer.toString());
        localZipInputStream.close();
        localCheckedInputStream.close();
        localFileInputStream1.close();
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
      }
    }
    FileInputStream localFileInputStream2 = new FileInputStream(paramFile1);
    CheckedInputStream localCheckedInputStream = new CheckedInputStream(localFileInputStream2, new CRC32());
    ZipInputStream localZipInputStream = new ZipInputStream(localCheckedInputStream);
    a(paramFile2, localZipInputStream, l1, paramc, locala);
    localZipInputStream.close();
    localCheckedInputStream.close();
    localFileInputStream2.close();
  }
  
  private static void a(File paramFile, ZipInputStream paramZipInputStream, long paramLong, c paramc, a parama)
    throws Exception
  {
    ZipEntry localZipEntry = null;
    int i = 0;
    int j = 0;
    while ((localZipEntry = paramZipInputStream.getNextEntry()) != null)
    {
      if ((parama != null) && (parama.a))
      {
        paramZipInputStream.closeEntry();
        return;
      }
      String str = paramFile.getPath() + File.separator + localZipEntry.getName();
      if (!a(str))
      {
        if (paramc != null) {
          paramc.a();
        }
        Utility.b("ZipEntry.getName contains ../ !");
        return;
      }
      File localFile = new File(str);
      
      a(localFile);
      if (localZipEntry.isDirectory())
      {
        if (localFile.mkdirs()) {}
      }
      else
      {
        j = a(localFile, paramZipInputStream, i, paramLong, paramc, parama);
        
        i += j;
      }
      paramZipInputStream.closeEntry();
    }
  }
  
  private static boolean a(String paramString)
  {
    if (paramString.contains("../")) {
      return false;
    }
    return true;
  }
  
  private static int a(File paramFile, ZipInputStream paramZipInputStream, long paramLong1, long paramLong2, c paramc, a parama)
    throws Exception
  {
    int i = 0;
    BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(new FileOutputStream(paramFile));
    
    byte[] arrayOfByte = new byte['Ѐ']; //????? 得转为数字
    int j;
    while ((j = paramZipInputStream.read(arrayOfByte, 0, 1024)) != -1)
    {
      if ((parama != null) && (parama.a))
      {
        localBufferedOutputStream.close();
        return i;
      }
      localBufferedOutputStream.write(arrayOfByte, 0, j);
      i += j;
      if ((paramLong2 > 0L) && (paramc != null))
      {
        long l = (paramLong1 + i) * 100L / paramLong2;
        if ((parama == null) || (!parama.a)) {
          paramc.a(l);
        }
      }
    }
    localBufferedOutputStream.close();
    return i;
  }
  
  private static void a(File paramFile)
  {
    File localFile = paramFile.getParentFile();
    if (!localFile.exists())
    {
      a(localFile);
      if (localFile.mkdir()) {}
    }
  }
  
  private class b
  {
    private String b;
    private String c;
    private IUnZipListener d = null;
    private UnZipFile.a e = new UnZipFile.a();
    private String f;
    
    public b(String paramString1, String paramString2, IUnZipListener paramac)
    {
      this.b = paramString1;
      this.c = paramString2;
      this.d = paramac;
    }
    
    public void a(String paramString)
    {
      if (paramString.length() > 1) {
        this.f = paramString;
      }
    }
    
    public String a()
    {
      return this.b;
    }
    
    public String b()
    {
      return this.c;
    }
    
    public String c()
    {
      return this.f;
    }
    
    public IUnZipListener d()
    {
      return this.d;
    }
    
    public UnZipFile.a e()
    {
      return this.e;
    }
    
    public void f()
    {
      this.e.a = true;
    }
  }
  
  public static abstract interface c
  {
    public abstract void a(long paramLong);
    
    public abstract void a();
  }
  
  public static class ddd implements c{
	  private IUnZipListener a;
	  public ddd(IUnZipListener arg){
		  a = arg;
	  }
	  
	@Override
	public void a(long paramLong) {
		try {
            if(this.a == null) {
                return;
            }

            this.a.onUnzipSchedule(paramLong);
        }
        catch(Exception v0) {
            Utility.b("UnzipFile zip Exception: " + v0.getMessage());
        }
	}

	@Override
	public void a() {
		if(this.a != null) {
            this.a.p();
        }
	}
  }
  
  public static class a
  {
    public boolean a = false;
  }
}
