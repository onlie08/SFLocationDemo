package com.sfmap.api.mapcore.util;

public class DefaultState//an
  extends CityStateImp
{
  public DefaultState(int state, CityObject cityObject)
  {
    super(state, cityObject);
  }
  
  public void c()
  {
    log(this.cityObject.waitingState);
    
    this.cityObject.setCityState(this.cityObject.waitingState);
    
    this.cityObject.d();
    
    this.cityObject.getCityStateImp().c();
  }
}
