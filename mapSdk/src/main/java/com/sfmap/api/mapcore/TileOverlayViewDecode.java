package com.sfmap.api.mapcore;

import android.content.Context;
import android.view.View;
import com.sfmap.api.mapcore.util.LogManager;
import com.sfmap.api.mapcore.util.Util;
import com.sfmap.api.mapcore.util.SDKLogHandler;
import com.sfmap.api.maps.model.TileOverlayOptions;
import com.sfmap.api.maps.model.UrlTileProvider;
import java.io.Serializable;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.microedition.khronos.opengles.GL10;

public class TileOverlayViewDecode
  extends View
{
  private IMapDelegate mMap; //e
  CopyOnWriteArrayList<ITileOverlayDelegate> mTileOverlays = new CopyOnWriteArrayList<ITileOverlayDelegate>(); // a
  ListOverlayComparator listOverlayComparator = new ListOverlayComparator();
  CopyOnWriteArrayList<Integer> recycleTextureIds = new CopyOnWriteArrayList<Integer>(); //c
  TileOverlayDelegateImp tileOverlayDelegateImp = null;
  
  static class ListOverlayComparator // a
    implements Comparator<Object>, Serializable
  {
    public int compare(Object paramObject1, Object paramObject2)
    {
      ITileOverlayDelegate localao1 = (ITileOverlayDelegate)paramObject1;
      ITileOverlayDelegate localao2 = (ITileOverlayDelegate)paramObject2;
      try
      {
        if ((localao1 != null) && (localao2 != null))
        {
          if (localao1.getZIndex() > localao2.getZIndex()) {
            return 1;
          }
          if (localao1.getZIndex() < localao2.getZIndex()) {
            return -1;
          }
        }
      }
      catch (Throwable localThrowable)
      {
        SDKLogHandler.exception(localThrowable, "TileOverlayView", "compare");
        localThrowable.printStackTrace();
      }
      return 0;
    }
  }
  
  public TileOverlayViewDecode(Context paramContext)
  {
    super(paramContext);
  }
  
  public TileOverlayViewDecode(Context paramContext, IMapDelegate paramaa)
  {
    super(paramContext);
    this.mMap = paramaa;
    UrlTileProvider local1 = new UrlTileProvider(256, 256)
    {
      public URL getTileUrl(int x, int y, int zoom)
      {
        try
        {return null;
        }
        catch (Throwable localThrowable) {}
        return null;
      }
    };
    TileOverlayOptions localTileOverlayOptions = new TileOverlayOptions().tileProvider(local1);
    this.tileOverlayDelegateImp = new TileOverlayDelegateImp(localTileOverlayOptions, this, true);
  }
  
  IMapDelegate getMap() // a
  {
    return this.mMap;
  }
  
  public void onDrawGL(GL10 gl10) // a
  {
    try
    {
      for (Iterator<Integer> localIterator = this.recycleTextureIds.iterator(); localIterator.hasNext();)
      {
        Integer localObject = (Integer)localIterator.next();
        Util.a(gl10, localObject.intValue());
      }
      this.recycleTextureIds.clear();
      this.tileOverlayDelegateImp.drawTiles(gl10);
      for (Iterator<ITileOverlayDelegate> localIterator = this.mTileOverlays.iterator(); localIterator.hasNext();)
      {
        ITileOverlayDelegate localObject = (ITileOverlayDelegate)localIterator.next();
        if (localObject.isVisible()) {
          localObject.drawTiles(gl10);
        }
      }
    }
    catch (Throwable localThrowable)
    {
      LogManager.writeLog(LogManager.productInfo, hashCode() + " TileOverLayView draw exception :" + localThrowable.getMessage(), 115);
    }
  }
  
  public void clear() //b
  {
    for (ITileOverlayDelegate localao : this.mTileOverlays) {
      if (localao != null) {
        localao.remove();
      }
    }
    this.mTileOverlays.clear();
  }
  
  void changeOverlayIndex() //c
  {
    Object[] arrayOfObject1 = this.mTileOverlays.toArray();
    Arrays.sort(arrayOfObject1, this.listOverlayComparator);
    this.mTileOverlays.clear();
    for (Object localObject : arrayOfObject1) {
      this.mTileOverlays.add((ITileOverlayDelegate)localObject);
    }
  }
  
  public void addTileOverlay(ITileOverlayDelegate tileOverlayDelegate) // a
  {
    remove(tileOverlayDelegate);
    this.mTileOverlays.add(tileOverlayDelegate);
    changeOverlayIndex();
  }
  
  public boolean remove(ITileOverlayDelegate paramao) //b
  {
    return this.mTileOverlays.remove(paramao);
  }
  
  public void refresh(boolean needDownload) // a
  {
    this.tileOverlayDelegateImp.refresh(needDownload);
    for (ITileOverlayDelegate localao : this.mTileOverlays) {
      if ((localao != null) && (localao.isVisible())) {
        localao.refresh(needDownload);
      }
    }
  }
  
  public void onPause() // d->onPause
  {
    this.tileOverlayDelegateImp.onPause();
    for (ITileOverlayDelegate localao : this.mTileOverlays) {
      if (localao != null) {
        localao.onPause();
      }
    }
  }
  
  public void onResume() //e->onResume
  {
    this.tileOverlayDelegateImp.onResume();
    for (ITileOverlayDelegate localao : this.mTileOverlays) {
      if (localao != null) {
        localao.onResume();
      }
    }
  }
  
  public void onFling(boolean isFling) //b
  {
    this.tileOverlayDelegateImp.onFling(isFling);
    for (ITileOverlayDelegate tileOverlayDelegate : this.mTileOverlays) {
      if (tileOverlayDelegate != null) {
        tileOverlayDelegate.onFling(isFling);
      }
    }
  }
  
  public void destroy() //f
  {
    this.tileOverlayDelegateImp.remove();
    this.tileOverlayDelegateImp = null;
  }
}
