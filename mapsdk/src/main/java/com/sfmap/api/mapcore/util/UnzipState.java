package com.sfmap.api.mapcore.util;

public class UnzipState
  extends CityStateImp
{
  public UnzipState(int paramInt, CityObject paramg)
  {
    super(paramInt, paramg);
  }
  
  public void c()
  {
    this.cityObject.d();
  }
  
  public void start()
  {
    this.cityObject.d();
  }
  
  public void complete()
  {
    log(this.cityObject.completeState);
    
    this.cityObject.setCityState(this.cityObject.completeState);
    
    this.cityObject.getCityStateImp().c();
  }
  
  public void fail()
  {
    log(this.cityObject.errorState);
    
    this.cityObject.setCityState(this.cityObject.errorState);
    
    this.cityObject.getCityStateImp().c();
  }
}
