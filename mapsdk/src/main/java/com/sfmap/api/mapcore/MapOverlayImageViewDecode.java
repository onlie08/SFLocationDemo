package com.sfmap.api.mapcore;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.sfmap.api.mapcore.util.SDKLogHandler;
import com.sfmap.api.maps.model.BitmapDescriptor;
import com.sfmap.api.maps.model.Marker;
import com.sfmap.mapcore.FPoint;
import com.sfmap.mapcore.IPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.microedition.khronos.opengles.GL10;

class MapOverlayImageViewDecode

{
  private Lock lock=new ReentrantLock();
  IMapDelegate map;
  private CopyOnWriteArrayList<IMarkerDelegate> markers = new CopyOnWriteArrayList<IMarkerDelegate>(new ArrayList(500));
  private CopyOnWriteArrayList<OverlayTextureItem> overlayitem = new CopyOnWriteArrayList<OverlayTextureItem>();
  private CopyOnWriteArrayList<Integer> recycleTextureIds = new CopyOnWriteArrayList<Integer>();
  private final ZIndexComparator comparator = new ZIndexComparator();
  private IPoint mHitMarkPoint;
  private IMarkerDelegate mHitMarker;
  private Handler handler = new Handler();
  private Runnable sortMarkerRunnable = new Runnable(){ //原始逆向出来的类是aw,此处将其省略
	  public synchronized void run()
	    {
		  //av.a(this.a);
	      changeOverlayIndex(); //上一句翻译
	    }
  };

  public MapOverlayImageViewDecode(Context context)
  {

  }

  public MapOverlayImageViewDecode(Context context, AttributeSet attrs, IMapDelegate map)
  {

    this.map = map;
  }

  protected synchronized int getMarkersSize()
  {
    return this.markers.size();
  }

  public synchronized void clear(String paramString)
  {
    try
    {
      int m = (paramString == null) || (paramString.trim().length() == 0) ? 1 : 0;
      this.mHitMarker = null;
      this.mHitMarkPoint = null;
      if (m != 0)
      {
        for (Iterator<IMarkerDelegate> localIterator = this.markers.iterator(); localIterator.hasNext();)
        {
          IMarkerDelegate localag = (IMarkerDelegate)localIterator.next();
          localag.remove();
        }
        this.markers.clear();
      }
      else
      {
        for (Iterator<IMarkerDelegate> localIterator = this.markers.iterator(); localIterator.hasNext();)
        {
          IMarkerDelegate localag = (IMarkerDelegate)localIterator.next();
          if (!paramString.equals(localag.getId())) {
            localag.remove();
          }
        }
      }
    }
    catch (RemoteException localRemoteException)
    {
      //Iterator localIterator;
      //ag localag;
      SDKLogHandler.exception(localRemoteException, "MapOverlayImageView", "clear");
      localRemoteException.printStackTrace();
    }
  }

  public synchronized void addMarker(IMarkerDelegate paramag)
  {
    this.markers.add(paramag);
    sortMarkers();
  }

  public  boolean removeMarker(IMarkerDelegate marker) {
    boolean result = false;
    try {
      lock.tryLock(2, TimeUnit.SECONDS);
      hideInfoWindow(marker);
      result = this.markers.remove(marker);
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      lock.unlock();
    }
    return result;
  }

  public synchronized void set2Top(IMarkerDelegate marker)
  {
    try
    {
      if (this.markers.remove(marker))
      {
        changeOverlayIndex();
        this.markers.add(marker);
      }
    }
    catch (Throwable throwable)
    {
      SDKLogHandler.exception(throwable, "MapOverlayImageView", "set2Top");
    }
  }

  public void showInfoWindow(IMarkerDelegate marker)
  {
    if (this.mHitMarkPoint == null) {
      this.mHitMarkPoint = new IPoint();
    }
    Rect rect = marker.getRect();
    this.mHitMarkPoint = new IPoint(rect.left + rect.width() / 2, rect.top);
    this.mHitMarker = marker;
    try
    {
      this.map.showInfoWindow(this.mHitMarker);
    }
    catch (Throwable throwable)
    {
      SDKLogHandler.exception(throwable, "MapOverlayImageView", "showInfoWindow");

      throwable.printStackTrace();
    }
  }

