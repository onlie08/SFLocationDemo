package com.sfmap.api.mapcore;

import android.os.RemoteException;
import javax.microedition.khronos.opengles.GL10;

/**
 * 覆盖物抽象类
 */
public abstract interface IOverlayDelegateDecode
{
  public abstract void remove()  //b
    throws RemoteException;
  
  public abstract String getId() //c
    throws RemoteException;
  
  public abstract void setZIndex(float zIndex) // a
    throws RemoteException;
  
  public abstract float getZIndex() // d
    throws RemoteException;
  
  public abstract void setVisible(boolean visible) // a
    throws RemoteException;
  
  public abstract boolean isVisible() //e
    throws RemoteException;
  
  public abstract boolean equals(IOverlayDelegateDecode overlay) // a
    throws RemoteException;
  
  public abstract int hashCodeRemote() //f
    throws RemoteException;
  /**
   * 计算位置
   *
   * @throws RemoteException
   */
  public abstract void calMapFPoint() //g
    throws RemoteException;
  
  public abstract void draw(GL10 gl) // a
    throws RemoteException;
  
  public abstract void destroy(); //j
  
  public abstract boolean a(); // a
  
  public abstract boolean checkInBounds(); //k
}
