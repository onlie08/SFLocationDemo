package com.sfmap.api.mapcore.util;

public class ErrorState//ao
  extends CanDeleteState
{
  public ErrorState(int paramInt, CityObject paramg)
  {
    super(paramInt, paramg);
  }
  
  public void c()
  {
    this.cityObject.removeTask();
  }
  
  public void start()
  {
    log(this.cityObject.waitingState);
    
    this.cityObject.setCityState(this.cityObject.waitingState);
    
    this.cityObject.d();
    
    this.cityObject.getCityStateImp().c();
  }
}
