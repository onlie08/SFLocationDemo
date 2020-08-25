

-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontoptimize
-dontusemixedcaseclassnames

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService


-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

 #maps
-keep public class com.sfmap.api.maps.MapController{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.MapController$OnCacheRemoveListener{
     public  <methods>;
}
-keep public class com.sfmap.api.maps.MapController$OnMapLevelChangeListener{
     public  <methods>;
}
-keep public abstract interface com.sfmap.api.maps.MapController$OnPOIClickListener,com.sfmap.api.maps.MapController$OnMapLoadedListener,com.sfmap.api.maps.MapController$GridUrlListener{
     public  <methods>;
}
-keep public abstract interface com.sfmap.api.maps.MapController$OnMapTouchListener,com.sfmap.api.maps.MapController$OnMapClickListener,com.sfmap.api.maps.MapController$OnMapScreenShotListener{
     public  <methods>;
}
-keep public abstract interface com.sfmap.api.maps.MapController$OnMapLongClickListener,com.sfmap.api.maps.MapController$OnCameraChangeListener,com.sfmap.api.maps.MapController$OnMarkerClickListener{
     public  <methods>;
}
-keep public abstract interface com.sfmap.api.maps.MapController$OnPolylineClickListener,com.sfmap.api.maps.MapController$OnMarkerDragListener,com.sfmap.api.maps.MapController$OnInfoWindowClickListener{
     public  <methods>;
}
-keep public class com.sfmap.api.maps.MapController$CancelableCallback,com.sfmap.api.maps.MapController$OnMyLocationChangeListener,com.sfmap.api.maps.MapController$InfoWindowAdapter{
     public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.api.maps.MapException{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.MapOptionsCreator{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.MapOptions{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.MapUtils{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.MapView{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.CameraUpdate{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.CameraUpdateFactory{
     public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.api.maps.CustomRenderer{
     public protected <methods>;
}
-keep public interface com.sfmap.api.maps.LocationSource,com.sfmap.api.maps.LocationSource$OnLocationChangedListener{
     public protected <methods>;
}

-keep public class com.sfmap.api.maps.MapsInitializer{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.Projection{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.SupportMapFragment{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.TextureMapView{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.TextureSupportMapFragment{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.UiSettings{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.TextureMapFragment{
     public protected <fields>;
     public protected <methods>;
}
# maps

# overlay
-keep public class com.sfmap.api.maps.cluster.**{*;}

-keep public class com.sfmap.api.maps.overlay.BusLineOverlay{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.overlay.BusRouteOverlay{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.overlay.DrivingRouteOverlay{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.overlay.PoiOverlay{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.overlay.WalkRouteOverlay{
     public protected <fields>;
     public protected <methods>;
}
# overlay


# offlinemap
-keep public class com.sfmap.api.maps.offlinemap.City{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.offlinemap.OfflineMapCity{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.offlinemap.OfflineMapManager{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.offlinemap.OfflineMapManager$OfflineMapDownloadListener{
     public protected <methods>;
}

-keep public class com.sfmap.api.maps.offlinemap.OfflineMapProvince{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.offlinemap.Province{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.offlinemap.OfflineMapStatus{
     public protected <fields>;
     public protected <methods>;
}
# offlinemap


# model
-keep public class com.sfmap.api.maps.model.MapGLOverlay{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.Arc{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.ArcOptions{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.ArcOptionsCreator{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.BitmapDescriptor{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.BitmapDescriptorCreator{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.BitmapDescriptorFactory{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.CameraPosition,com.sfmap.api.maps.model.CameraPosition$Builder{
     public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.api.maps.model.CameraPositionCreator{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.Circle{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.CircleOptions{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.CircleOptionsCreator{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.Gradient{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.GroundOverlay{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.GroundOverlayOptions{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.GroundOverlayOptionsCreator{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.HeatmapTileProvider,com.sfmap.api.maps.model.HeatmapTileProvider$Builder{
     public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.api.maps.model.LatLng{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.LatLngBounds,com.sfmap.api.maps.model.LatLngBounds$Builder{
     public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.api.maps.model.LatLngBoundsCreator{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.Marker{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.LatLngCreator{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.MarkerOptions{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.MarkerOptionsCreator{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.MyLocationStyle{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.MyLocationStyleCreator{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.MyTrafficStyle{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.NavigateArrow{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.NavigateArrowOptions{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.NavigateArrowOptionsCreator{
     public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.api.maps.model.Poi{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.PoiCreator{
     public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.api.maps.model.Polygon{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.PolygonOptions{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.PolygonOptionsCreator{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.Polyline{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.PolylineOptions{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.PolylineOptionsCreator{
     public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.api.maps.model.RuntimeRemoteException{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.Text{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.TextOptions{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.TextOptionsCreator{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.Tile{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.TileOverlay{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.TileOverlayOptions{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.TileOverlayOptionsCreator{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.TileProjection{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.TileProjectionCreator{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.TileProvider{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.UrlTileProvider{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.VisibleRegion{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.VisibleRegionCreator{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.WeightedLatLng{
     public protected <fields>;
     public protected <methods>;
}

# model

-keep public class com.sfmap.mapcore.FPoint{
     public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.mapcore.Tile{
     public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.mapcore.NativeLineRenderer{
     public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.mapcore.DPoint{
     public protected <fields>;
     public protected <methods>;
}


-keep public class com.sfmap.mapcore.IPoint{
     public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.mapcore.MapPoi{
     public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.mapcore.MapCore{
     public protected private <fields>;
     public protected private <methods>;
}
-keep public class com.sfmap.mapcore.MapProjection{
     public protected private <fields>;
     public protected private <methods>;
}
#extral
-keep public class com.sfmap.api.maps.ExtralBaseDraw{
     public   <fields>;
     public   <methods>;
}
-keep public class com.sfmap.api.maps.ExtralDrawBitmap{
     public   <fields>;
     public   <methods>;
}
-keep public class com.sfmap.api.maps.ExtralDrawPolygon{
     public   <fields>;
     public   <methods>;
}
-keep public class com.sfmap.api.maps.ExtralDrawPolyline{
     public   <fields>;
     public   <methods>;
}
-keep public class com.sfmap.api.maps.ExtralDrawText{
     public   <fields>;
     public   <methods>;
}
-keep public class com.sfmap.api.maps.ExtralDrawArc{
     public   <fields>;
     public   <methods>;
}
-keep public class com.sfmap.api.maps.ExtralDrawCircle{
     public   <fields>;
     public   <methods>;
}
-keep public class com.sfmap.api.mapcore.util.AppInfo{
     public   <fields>;
     public   <methods>;
}
-keep public class com.sfmap.api.maps.DesUtil{
     public   <fields>;
     public   <methods>;
}



