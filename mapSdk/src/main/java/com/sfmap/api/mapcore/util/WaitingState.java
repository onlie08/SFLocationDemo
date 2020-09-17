package com.sfmap.api.mapcore.util;

public class WaitingState//at
  extends CanDeleteState
{
  public WaitingState(int paramInt, CityObject paramg)
  {
    super(paramInt, paramg);
  }
  
  public void c()
  {
    this.cityObject.download();
    
    this.cityObject.d();
  }
  
  public void start()
  {
    log(this.cityObject.loadingState);
    
    this.cityObject.setCityState(this.cityObject.loadingState);
    
    this.cityObject.getCityStateImp().c();
  }
  
  public void pause()
  {
    log(this.cityObject.pauseState);
    
    this.cityObject.setCityState(this.cityObject.pauseState);
    
    this.cityObject.getCityStateImp().c();
  }
  
  public void fail()
  {
    log(this.cityObject.errorState);
    
    this.cityObject.setCityState(this.cityObject.errorState);
    
    this.cityObject.getCityStateImp().c();
  }
}
