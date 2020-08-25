package com.sfmap.api.maps;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Location;
import android.os.RemoteException;
import android.view.MotionEvent;
import android.view.View;

import com.sfmap.api.mapcore.IMapDelegate;
import com.sfmap.api.mapcore.util.MapThrowException;
import com.sfmap.api.maps.model.Arc;
import com.sfmap.api.maps.model.ArcOptions;
import com.sfmap.api.maps.model.CameraPosition;
import com.sfmap.api.maps.model.Circle;
import com.sfmap.api.maps.model.CircleOptions;
import com.sfmap.api.maps.model.GroundOverlay;
import com.sfmap.api.maps.model.GroundOverlayOptions;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.maps.model.Marker;
import com.sfmap.api.maps.model.MarkerOptions;
import com.sfmap.api.maps.model.MyLocationStyle;
import com.sfmap.api.maps.model.MyTrafficStyle;
import com.sfmap.api.maps.model.NavigateArrow;
import com.sfmap.api.maps.model.NavigateArrowOptions;
import com.sfmap.api.maps.model.Poi;
import com.sfmap.api.maps.model.Polygon;
import com.sfmap.api.maps.model.PolygonOptions;
import com.sfmap.api.maps.model.Polyline;
import com.sfmap.api.maps.model.PolylineOptions;
import com.sfmap.api.maps.model.RuntimeRemoteException;
import com.sfmap.api.maps.model.Text;
import com.sfmap.api.maps.model.TextOptions;
import com.sfmap.api.maps.model.TileOverlay;
import com.sfmap.api.maps.model.TileOverlayOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * 定义MapController 地图对象的操作方法与接口。
 */
public final class MapController {
    /**
     * 道路底图。
     */
    public static final int MAP_TYPE_NORMAL = 1;
    /**
     * 卫星底图。
     */
    public static final int MAP_TYPE_SATELLITE = 2;
    /**
     * 黑夜地图。
     */
    public static final int MAP_TYPE_NIGHT = 3;
    /**
     * 导航模式。
     */
    public static final int MAP_TYPE_NAVI = 4;
    /**
     * 街景路网。
     */
    public static final int MAP_TYPE_STREETVIEW = 5;
    /**
     * 只在第一次定位移动到地图中心点。
     */
    public static final int LOCATION_TYPE_LOCATE = 1;
    /**
     * 定位、移动到地图中心点并跟随。
     */
    public static final int LOCATION_TYPE_MAP_FOLLOW = 2;
    /**
     * 定位、移动到地图中心点，跟踪并根据方向旋转地图。
     */
    public static final int LOCATION_TYPE_MAP_ROTATE = 3;

    private final IMapDelegate map;
    /**
     * 地图的用户界面设置。
     */
    private UiSettings uiSetting;
    /**
     * 当前可视区域的坐标。
     */
    private Projection projection;
    /**
     * 路况拥堵情况对应的颜色。
     */
    private MyTrafficStyle trafficStyle;

    /**
     * 构造方法。
     *
     * @param map 地图实例。
     */
    protected MapController(IMapDelegate map) {
        this.map = map;
    }


    /**
     * 返回可视区域的当前位置。在可视区域变换时，此返回会自己更新。
     *
     * @return 返回可视区域的当前位置的CameraPosition 对象。
     */
    public final CameraPosition getCameraPosition() {
        try {
            return this.map.getCameraPosition();
        } catch (RemoteException localRemoteException) {
            throw new RuntimeRemoteException(localRemoteException);
        }
    }

    /**
     *
     * @param isNeedToCenter 是否设置当前位置为地图中心点
     */
    public void setNeedToCenter(boolean isNeedToCenter) {
        map.setNeedToCenter(isNeedToCenter);
    }

    /**
     * 设置自定义地图样式文件
     *
     * @param stylePath 样式文件的绝对路径
     * @param iconPath  图标文件的绝对路径
     */
    public void setMapStyleFileByPath(String stylePath, String iconPath) {
        this.map.setMapStyleIconForPath(stylePath, iconPath);
    }

    /**
     * 设置自定义地图样式
     *
     * @param mapStyleType 0:默认样式，1:定制样式
     */
    public void setMapStyleType(int mapStyleType) {
        this.map.setMapStyleType(mapStyleType);
    }

    /**
     * 设置地图最大级别，但不能小于最小级别，必须小于系统支持的最大级别22，默认为19级。
     *
     * @param maxZoomLevel 地图最大级别
     */
    public final void setMaxZoomLevel(float maxZoomLevel) {
        this.map.setMaxZoomLevel(maxZoomLevel);
    }

