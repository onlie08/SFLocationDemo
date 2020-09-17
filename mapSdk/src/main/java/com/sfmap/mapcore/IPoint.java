package com.sfmap.mapcore;

public class IPoint
{
  public int x;
  public int y;
  
  public IPoint() {}
  
  public IPoint(int x, int y)
  {
    this.x = x;
    this.y = y;
  }
  
  public Object clone()
  {
    IPoint localIPoint = null;
    try
    {
      localIPoint = (IPoint)super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      localCloneNotSupportedException.printStackTrace();
    }
    return localIPoint;
  }
}
