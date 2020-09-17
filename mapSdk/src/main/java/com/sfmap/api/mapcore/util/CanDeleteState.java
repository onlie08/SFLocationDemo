package com.sfmap.api.mapcore.util;

public abstract class CanDeleteState//ak
  extends CityStateImp
{
  public CanDeleteState(int state, CityObject cityObject)
  {
    super(state, cityObject);
  }
  
  public void delete()
  {
    log(this.cityObject.defaultState);
    
    this.cityObject.setCityState(this.cityObject.defaultState);
    
    this.cityObject.d();
  }
}
