package com.sfmap.api.mapcore.util;

import java.net.Proxy;

public class DownloadManager
{
  private HttpUrlUtil httpUtil;
  private Request request;
  
  public DownloadManager(Request paramdp)
  {
    this(paramdp, 0L, -1L);
  }
  
  public DownloadManager(Request paramdp, long paramLong1, long paramLong2)
  {
    this.request = paramdp;
    Proxy proxy;
    if (paramdp.proxy == null) {
      proxy = null;
    } else {
      proxy = paramdp.proxy;
    }
    this.httpUtil = new HttpUrlUtil(this.request.connTimeout, this.request.readTimeout, proxy);
    
    this.httpUtil.b(paramLong2);
    this.httpUtil.a(paramLong1);
  }
  
  public void makeGetRequest(DownloadListener listener)
  {
    this.httpUtil.makeGetRequest(this.request.getUrl(), this.request.getHeadMaps(), this.request.getRequestParam(), listener);
  }
  
  public void a()
  {
    this.httpUtil.a();
  }
  
  public static abstract interface DownloadListener
  {
    public abstract void a(byte[] paramArrayOfByte, long paramLong);
    
    public abstract void d();
    
    public abstract void e();
    
    public abstract void a(Throwable paramThrowable);
  }
}
