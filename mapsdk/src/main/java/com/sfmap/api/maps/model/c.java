package com.sfmap.api.maps.model;

import com.sfmap.api.mapcore.util.Bounds;
import com.sfmap.mapcore.DPoint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

class c
{
  private final Bounds bounds;
  private final int b;
  private List<WeightedLatLng> data;
  private List<c> d = null;
  
  protected c(Bounds paramay)
  {
    this(paramay, 0);
  }
  
  private c(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, int paramInt)
  {
    this(new Bounds(paramDouble1, paramDouble2, paramDouble3, paramDouble4), paramInt);
  }
  
  private c(Bounds paramay, int b)
  {
    this.bounds = paramay;
    this.b = b;
  }
  
  protected void a(WeightedLatLng paramWeightedLatLng)
  {
    DPoint localDPoint = paramWeightedLatLng.getPoint();
    if (this.bounds.covers(localDPoint.x, localDPoint.y)) {
      a(localDPoint.x, localDPoint.y, paramWeightedLatLng);
    }
  }
  
  private void a(double paramDouble1, double paramDouble2, WeightedLatLng paramWeightedLatLng)
  {
    if (this.d != null)
    {
      if (paramDouble2 < this.bounds.centerX)
      {
        if (paramDouble1 < this.bounds.centerY) {
          ((c)this.d.get(0)).a(paramDouble1, paramDouble2, paramWeightedLatLng);
        } else {
          ((c)this.d.get(1)).a(paramDouble1, paramDouble2, paramWeightedLatLng);
        }
      }
      else if (paramDouble1 < this.bounds.centerY) {
        ((c)this.d.get(2)).a(paramDouble1, paramDouble2, paramWeightedLatLng);
      } else {
        ((c)this.d.get(3)).a(paramDouble1, paramDouble2, paramWeightedLatLng);
      }
      return;
    }
    if (this.data == null) {
      this.data = new ArrayList();
    }
    this.data.add(paramWeightedLatLng);
    if ((this.data.size() > 50) && (this.b < 40)) {
      a();
    }
  }
  
  private void a()
  {
    this.d = new ArrayList(4);
    this.d.add(new c(this.bounds.left, this.bounds.centerY, this.bounds.top, this.bounds.centerX, this.b + 1));
    this.d.add(new c(this.bounds.centerY, this.bounds.right, this.bounds.top, this.bounds.centerX, this.b + 1));
    this.d.add(new c(this.bounds.left, this.bounds.centerY, this.bounds.centerX, this.bounds.bottom, this.b + 1));
    this.d.add(new c(this.bounds.centerY, this.bounds.right, this.bounds.centerX, this.bounds.bottom, this.b + 1));
    
    List<WeightedLatLng> localList = this.data;
    this.data = null;
    for (WeightedLatLng localWeightedLatLng : localList) {
      a(localWeightedLatLng.getPoint().x, localWeightedLatLng.getPoint().y, localWeightedLatLng);
    }
  }
  
  protected Collection<WeightedLatLng> a(Bounds paramay)
  {
    ArrayList localArrayList = new ArrayList();
    a(paramay, localArrayList);
    return localArrayList;
  }
  
  private void a(Bounds paramay, Collection<WeightedLatLng> paramCollection)
  {
    if (!this.bounds.contains(paramay)) {
      return;
    }
    c localObject;
    if (this.d != null) {
      for (Iterator<c> localIterator = this.d.iterator(); localIterator.hasNext();)
      {
        localObject = (c)localIterator.next();
        ((c)localObject).a(paramay, paramCollection);
      }
    } else if (this.data != null) {
      if (paramay.include(this.bounds)) {
        paramCollection.addAll(this.data);
      } else {
        for (Iterator<WeightedLatLng> localIterator = this.data.iterator(); localIterator.hasNext();)
        {
          WeightedLatLng localObject2 = (WeightedLatLng) localIterator.next();
          if (paramay.covers(((WeightedLatLng) localObject2).getPoint())) {
            paramCollection.add(localObject2);
          }
        }
      }
    }
  }
}
