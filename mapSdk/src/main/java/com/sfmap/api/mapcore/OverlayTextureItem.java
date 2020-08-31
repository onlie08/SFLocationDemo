package com.sfmap.api.mapcore;

import com.sfmap.api.maps.model.BitmapDescriptor;

public class OverlayTextureItem
{
  private BitmapDescriptor bitmapDescriptor;
  private int b;
  
  public OverlayTextureItem(BitmapDescriptor paramBitmapDescriptor, int paramInt)
  {
    this.bitmapDescriptor = paramBitmapDescriptor;
    this.b = paramInt;
  }
  
  public BitmapDescriptor getBitmapDes()
  {
    return this.bitmapDescriptor;
  }
  
  public int b()
  {
    return this.b;
  }
}