    /**
     * 设置地图最小级别，但不能小于0级别，默认为0级。
     *
     * @param minZoomLevel 地图最小级别
     */
    public final void setMinZoomLevel(float minZoomLevel) {
        this.map.setMinZoomLevel(minZoomLevel);
    }

    /**
     * 设置是否启动2D地图手势旋转功能
     *
     * @param isEnable true 开启旋转,false 关闭旋转
     */
    public final void set2DMapRotateEnable(boolean isEnable) {
        this.map.set2DMapRotateEnable(isEnable);
    }

    /**
     * 返回当前可视区域的最大缩放级别。
     *
     * @return 当前可视区域的最大缩放级别。
     */
    public final float getMaxZoomLevel() {
        return this.map.getMaxZoomLevel();
    }

    /**
     * 返回当前可视区域的最小缩放级别。可能在不同设备上返回有区别。
     *
     * @return 当前可视区域的最小缩放级别。
     */
    public final float getMinZoomLevel() {
        return this.map.getMinZoomLevel();
    }

    /**
     * 按照传入的CameraUpdate 参数移动可视区域。这个方法为瞬间移动，没有移动过程，如果在调用此方法后再调用getCameraPosition()将返回移动后位置。
     *
     * @param update 定义转换的目的地位置。
     */
    public final void moveCamera(CameraUpdate update) {
        try {
            this.map.moveCamera(update
                    .getCameraUpdateFactory());
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 可视区域动画是指从当前可视区域转换到一个指定位置的可视区域的过程。 在运动的时候，调用getCameraPosition()返回可视区域当前位置。
     *
     * @param update 定义转换的目的地位置。
     */
    public final void animateCamera(CameraUpdate update) {
        try {
            this.map.animateCamera(update
                    .getCameraUpdateFactory());
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 可视区域动画是指从当前可视区域转换到一个指定位置的可视区域的过程，在转换完成后， 将回调转入的LMap.CancelableCallback接口。 可以参考CameraUpdateFactory来设置LMap.CancelableCallback对象。
     *
     * @param update   定义转换的目的地位置。
     * @param callback 这个类是在动画停止时从主线程回调。 如果动画正常完成，则会调用onFinish()方法，否则调用onCancel()。 调用onCancel()时，不要变换或运动可视区域。
     */
    public final void animateCamera(CameraUpdate update, CancelableCallback callback) {
        try {
            this.map.animateCamera(update.getCameraUpdateFactory(), callback);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 在指定的持续时间内，动画地移动地图到指定的位置，完成时调用可选的回调方法。 如果运动过程中调用getCameraPosition()，它将返回可视区域飞行中的当前位置。
     *
     * @param update             定义转换的目的地位置。
     * @param durationMs         运动的持续时间，单位毫秒。必须是正数，否则将会抛出IllegalArgumentException异常。
     * @param cancelableCallback 这个类是在动画停止时从主线程回调。如果动画正常完成，则会调用onFinish()方法； 如果因为随后的可视区域移动或用户手势而动画停止，调用onCancel()。 取消的方法，这个自定义的类不会尝试移动或动画地移动可视区域。
     */
    public final void animateCamera(CameraUpdate update, long durationMs, CancelableCallback cancelableCallback) {
        try {
            MapThrowException.bThrowIllegalArgumentException(durationMs > 0L, "durationMs must be positive");

            this.map.animateCameraWithDurationAndCallback(update.getCameraUpdateFactory(), durationMs, cancelableCallback);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 停止动画效果。当方法被调用，可视区域立即停止运动并停留在此位置。
     */
    public final void stopAnimation() {
        try {
            this.map.stopAnimation();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 加一个箭头对象（NavigateArrow）对象在地图上。
     *
     * @param options 一个NavigateArrowOptions 对象，它定义了如何渲染NaviagetArrow 的属性。
     * @return 返回一个 NavigateArrow 对象，此对象已经加到地图上。
     */
    public final NavigateArrow addNavigateArrow(NavigateArrowOptions options) {
        try {
            return new NavigateArrow(this.map.addNavigateArrow(options));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 加一个多段线对象（Polyline）对象在地图上。
     *
     * @param options 一个PolylineOptions 对象，它定义了如何渲染Polyline 的属性。
     * @return 返回一个 Polyline 对象，此对象已经加到地图上。
     */
    public final Polyline addPolyline(PolylineOptions options) {
        try {
            return new Polyline(this.map.addPolyline(options));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 添加圆形（circle）覆盖物到地图上。
     *
     * @param options 设置圆形初始化属性的CircleOptions对象。
     * @return 一个Circle对象。
     */
    public final Circle addCircle(CircleOptions options) {
        try {
            return new Circle(this.map.addCircle(options));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 添加弧线（arc）覆盖物到地图上。
     *
     * @param options 设置弧线初始化属性的ArcOptions对象。
     * @return 一个Arc对象。
     */
    public final Arc addArc(ArcOptions options) {
        try {
            return new Arc(this.map.addArc(options));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 在地图上添加一个多边形（polygon）对象。
     *
     * @param options 一个PolygonOptions 对象，它定义了如何渲染Polygon 的属性。
     * @return 返回一个 Polygon 对象，此对象已经加到地图上。
     */
    public final Polygon addPolygon(PolygonOptions options) {
        try {
            return new Polygon(this.map.addPolygon(options));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 在地图上添加一个GroundOverlay对象。
     *
     * @param options 一个GroundOverlayOptions 对象，它定义了GroundOverlay 的属性。
     * @return 返回一个 GroundOverlay 对象，此对象已经加到地图上。
     */
    public final GroundOverlay addGroundOverlay(GroundOverlayOptions options) {
        try {
            return new GroundOverlay(this.map.addGroundOverlay(options));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final boolean isPOIByPoint(Point p) {
        return map.isPOIByPoint(p);
    }

    /**
     * 加一个Marker（标记）到地图上。
     * Marker 的图标会根据Marker.position 位置渲染在地图上。点击这个Marker，可视区域将以这个Marker 的位置为中心点。如果Marker 设置了title，则地图上会显示一个包括title 文字的信息框。如果Marker 被设置为可拖拽的，那么长按此Marker 可以拖动它。
     *
     * @param options 一个MarkerOptions 对象，它定义了如何渲染Marker 的属性。
     * @return 返回一个 Marker 对象，此对象已经加到地图上。
     */
    public final Marker addMarker(MarkerOptions options) {
        try {
            return this.map.addMarker(options);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 加一个文字（Text）到地图上。
     *
     * @param options options - TextOptions 对象，它定义了如何渲染Text 的属性。
     * @return 返回一个 文字（Text） 对象。
     */
    public final Text addText(TextOptions options) {
        try {
            return this.map.addText(options);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 加多个Marker（标记）到地图上，并设置是否移动到屏幕中间。如果只有一个标记，移动此标记到地图中心，并设置地图级别为18级；如果为多个标记，根据标记形成的矩形区域进行缩放和显示到正常区域。
     *
     * @param options      多个MarkerOptions对象，它定义了如何渲染Marker的属性。
     * @param moveToCenter 是否移动到屏幕中心，默认为false，不移动到屏幕中心。
     * @return 返回多个Marker对象，此对象已经加到地图上。
     */
    public final ArrayList<Marker> addMarkers(ArrayList<MarkerOptions> options, boolean moveToCenter) {
        try {
            return this.map.addMarkers(options, moveToCenter);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 获取当前可见地图区域的所有marker。
     *
     * @return 可见地图区域的所有marker。
     */
    public final List<Marker> getMapScreenMarkers() {
        try {
            return this.map.getMapScreenMarkers();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 添加一个TileOverlay到地图上。
     *
     * @param options TileOverlayOptions 对象，它定义了如何渲染TileOverlay 的属性。
     * @return 一个TileOverlay对象。
     */
    public final TileOverlay addTileOverlay(TileOverlayOptions options) {
        try {
            return this.map.addTileOverlay(options);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 从地图上删除所有的Marker，Overlay，Polyline 等覆盖物。
     */
    public final void clear() {
        try {
            this.map.clear();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 从地图上删除所有的Marker，Overlay，Polyline 等覆盖物(保留MyLocationOverlay)。
     *
     * @param isKeepMyLocationOverlay true 表示保留，false 表示不保留。
     */
    public final void clear(boolean isKeepMyLocationOverlay) {
        try {
            this.map.clear(isKeepMyLocationOverlay);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 回当前的地图显示类型。可以参考MAP_TYPE_NORMAL, MAP_TYPE_SATELLITE, MAP_TYPE_STREETVIEW。
     *
     * @return 地图显示类型。
     */
    public final int getMapType() {
        try {
            return this.map.getMapType();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 设置地图显示的类型(建议在onMapLoaded之后调用)。
     *
     * @param type 地图类型： MAP_TYPE_NORMAL：普通地图，值为1；MAP_TYPE_SATELLITE：卫星地图，值为2； MAP_TYPE_NIGHT：黑夜地图，夜间模式，值为3； MAP_TYPE_NAVI：导航模式，值为4;MAP_TYPE_STREETVIEW：实景路网，值为5。
     */
    public final void setMapType(int type) {
        try {
            this.map.setMapType(type);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 返回当前地图是否有实时交通数据。
     *
     * @return 如果返回true 则说明当前地图有实时交通数据，否则返回false。
     */
    public final boolean isTrafficEnabled() {
        try {
            return this.map.isTrafficEnabled();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 返回当前地图是否有单元区域数据。
     *
     * @return 如果返回true 则说明当前地图有单元区域数据，否则返回false。
     */
    public final boolean isCellZonesEnabled() {
        try {
            return this.map.isCellZonesEnabled();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 设置是否显示实时交通。
     *
     * @param enabled true 表示显示，false 表示不显示。
     */
    public void setTrafficEnabled(boolean enabled) {
        try {
            this.map.setTrafficEnabled(enabled);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 设置是否显示底图文字标注，默认显示(需要在onMapLoaded之后调用)。
     *
     * @param enabled true： 表示显示，为默认值； false： 不显示。
     */
    public void showMapText(boolean enabled) {
        try {
            this.map.showMapText(enabled);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }


    /**
     * 自定义路况拥堵情况对应的颜色属性。
     *
     * @param style MyTrafficStyle 包含各种拥堵情况对应的颜色,详细情况请参考MyTrafficStyle。
     */
    public void setMyTrafficStyle(MyTrafficStyle style) {
        try {
            this.trafficStyle = style;
            this.map.setMyTrafficStyle(style.getSmoothColor(), style
                    .getSlowColor(), style.getCongestedColor(), style
                    .getSeriousCongestedColor());
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 获取 路况拥堵情况对应的颜色属性。
     *
     * @return 包含各种拥堵情况对应的颜色。
     */
    public MyTrafficStyle getMyTrafficStyle() {
        return this.trafficStyle;
    }

    /**
     * 返回my-location 层的显示状态。
     *
     * @return 如果返回true 则说明当前位置层可显示，否则返回false。
     */
    public final boolean isMyLocationEnabled() {
        try {
            return this.map.isMyLocationEnabled();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 设置定位层是否显示。
     * 如果显示定位层，则界面上将出现定位按钮，如果未设置Location Source 则定位按钮不可点击。
     *
     * @param enabled true 显示定位层, false 隐藏定位层。
     */
    public final void setMyLocationEnabled(boolean enabled) {
        try {
            this.map.setMyLocationEnabled(enabled);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 返回当前Location Source 提供的定位信息。如果未设置Location Source 则返回null。
     *
     * @return 当前显示的定位位置的经纬度。
     */
    public final Location getMyLocation() {
        try {
            return this.map.getMyLocation();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 设置定位资源。如果不设置此定位资源则定位按钮不可点击。
     *
     * @param locationSource locationSource - 与地图匹配的定位资源。
     */
    public final void setLocationSource(LocationSource locationSource) {
        try {
            this.map.setLocationSource(locationSource);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 设置当前位置绘制的样式，即小蓝点的样式。
     *
     * @param style MyLocationStyle 当前位置的绘制属性。
     */
    public final void setMyLocationStyle(MyLocationStyle style) {
        try {
            this.map.setMyLocationStyle(style);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 设置当前位置跟随模式。
     *
     * @param type style 总共有三种模式，定位，跟随和旋转。
     */
    public final void setMyLocationType(int type) {
        try {
            this.map.setMyLocationType(type);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 立体展示地图，但不能旋转
     */
    public final void setCustomType() {
        try {
            this.map.setCustomType();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 解除旋转监听
     */
    public final void unregisterSensorListener() {
        try {
            this.map.unregisterSensorListener();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 设置当前位置绘制的点旋转角度。
     *
     * @param rotate 旋转角度在0~360之间。(逆时针方向)）
     */
    public final void setMyLocationRotateAngle(float rotate) {
        try {
            this.map.setRotateAngle(rotate);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 返回地图的用户界面设置对象。可以对地图控件的显示以及一些手势操作的的控制。
     *
     * @return 地图界面设置对象。
     */
    public final UiSettings getUiSettings() {
        try {
            if (this.uiSetting == null) {
                this.uiSetting = new UiSettings(this.map.getUiSettings());
            }
            return this.uiSetting;
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 返回一个Projection 对象。可以通过这个对象在屏幕坐标与经纬度坐标之间进行转换。Projection 对象返回的是当前可视区域的坐标，当可视区域变换时，它不会自己更新。
     *
     * @return 当前地图位置的Projection 对象。
     */
    public final Projection getProjection() {
        try {
            if (this.projection == null) {
                this.projection = new Projection(this.map.getProjection());
            }
            return this.projection;
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 设置一个可视范围变化时的回调的接口方法。
     *
     * @param listener 当可视范围变化时的回调的接口方法。传入null值，则表示不回调方法。
     */
    public final void setOnCameraChangeListener(OnCameraChangeListener listener) {
        try {
            this.map.setOnCameraChangeListener(listener);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 设置一个地图级别变化时的回调的接口方法。
     *
     * @param listener 当地图级别变化时的回调的接口方法。传入null值，则表示不回调方法。
     */
    public final void setOnMapLevelChangeListener(OnMapLevelChangeListener listener) {
        try {
            this.map.setOnMapLevelChangeListener(listener);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 设置一个当地图被点击时的回调方法。
     *
     * @param listener 当地图被点击时的接口回调方法，传入null 值，则表示不回调方法。
     */
    public final void setOnMapClickListener(OnMapClickListener listener) {
        try {
            this.map.setOnMapClickListener(listener);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 设置一个当地图被触摸时的回调方法。
     *
     * @param listener 当地图被触摸时的接口回调方法，传入null 值，则表示不回调方法。
     */
    public final void setOnMapTouchListener(OnMapTouchListener listener) {
        try {
            this.map.setOnMapTouchListener(listener);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 设置底图poi选中回调监听。
     *
     * @param listener 当底图上的POI被点击时的接口回调方法，传入null 值，则表示不回调方法。
     */
    public final void setOnPOIClickListener(OnPOIClickListener listener) {
        try {
            this.map.setOnPOIClickListener(listener);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 设置当用户位置改变后的回调方法。
     *
     * @param listener 当用户位置改变后的回调方法。
     */
    public final void setOnMyLocationChangeListener(OnMyLocationChangeListener listener) {
        try {
            this.map.setOnMyLocationChangeListener(listener);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 设置一个当长按地图时的回调方法。
     *
     * @param listener 当长按地图时回调接口方法，传入null 值，则表示不回调方法。
     */
    public final void setOnMapLongClickListener(OnMapLongClickListener listener) {
        try {
            this.map.setOnMapLongClickListener(listener);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 设置一个当地图上的Marker 被点击的回调方法。
     *
     * @param listener 当地图上的Marker 被点击时，回调此方法，传入null 值，则表示不回调方法。
     */
    public final void setOnMarkerClickListener(OnMarkerClickListener listener) {
        try {
            this.map.setOnMarkerClickListener(listener);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 设置一个当地图上的折线段Polyline 被点击的回调方法。
     *
     * @param listener 当地图上的Polyline 被点击时，回调此方法，传入null 值，则表示不回调方法。
     */
    public final void setOnPolylineClickListener(OnPolylineClickListener listener) {
        try {
            this.map.setOnPolylineClickListener(listener);
        } catch (RemoteException exception) {
            throw new RuntimeRemoteException(exception);
        }
    }

    /**
     * 设置一个当地图上的Marker被拖拽的回调方法。
     *
     * @param listener 当地图上的Marker被拖拽时，回调此方法；传入null值，则表示不回调方法。
     */
    public final void setOnMarkerDragListener(OnMarkerDragListener listener) {
        try {
            this.map.setOnMarkerDragListener(listener);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 设置当消息窗口Marker 被点击时的回调方法。
     *
     * @param listener 当信息窗口Marker 被点击时的回调方法，传入null 值，则表示不回调方法。
     */
    public final void setOnInfoWindowClickListener(OnInfoWindowClickListener listener) {
        try {
            this.map.setOnInfoWindowClickListener(listener);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 设置一个个性化渲染的内容的信息窗口。
     * 像地图事件接口一样，这个状态不是绑定在地图上的。如果地图被再次创建，比如当一些配制更改时，你必须确认要再次调用这个方法以保证个性化的信息窗口不变。
     *
     * @param adapter 这个适配器用来渲染信息窗口的内容，如果传入null，则API 会用默认方式来渲染信息窗口。
     */
    public final void setInfoWindowAdapter(InfoWindowAdapter adapter) {
        try {
            this.map.setInfoWindowAdapter(adapter);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 设置地图加载完成回调。
     *
     * @param listener 当地图加载成功后回调的接口方法。
     */
    public final void setOnMapLoadedListener(OnMapLoadedListener listener) {
        try {
            this.map.setOnMapLoadedListener(listener);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

//    /**
//     * 截取当前屏幕设备上的可见地图。
//     *
//     * @param listener 当截屏时回调的接口方法。
//     * @deprecated
//     */
//    public void getMapPrintScreen(onMapPrintScreenListener listener) {
//        this.map.getMapPrintScreen(listener);
//    }

    /**
     * 截取当前屏幕设备上的可见地图区域。
     *
     * @param listener 当截屏时回调的接口。
     */
    public void getMapScreenShot(OnMapScreenShotListener listener) {
        this.map.a(listener);
    }

    /**
     * 获取比例尺数据。当前缩放级别下，地图上1像素点对应的长度，单位米。
     *
     * @return 当前缩放级别下，地图上1像素点对应的长度，单位米。
     */
    public float getScalePerPixel() {
        try {
            return this.map.getScalePerPixel();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 如果发现抖动现象 可以调用该属性。
     */
    public void runOnDrawFrame() {
        this.map.setRunLowFrame(false);
    }

    /**
     * 删除地图存储目录下的手机地图缓存, 没有回调。
     */
    public void removecache() {
        try {
            this.map.removecache();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 删除地图存储目录下的手机地图缓存。
     *
     * @param listener - 监听删除地图缓存文件的接口, 传入null，则没有回调。
     */
    public void removecache(OnCacheRemoveListener listener) {
        try {
            this.map.removecache(listener);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 设置获取openGL的回调方法。
     *
     * @param listener - OpenGL渲染回调接口。
     */
    public void setCustomRenderer(CustomRenderer listener) {
        try {
            this.map.setCustomRenderer(listener);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 添加获取openGL的回调方法。
     *
     * @param listener - OpenGL渲染回调接口。
     */
    public void addCustomRenderer(CustomRenderer listener) {
        try {
            if (listener != null)
                this.map.addCustomRenderer(listener);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 删除openGL的回调方法。
     *
     * @param listener 监听器实例。
     */
    public void removeCustomRenderer(CustomRenderer listener) {
        try {
            if (listener != null)
                this.map.removeCustomRenderer(listener);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 去除所有CustomRenderer图层。
     */
    public void clearCustomRenderer() {
        try {
            this.map.clearCustomRenderer();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 设置自绘制图层渲染顺序，drawTime为3时，在绘制标注之前绘制自绘制图层；drawTime为4时，在绘制标注之后绘制自绘制图层。
     *
     * @param drawTime 绘制时间，3表示标注之前绘制；4表示标注之后绘制。
     */
    public void drawCustomRenderTime(int drawTime) {
        this.map.drawCustomRenderTime(drawTime);
    }

    /**
     * 设置屏幕上的某个点为地图旋转中心点
     *
     * @param x - 屏幕上设置为地图中心点的 x 像素坐标，x 的范围为 0 <= x <= 手机屏幕的像素宽度。
     * @param y - 屏幕上设置为地图中心点的 y 像素坐标，y 的范围为 0 <= y <= 手机屏幕的像素高度。
     */
    public void setPointToCenter(int x, int y) {
        try {
            this.map.setPointToCenter(x, y);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 无动画设置地图中心点。
     *
     * @param latLng 中心点坐标
     */
    public void setMapCenter(LatLng latLng) {
        try {
            this.map.setMapCenter(latLng);
        } catch (Exception e) {

        }
    }

    /**
     * 设置地图底图文字的z轴指数。
     *
     * @param x - 为地图底图文字的显示顺序，默认为0。
     */
    public final void setMapTextZIndex(int x) {
        try {
            this.map.setMapTextZIndex(x);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 重新加载地图数据。
     * 下载完离线数据之后如果要直接显示，可以调用此接口先关闭再打开，从而达到重新加载地图数据的效果。
     *
     * @param enabled - true 打开地图数据库；false 关闭地图数据库。
     */
    public final void setLoadOfflineData(boolean enabled) {
        try {
            this.map.setLoadOfflineData(enabled);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 得到地图底图文字的z轴指数。
     *
     * @return 地图底图文字的z轴指数，默认为0。
     */
    public final int getMapTextZIndex() {
        try {
            return this.map.getMapTextZIndex();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 设置指北针,比例尺,logo相对屏幕的位置,注:在地图控件初始化完成后调用
     *
     * @param viewType 类型:MapOption.POSITION_COMPASS,MapOption.POSITION_SCALE,MapOption.POSITION_LOGO
     * @param xPix     相对地图左侧的绝对x坐标
     * @param yPix     相对地图顶部的绝对y坐标
     */
    public void setViewPosition(int viewType, int xPix, int yPix) {
        try {
            if (viewType == MapOptions.POSITION_COMPASS) {
                map.setCompassViewPosition(xPix, yPix);
            } else if (viewType == MapOptions.POSITION_SCALE) {
                map.setScaleViewPosition(xPix, yPix);
            } else if (viewType == MapOptions.POSITION_LOGO) {
                map.setLogoViewPosition(xPix, yPix);
            }
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 设置指北针,定位控件的图标
     *
     * @param viewType 1,指北针;2我的位置按钮,控件图标需要2个,分别为:正常,按下;
     * @param bitmaps  图标
     */
    public void setViewBitmap(int viewType, Bitmap[] bitmaps) {
        try {
            if (bitmaps == null || bitmaps.length == 0) return;
            if (viewType == 1) {
                this.map.setCompassViewBitmap(bitmaps[0]);
            } else if (viewType == 2) {
                this.map.setLocationViewBitmap(bitmaps);
            } else if (viewType == 0) {
                this.map.setLogoBitmap(bitmaps[0]);
            }
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 重新加载地图引擎，即调用此接口时会重新加载底图数据，覆盖物比如marker不会消失。
     */
    public void reloadMap() {
        this.map.reloadMap();
    }

    /**
     * 设置获取用户栅格服务URL的监听器。
     *
     * @param listener 事件监听器实例。
     */
    public void setGridUrlListener(GridUrlListener listener) {
        this.map.setGridUrlListener(listener);
    }

    public void setParameter(String method, boolean paramBoolean) {
        try {
            this.map.setParameter(method, paramBoolean);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    /**
     * 截屏时回调的接口 。
     */
    public static abstract interface OnMapScreenShotListener {
        /**
         * 截屏时回调的方法。
         *
         * @param bitmap 调用截屏接口返回的截屏对象。
         */
        public abstract void onMapScreenShot(Bitmap bitmap);

        /**
         * 带有地图渲染状态的截屏回调方法
         * 根据返回的状态码,可以判断当前视图渲染是否完成
         *
         * @param bitmap 调用截屏接口返回的截屏对象
         * @param status 地图渲染状态, 1:地图渲染完成,0:未绘制完成
         */
        public abstract void onMapScreenShot(Bitmap bitmap, int status);
    }

//    /**
//     * @deprecated 地图截屏回调的方法。
//     */
//    public static abstract interface onMapPrintScreenListener {
//        /**
//         * 截屏时回调的方法。
//         *
//         * @param drawable 调用截屏接口返回的截屏对象。
//         * @deprecated
//         */
//        public abstract void onMapPrint(Drawable drawable);
//    }

    /**
     * 缓存数据清除监听。
     */
    public static abstract interface OnCacheRemoveListener {
        /**
         * 删除缓存回调。
         *
         * @param isCacheRemove 是否成功删除。
         */
        public abstract void onRemoveCacheFinish(boolean isCacheRemove);
    }

    /**
     * 地图底图poi选中回调 此接口的调用是在主线程中。
     */
    public static abstract interface OnPOIClickListener {
        /**
         * 当用户点击底图上的POI时回调此方法。
         *
         * @param poi 点击的poi对象 。
         */
        public abstract void onPOIClick(Poi poi);
    }

    /**
     * 当地图加载完成后回调。
     */
    public static abstract interface OnMapLoadedListener {
        /**
         * 当地图加载完成后回调此方法。
         */
        public abstract void onMapLoaded();
    }

    /**
     * 当用户触摸地图时回调的接口。此接口的调用是在主线程中。
     */
    public static abstract interface OnMapTouchListener {
        /**
         * 当用户触摸地图时回调此方法。
         *
         * @param event 系统自带的移动事件。
         */
        public abstract void onTouch(MotionEvent event);
    }

    /**
     * 当用户点击地图时回调的接口。此接口的调用是在主线程中。
     */
    public static abstract interface OnMapClickListener {
        /**
         * 当用户点击地图时回调此方法，如果点击在某overlays 上，overlays 相应了点击事件，则不会触发此方法。如果overlays 没有相应点击事件，则会透传给此方法。此方法的调用是在主线程中。
         *
         * @param point 用户所点击的地理坐标。
         */
        public abstract void onMapClick(LatLng point);
    }

    /**
     * 当用户长按地图时回调的接口。此接口的调用是在主线程中。
     */
    public static abstract interface OnMapLongClickListener {
        /**
         * 当用户长按地图时回调此方法，如果长按在某overlays 上，overlays 相应了点击事件，则不会触发此方法。如果overlays 没有相应点击事件，则会透传给此方法。此方法的调用是在主线程中。
         *
         * @param point 用户所点击的地理坐标。
         */
        public abstract void onMapLongClick(LatLng point);
    }

    /**
     * 定义了当可视范围改变时回调的接口。 使用此接口时，需要实现接口内的每个方法。
     */
    public static abstract interface OnCameraChangeListener {
        /**
         * 在可视范围改变完成之后回调此方法。此方法的调用是在主线程中。
         *
         * @param position 可视范围最终改变的CameraPosition 对象。
         */
        public abstract void onCameraChange(CameraPosition position);

        /**
         * 在可视范围一系列动作改变完成之后（例如拖动、fling、缩放）回调此方法。此方法的调用是在主线程中。
         *
         * @param position 可视范围最终改变的CameraPosition 对象。
         */
        public abstract void onCameraChangeFinish(CameraPosition position);
    }

    /**
     * 定义了当marker 对象被点击时回调的接口。
     */
    public static abstract interface OnMarkerClickListener {
        /**
         * 当一个marker 对象被点击时调用此方法。
         *
         * @param marker - 被点击的marker 对象。
         * @return true 则表示接口已相应事件，否则返回false。
         */
        public abstract boolean onMarkerClick(Marker marker);
    }

    /**
     * 定义了当Polyline 对象被点击时回调的接口。
     */
    public static abstract interface OnPolylineClickListener {
        /**
         * 当一个Polyline 对象被点击时调用此方法。
         *
         * @param polyline 被点击的polyline 对象。
         */
        public abstract void onPolylineClick(Polyline polyline);
    }

    /**
     * marker拖动时的回调接口。
     */
    public static abstract interface OnMarkerDragListener {
        /**
         * 当marker开始被拖动时回调此方法。这个marker的位置可以通过getPosition()方法返回。 这个位置可能与拖动的之前的marker位置不一样。
         *
         * @param marker 被拖动的marker对象。
         */
        public abstract void onMarkerDragStart(Marker marker);

        /**
         * 在marker拖动过程当中回调此方法。这个marker的位置可以通过getPosition()方法返回。
         *
         * @param marker 被拖动的marker对象。
         */
        public abstract void onMarkerDrag(Marker marker);

        /**
         * 在marker拖动完成后回调此方法。这个marker的位置可以通过getPosition()方法返回。
         *
         * @param marker 被拖动的marker对象。
         */
        public abstract void onMarkerDragEnd(Marker marker);
    }

    /**
     * 信息窗口点击事件的回调接口。
     */
    public static abstract interface OnInfoWindowClickListener {
        /**
         * 当marker 的信息窗口被点击时，回调此方法。
         *
         * @param marker 被点击的信息窗口的marker 对象。
         */
        public abstract void onInfoWindowClick(Marker marker);
    }

    /**
     * 用户栅格服务回调接口
     */
    public static abstract interface GridUrlListener {
        /**
         * 根据栅格编号获取用户栅格URL。
         *
         * @param x 栅格x方向编号。
         * @param y 栅格y方向编号。
         * @param z 地图级别。
         * @return 栅格图块URL。
         */
        public abstract String getGridUrl(int x, int y, int z);
    }

    /**
     * 当一个任务完成或关闭时的回调接口。
     */
    public static abstract interface CancelableCallback {
        /**
         * 当一个任务完成时回调此方法。
         */
        public abstract void onFinish();

        /**
         * 当一个任务被终止时回调此方法。
         */
        public abstract void onCancel();
    }

    /**
     * 当用户位置改变时回调的方法类。
     */
    public static abstract interface OnMyLocationChangeListener {
        /**
         * 当用户位置改变时回调的方法。
         *
         * @param location 用户当前位置的对象。
         */
        public abstract void onMyLocationChange(Location location);
    }

    /**
     * 提供了一个可个性化定制的信息窗口视图的类。 这个类提供的方法是当一个信息窗口显示时回调的方法。无论是用户的手势调用显示信息窗口或一个程序调用方法显示信息窗口都会触发回调。 当你构造一个信息窗口时，这个类的方法将会按默认的顺序被调用。
     * 如果要定制化渲染这个信息窗口，需要重载getInfoWindow(Marker)方法。 如果只是需要替换信息窗口的内容，则需要重载getInfoContents(Marker)方法。
     */
    public static abstract interface InfoWindowAdapter {
        /**
         * 提供了一个个性化定制信息窗口的marker 对象。如果这个方法返回一个view，则它会被用来当对整个信息窗口。如果你在调用这个方法之后修改了信息窗口的view对象 ，那么这些改变不一定会起作用 。
         * 如果这个方法返回null，则将会使用默认的信息窗口风格，内容将会从getInfoContents(Marker)方法获取。
         *
         * @param marker 弹出的信息窗口的marker 对象。
         * @return 一个定制化的信息窗口的marker 对象，如果返回null 则使用默认的信息窗口风格。
         */
        public abstract View getInfoWindow(Marker marker);

        /**
         * 提供了一个给默认信息窗口定制内容的方法。这个方法只有在getInfoWindow(Marker)返回null 时才会被调用。如果这个方法返回一个view ，它将替代现有的默认的信息窗口，如果你在调用这个方法之后修改了view，则这些改变将不一定会呈现在信息窗口上 。如果这个方法返回null，将使用默认的方式渲染信息窗口。
         *
         * @param marker 弹出的信息窗口的marker 对象。
         * @return 一个定制化的view 做这个信息窗口的内容，如果返回null 将以默认内容渲染。
         */
        public abstract View getInfoContents(Marker marker);
    }

    /**
     * 当地图级别变化回的回调接口
     */
    public static abstract interface OnMapLevelChangeListener {
        /**
         * 地图级别变化回调
         *
         * @param level 地图级别
         */
        public abstract void onMapLevelChanged(float level);
    }
}
