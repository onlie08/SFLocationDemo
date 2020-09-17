package com.sfmap.api.mapcore.util;

public class NewVersionState
  extends CanDeleteState
{
  public NewVersionState(int paramInt, CityObject paramg)
  {
    super(paramInt, paramg);
  }
  
  public void c()
  {
    this.cityObject.d();
  }
  
  public void start()
  {
    log(this.cityObject.waitingState);
    
    this.cityObject.setCityState(this.cityObject.waitingState);
    this.cityObject.getCityStateImp().c();
  }
}
