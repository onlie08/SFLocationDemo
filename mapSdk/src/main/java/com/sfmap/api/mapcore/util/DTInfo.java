package com.sfmap.api.mapcore.util;

class DTInfo//x
{
  private UpdateItem a = null;
  
  public DTInfo(UpdateItem paramr)
  {
    this.a = paramr;
  }
  
  public UpdateItem a()
  {
    return this.a;
  }
  
  public String b()
  {
    return this.a.getCity();
  }
  
  public String c()
  {
    return this.a.getUrl();
  }
  
  public String d()
  {
    return this.a.getAdcode();
  }
  
  public String e()
  {
    return this.a.getDataFileTempPath();
  }
  
  public String getVersion()
  {
    return this.a.getVersion();
  }
  
  public long g()
  {
    return this.a.getLocalSize();
  }
  
  public long h()
  {
    return this.a.getRemoteLength();
  }
  
  public String i()
  {
    return this.a.getLocalPath();
  }
  
  public int j()
  {
    return this.a.getIndex();
  }
  
  public boolean k()
  {
    return this.a.isSheng();
  }
  
  public int l()
  {
    return this.a.getCompleteCode();
  }
  
  public String m()
  {
    return this.a.getCode();
  }
  
  public int n()
  {
    return this.a.state;
  }
}
