package com.sfmap.api.mapcore.util;

public abstract class CityStateImp//al
{
  protected int state;
  protected CityObject cityObject;
  
  public CityStateImp(int state, CityObject cityObject)
  {
    this.state = state;
    this.cityObject = cityObject;
  }
  
  public int getState()
  {
    return this.state;
  }
  
  public boolean a(CityStateImp paramal)
  {
    return paramal.getState() == getState();
  }
  
  public void log(CityStateImp paramal)
  {
    Utility.log(getState() + " ==> " + paramal.getState() + "   " + getClass() + "==>" + paramal.getClass());
  }
  
  public abstract void c();
  
  public void start()
  {
    Utility.log("Wrong call start()  State: " + getState() + "  " + getClass());
  }
  
  public void continueDownload()
  {
    Utility.log("Wrong call continueDownload()  State: " + getState() + "  " + getClass());
  }
  
  public void pause()
  {
    Utility.log("Wrong call pause()  State: " + getState() + "  " + getClass());
  }
  
  public void delete()
  {
    Utility.log("Wrong call delete()  State: " + getState() + "  " + getClass());
  }
  
  public void fail()
  {
    Utility.log("Wrong call fail()  State: " + getState() + "  " + getClass());
  }
  
  public void hasNew()
  {
    Utility.log("Wrong call hasNew()  State: " + getState() + "  " + getClass());
  }
  
  public void complete()
  {
    Utility.log("Wrong call complete()  State: " + getState() + "  " + getClass());
  }
}
