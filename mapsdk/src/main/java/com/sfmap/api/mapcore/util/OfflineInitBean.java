package com.sfmap.api.mapcore.util;

public class OfflineInitBean//i
{
  private boolean hasNewVersion;
  private String version;

  public String getVersion() {
    return version;
  }

  public boolean hasNewVersion()
  {
    return this.hasNewVersion;
  }
  
  public void setHasNewVersion(boolean hasNewVersion)
  {
    this.hasNewVersion = hasNewVersion;
  }
  
  public void setVersion(String version)
  {
    this.version = version;
  }
}
