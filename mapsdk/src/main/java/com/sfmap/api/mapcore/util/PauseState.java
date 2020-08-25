package com.sfmap.api.mapcore.util;

public class PauseState
  extends CanDeleteState
{
  public PauseState(int paramInt, CityObject paramg)
  {
    super(paramInt, paramg);
  }
  
  public void c()
  {
    this.cityObject.d();
    
    this.cityObject.k();
  }
  
  public void continueDownload()
  {
    log(this.cityObject.waitingState);
    
    this.cityObject.setCityState(this.cityObject.waitingState);
    
    this.cityObject.getCityStateImp().c();
  }
}
