package com.sfmap.api.mapcore;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MapGLTextureView//k
  extends GLTextureView
  implements IGLSurfaceView
{
  private IMapDelegate mapDelegate = null;
  
  public MapGLTextureView(Context context)
  {
    this(context, null);
  }

  public MapGLTextureView(Context context, AttributeSet attributeSet)
  {
    super(context, attributeSet);
    this.mapDelegate = new MapDelegateImp(this, context, null);
  }
  
  public IMapDelegate getMapDelegate()
  {
    return this.mapDelegate;
  }
  
  public boolean onTouchEvent(MotionEvent event)
  {
    super.onTouchEvent(event);
    return this.mapDelegate.onTouchEvent(event);
  }
  
  public void setZOrderOnTop(boolean zOrder) {}
}
