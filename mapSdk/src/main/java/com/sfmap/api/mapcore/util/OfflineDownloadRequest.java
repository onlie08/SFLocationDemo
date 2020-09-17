package com.sfmap.api.mapcore.util;

import java.util.Map;

public class OfflineDownloadRequest//ai
  extends Request
{
  private String url;
  
  public OfflineDownloadRequest(String url)
  {
    this.url = url;
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
    return this.url;
  }
}