  public void hideInfoWindow(IMarkerDelegate marker)
  {
    try
    {
      if (marker.isInfoWindowShow())
      {
        this.map.hiddenInfoWindowShown();
        this.mHitMarker = null;
      }
      else if ((this.mHitMarker != null) && (this.mHitMarker.getId() == marker.getId()))
      {
        this.mHitMarker = null;
      }
    }
    catch (Throwable throwable)
    {
      throwable.printStackTrace();
    }
  }

  private final Runnable reDrawRunnable = new Runnable(){ //原始逆向出来的类是ax,此处将其省略
	  public void run()
	  {
	    try
	    {
	      //this.a.a.p();
	    	map.redrawInfoWindow(); //上一句翻译
	    }
	    catch (Throwable throwable)
	    {
	      SDKLogHandler.exception(throwable, "MapOverlayImageView", "redrawInfoWindow post");

	      throwable.printStackTrace();
	    }
	  }
  };

  public synchronized void calFPoint()
  {
    for (IMarkerDelegate marker : this.markers) {
      try
      {
        if (marker.isVisible()) {
          marker.calFPoint();
        }
      }
      catch (Throwable throwable)
      {
        SDKLogHandler.exception(throwable, "MapOverlayImageView", "calFPoint");

        throwable.printStackTrace();
      }
    }
  }

  private void changeOverlayIndex() //j
  {
    try
    {
      ArrayList<IMarkerDelegate> localArrayList = new ArrayList<IMarkerDelegate>(this.markers);

      Collections.sort(localArrayList, this.comparator);
      this.markers = new CopyOnWriteArrayList<IMarkerDelegate>(localArrayList);
    }
    catch (Throwable throwable)
    {
      SDKLogHandler.exception(throwable, "MapOverlayImageView", "changeOverlayIndex");
    }
  }

  public void onDrawGL(GL10 gl)
  {
	  for (Integer id : recycleTextureIds) {
      gl.glDeleteTextures(1, new int[]{id}, 0);
      this.map.deleteTexsureId(id.intValue());
    }

    this.recycleTextureIds.clear();
    if ((this.mHitMarker != null) && (!this.mHitMarker.F())) {
      postDraw();
    }

    for (IMarkerDelegate marker : markers) {
      if ((marker.isInScreen()) || (marker.isInfoWindowShow())) {
        marker.drawMarker(gl, this.map);
      }
    }
  }

  public synchronized boolean c()
  {
    for (IMarkerDelegate localag : this.markers) {
      if (!localag.c()) {
        return false;
      }
    }
    return true;
  }

  public IMarkerDelegate getHitMarker()
  {
    return this.mHitMarker;
  }

  public IMarkerDelegate getLongPressHitMarker(MotionEvent paramMotionEvent)
  {
    for (IMarkerDelegate marker : this.markers) {
      if ((marker instanceof MarkerDelegateImp))
      {
        Rect localRect = marker.getRect();
        boolean retvalue = hitTest(localRect, (int)paramMotionEvent.getX(), (int)paramMotionEvent.getY());
        if (retvalue)
        {
          this.mHitMarker = marker;
          return this.mHitMarker;
        }
      }
    }
    return null;
  }

  public synchronized void a(OverlayTextureItem overlayTextureItem)
  {
    if (overlayTextureItem == null) {
      return;
    }
    if (overlayTextureItem.b() == 0) {
      return;
    }
    this.overlayitem.add(overlayTextureItem);
  }

  public synchronized void a(int paramInt)
  {
    for (OverlayTextureItem localbd : this.overlayitem) {
      if (localbd.b() == paramInt) {
        this.overlayitem.remove(localbd);
      }
    }
  }

  public void a(Integer paramInteger)
  {
    if (paramInteger.intValue() != 0) {
      this.recycleTextureIds.add(paramInteger);
    }
  }

  public synchronized int a(BitmapDescriptor paramBitmapDescriptor)
  {
    if ((paramBitmapDescriptor == null) || (paramBitmapDescriptor.getBitmap() == null) ||
      (paramBitmapDescriptor.getBitmap().isRecycled())) {
      return 0;
    }
    OverlayTextureItem overlayTextureItem = null;
    for (int m = 0; m < this.overlayitem.size(); m++)
    {
      overlayTextureItem = (OverlayTextureItem)this.overlayitem.get(m);
      if (overlayTextureItem.getBitmapDes().equals(paramBitmapDescriptor)) {
        return overlayTextureItem.b();
      }
    }
    return 0;
  }

