package com.sfmap.api.mapcore.util;

public class CompleteState
  extends CanDeleteState
{
  public CompleteState(int paramInt, CityObject paramg)
  {
    super(paramInt, paramg);
  }
  
  public void c()
  {
    this.cityObject.removeTask();
  }
  
  public void hasNew()
  {
    log(this.cityObject.newVersionState);
    
    this.cityObject.setCityState(this.cityObject.newVersionState);
    
    this.cityObject.d();
  }
}
