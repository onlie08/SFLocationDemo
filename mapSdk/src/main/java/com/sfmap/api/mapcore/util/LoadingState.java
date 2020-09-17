package com.sfmap.api.mapcore.util;

public class LoadingState
  extends CanDeleteState
{
  public LoadingState(int paramInt, CityObject paramg)
  {
    super(paramInt, paramg);
  }
  
  public void c()
  {
    this.cityObject.d();
  }
  
  public void pause()
  {
    log(this.cityObject.pauseState);
    
    this.cityObject.setCityState(this.cityObject.pauseState);
    
    this.cityObject.getCityStateImp().c();
  }
  
  public void complete()
  {
    log(this.cityObject.unzipState);
    
    this.cityObject.setCityState(this.cityObject.unzipState);
    
    this.cityObject.getCityStateImp().c();
  }
  
  public void fail()
  {
    log(this.cityObject.errorState);
    
    this.cityObject.setCityState(this.cityObject.errorState);
    
    this.cityObject.getCityStateImp().c();
  }
}