  public synchronized void destroy() //e
  {
    try
    {
    	for (IMarkerDelegate mk : markers) {
        if (mk != null)
        {
          mk.destroy();
          mk = null;
        }
      }
      // a(null);
      clear(null); //上一句翻译
      for (Iterator<OverlayTextureItem> localIterator = this.overlayitem.iterator(); localIterator.hasNext();)
      {
        OverlayTextureItem localObject = (OverlayTextureItem)localIterator.next();
        ((OverlayTextureItem)localObject).getBitmapDes().recycle();
      }
      this.overlayitem.clear();
    }
    catch (Throwable localThrowable)
    {
      SDKLogHandler.exception(localThrowable, "MapOverlayImageView", "destroy");
      localThrowable.printStackTrace();
      Log.d("MapSDK", "MapOverlayImageView clear erro" + localThrowable.getMessage());
    }
  }
  
  public boolean b(MotionEvent paramMotionEvent)
    throws RemoteException
  {
    boolean bool1 = false;
    int markerCount = this.markers.size();

    for (int index = markerCount - 1; index >= 0; index--) {
      IMarkerDelegate marker = markers.get(index);
      if (((marker instanceof MarkerDelegateImp)) && (marker.isVisible()))
      {
        Rect localRect = marker.getRect();
        boolean bool2 = hitTest(localRect, (int)paramMotionEvent.getX(), (int)paramMotionEvent.getY());
        if (bool2)
        {
          bool1 = bool2;
          
          this.mHitMarkPoint = new IPoint(localRect.left + localRect.width() / 2, localRect.top);
          this.mHitMarker = marker;
          break;
        }
      }
    }
    return bool1;
  }
  
  public boolean hitTest(Rect rect, int x, int y)
  {
    return rect.contains(x, y);
  }
  
  public synchronized List<Marker> getMapScreenMarkers()
  {
    ArrayList<Marker> localArrayList = new ArrayList<Marker>();
    try
    {
      Rect localRect = new Rect(0, 0, this.map.getMapWidth(), this.map.getMapHeight());
      FPoint fPoint = null;
      IPoint iPoint = new IPoint();
      for (IMarkerDelegate localag : this.markers) {
        if (!(localag instanceof TextDelegateImp))
        {
          fPoint = localag.anchorUVoff();
          if (fPoint != null)
          {
            this.map.getMapProjection().map2Win(fPoint.x, fPoint.y, iPoint);
            if (hitTest(localRect, iPoint.x, iPoint.y)) {
              localArrayList.add(new Marker(localag));
            }
          }
        }
      }
    }
    catch (Throwable localThrowable)
    {
//      Rect localRect;
//      FPoint localFPoint;
//      IPoint localIPoint;
      SDKLogHandler.exception(localThrowable, "MapOverlayImageView", "getMapScreenMarkers");
      
      localThrowable.printStackTrace();
    }
    return localArrayList;
  }
  
  public synchronized void g()
  {
    for (IMarkerDelegate localag : this.markers) {
      if (localag.x()) {
        localag.realdestroy();
      }
    }
  }
  
  protected synchronized void sortMarkers()
  {
    this.handler.removeCallbacks(this.sortMarkerRunnable);
    this.handler.postDelayed(this.sortMarkerRunnable, 10L);
  }
  
  static class ZIndexComparator implements Comparator<Object>, Serializable {
    public int compare(Object paramObject1, Object paramObject2) {
      IMarkerDelegate marker1 = (IMarkerDelegate)paramObject1;
      IMarkerDelegate marker2 = (IMarkerDelegate)paramObject2;
      try {
        if ((marker1 != null) && (marker2 != null)) {
          return (int) (marker1.getZIndex() - marker2.getZIndex());
        }
      } catch (Throwable localThrowable) {
        SDKLogHandler.exception(localThrowable, "MapOverlayImageView", "compare");
        localThrowable.printStackTrace();
      }
      return 0;
    }
  }
  
  public void postDraw()
  {
    this.handler.post(this.reDrawRunnable);
  }
}
