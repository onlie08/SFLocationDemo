package com.sfmap.api.mapcore;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MapGLSurfaceView extends GLSurfaceView implements IGLSurfaceView
{
  private IMapDelegate map = null;
  
  public MapGLSurfaceView(Context context)
  {
    this(context, null);
  }

  public MapGLSurfaceView(Context context, AttributeSet attributeSet)
  {
    super(context, attributeSet);
    this.map = new MapDelegateImp(this, context, null);
  }

  public IMapDelegate getMapDelegate()
  {

    return this.map;
  }
  
  public boolean onTouchEvent(MotionEvent motionEvent)
  {
    super.onTouchEvent(motionEvent);
    return this.map.onTouchEvent(motionEvent);
  }
}
