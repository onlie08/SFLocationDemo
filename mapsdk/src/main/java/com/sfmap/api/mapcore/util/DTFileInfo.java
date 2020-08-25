package com.sfmap.api.mapcore.util;

class DTFileInfo//w
{
  private String adcode = "";
  private String file = "";
  
  public DTFileInfo() {}
  
  public DTFileInfo(String adcode, String file)
  {
    this.adcode = adcode;
    this.file = file;
  }
  
  public String getAdcode()
  {
    return this.adcode;
  }
  
  public String getFile()
  {
    return this.file;
  }
}
