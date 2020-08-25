package com.sfmap.api.mapcore.util;

import com.sfmap.mapcore.DPoint;

public class Bounds
{
  public final double left;
  public final double top;
  public final double right;
  public final double bottom;
  public final double centerY;
  public final double centerX;
  
  public Bounds(double left, double right, double top, double bottom)
  {
    this.left = left;
    this.top = top;
    this.right = right;
    this.bottom = bottom;
    
    this.centerY = ((left + right) / 2.0D);
    this.centerX = ((top + bottom) / 2.0D);
  }
  
  public boolean covers(double x, double y)
  {
    return (this.left <= x) && (x <= this.right) && (this.top <= y) && (y <= this.bottom);
  }
  
  public boolean covers(DPoint paramDPoint)
  {
    return covers(paramDPoint.x, paramDPoint.y);
  }
  
  public boolean contains(double left, double right, double top, double bottom)
  {
    return (left < this.right) && (this.left < right) && (top < this.bottom) && (this.top < bottom);
  }
  
  public boolean contains(Bounds bounds)
  {
    return contains(bounds.left, bounds.right, bounds.top, bounds.bottom);
  }

  /**
   * 是否包含bounds。
   *
   * @param bounds
   * @return
     */
  public boolean include(Bounds bounds)
  {
    return (bounds.left >= this.left) && (bounds.right <= this.right) && (bounds.top >= this.top) && (bounds.bottom <= this.bottom);
  }
}
