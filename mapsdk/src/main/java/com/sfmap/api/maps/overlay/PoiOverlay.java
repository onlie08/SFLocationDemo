package com.sfmap.api.maps.overlay;

import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.CameraUpdateFactory;
import com.sfmap.api.maps.model.BitmapDescriptor;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.maps.model.LatLngBounds;
import com.sfmap.api.maps.model.Marker;
import com.sfmap.api.maps.model.MarkerOptions;
import com.sfmap.api.services.poisearch.PoiItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Poi图层类。在地图API里，如果要显示Poi，可以用此类来创建Poi图层。如不满足需求，也可以自己创建自定义的Poi图层。
 */
public class PoiOverlay
{
  private List<PoiItem> poiItemList;
  private MapController map;
  private ArrayList<Marker> markers = new ArrayList<Marker>();

    /**
     * 通过此构造函数创建Poi图层。
     * @param paramMap - 地图对象。
     * @param pois - 要在地图上添加的poi。列表中的poi对象详见搜索服务模块的基础核心包（com.sfmap.api.services.core）中的类 {@link PoiItem}。
     */
  public PoiOverlay(MapController paramMap, List<PoiItem> pois)
  {
    this.map = paramMap;
    this.poiItemList = pois;
  }

    /**
     * 添加Marker到地图中。
     */
  public void addToMap()
  {
    try
    {
      for (int i = 0; i < this.poiItemList.size(); i++)
      {
        Marker localMarker = this.map.addMarker(a(i));
        localMarker.setObject(Integer.valueOf(i));
        this.markers.add(localMarker);
      }
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
  }

    /**
     * 去掉PoiOverlay上所有的Marker。
     */
  public void removeFromMap()
  {
    for (Marker localMarker : this.markers) {
      localMarker.remove();
    }
  }

    /**
     * 移动镜头到当前的视角。
     */
  public void zoomToSpan()
  {
    try
    {
      if ((this.poiItemList != null) && (this.poiItemList.size() > 0))
      {
        if (this.map == null) {
          return;
        }
        if (this.poiItemList.size() == 1)
        {
          this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(((PoiItem)this.poiItemList.get(0)).getLatLonPoint().getLatitude(),
            ((PoiItem)this.poiItemList.get(0)).getLatLonPoint().getLongitude()), 18.0F));
        }
        else
        {
          LatLngBounds localLatLngBounds = a();
          this.map.moveCamera(CameraUpdateFactory.newLatLngBounds(localLatLngBounds, 5));
        }
      }
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
  }
  
  private LatLngBounds a()
  {
    LatLngBounds.Builder localBuilder = LatLngBounds.builder();
    for (int i = 0; i < this.poiItemList.size(); i++) {
      localBuilder.include(new LatLng(((PoiItem)this.poiItemList.get(i)).getLatLonPoint().getLatitude(),
        ((PoiItem)this.poiItemList.get(i)).getLatLonPoint().getLongitude()));
    }
    return localBuilder.build();
  }
  
  private MarkerOptions a(int index)
  {
    return new MarkerOptions().position(new LatLng(((PoiItem)this.poiItemList.get(index)).getLatLonPoint().getLatitude(), ((PoiItem)this.poiItemList.get(index)).getLatLonPoint().getLongitude())).title(getTitle(index)).snippet(getSnippet(index)).icon(getBitmapDescriptor(index));
  }

    /**
     * 给第几个Marker设置图标，并返回更换图标的图片。如不用默认图片，需要重写此方法。
     * @param index- 第几个Marker。
     * @return 更换的Marker图片。
     */
  protected BitmapDescriptor getBitmapDescriptor(int index)
  {
    return null;
  }

    /**
     * 返回第index的Marker的标题。
     * @param index - 第几个Marker。
     * @return marker的标题。
     */
  protected String getTitle(int index)
  {
    return ((PoiItem)this.poiItemList.get(index)).getTitle();
  }

    /**
     * 返回第index的Marker的详情。
     * @param index  - 第几个Marker。
     * @return marker的详情。
     */
  protected String getSnippet(int index)
  {
    return ((PoiItem)this.poiItemList.get(index)).getSnippet();
  }

    /**
     * 从marker中得到poi在list的位置。
     * @param marker  - 一个标记的对象。
     * @return poi索引。
     */
  public int getPoiIndex(Marker marker)
  {
    for (int i = 0; i < this.markers.size(); i++) {
      if (((Marker)this.markers.get(i)).equals(marker)) {
        return i;
      }
    }
    return -1;
  }

    /**
     * 返回第index的poi的信息。
     * @param index - 第几个poi。
     * @return poi的信息。poi对象详见搜索服务模块的基础核心包（com.sfmap.api.services.core）中的类 {@link PoiItem}。
     */
  public PoiItem getPoiItem(int index)
  {
    if ((index < 0) || (index >= this.poiItemList.size())) {
      return null;
    }
    return (PoiItem)this.poiItemList.get(index);
  }
}
