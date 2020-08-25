

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


-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}



-keep public class com.sfmap.api.services.core.SearchException, com.sfmap.api.services.core.SearchException$* {
      public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.api.services.core.LatLonPoint {

     public protected <methods>;
      public protected <fields>;
}

# geocodesearch
-keep public class com.sfmap.api.services.geocoder.GeocodeSearch {
     public protected <fields>;
     public protected <methods>;
}
# geocode
-keep public class com.sfmap.api.services.geocoder.GeocodeAddress, com.sfmap.api.services.geocoder.GeocodeQuery, com.sfmap.api.services.geocoder.GeocodeResult{
     public protected <methods>;
       public protected <fields>;
}
-keep public interface com.sfmap.api.services.geocoder.GeocodeSearch,com.sfmap.api.services.geocoder.GeocodeSearch$OnGeocodeSearchListener{
     public protected <methods>;
      public protected <fields>;
}

# regeocode
-keep public class com.sfmap.api.services.geocoder.RegeocodeAddress, com.sfmap.api.services.geocoder.RegeocodeQuery, com.sfmap.api.services.geocoder.RegeocodeResult{
     public protected <methods>;
       public protected <fields>;
}

-keep public class com.sfmap.api.services.geocoder.RegeocodeAddress{
     public protected <fields>;
     public protected <methods>;
}

#district begin
-keep public class com.sfmap.api.services.district.DistrictItem, com.sfmap.api.services.district.DistrictResult, com.sfmap.api.services.district.DistrictSearchQuery{
     public protected <methods>;
       public protected <fields>;
}

-keep public class com.sfmap.api.services.district.DistrictSearch, com.sfmap.api.services.district.DistrictSearch$* {
     public protected <fields>;
     public protected <methods>;
}
#district end


#busline begin
-keep public class com.sfmap.api.services.busline.BusLineItem, com.sfmap.api.services.busline.BusLineResult, com.sfmap.api.services.busline.BusLineQuery{
     public protected <methods>;
       public protected <fields>;
}

-keep public class com.sfmap.api.services.busline.BusLineSearch, com.sfmap.api.services.busline.BusLineSearch$* {
     public protected <fields>;
     public protected <methods>;
}


-keep public class com.sfmap.api.services.busline.BusLineQuery$SearchType {
     public protected <fields>;
     public protected <methods>;
}


#busline end

#busstation begin
-keep public class com.sfmap.api.services.busline.BusStationItem, com.sfmap.api.services.busline.BusStationResult, com.sfmap.api.services.busline.BusStationQuery{
    public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.api.services.busline.BusStationSearch, com.sfmap.api.services.busline.BusStationSearch$* {
     public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.api.services.busline.BusStationQuery$StopSearchType {
     public protected <fields>;
     public protected <methods>;
}
#busstation end


#inputtips begin
-keep public class com.sfmap.api.services.help.Tip,com.sfmap.api.services.help.InputtipsQuery{
     public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.api.services.help.Inputtips, com.sfmap.api.services.help.Inputtips$* {
     public protected <fields>;
     public protected <methods>;
}


#route begin
-keep public class com.sfmap.api.services.route.RouteSearch, com.sfmap.api.services.route.Path,com.sfmap.api.services.route.WalkPath{
     public protected <methods>;
     public protected <fields>;
}
-keep public class com.sfmap.api.services.route.DrivePath, com.sfmap.api.services.route.BusPath,com.sfmap.api.services.route.Step,com.sfmap.api.services.route.DriveStep{
     public protected <methods>;
     public protected <fields>;
}
-keep public class com.sfmap.api.services.route.WalkStep,com.sfmap.api.services.route.BusStep,com.sfmap.api.services.route.RoutePoint{
     public protected <methods>;
     public protected <fields>;
}
-keep public class com.sfmap.api.services.route.WalkRouteResult, com.sfmap.api.services.route.BusRouteResult,com.sfmap.api.services.route.DriveRouteResult,com.sfmap.api.services.route.RouteResult{
     public protected <methods>;
     public protected <fields>;
}

-keep public class com.sfmap.api.services.route.Doorway,com.sfmap.api.services.route.RouteBusLineItem,com.sfmap.api.services.route.RouteBusWalkItem{
     public protected <methods>;
     public protected <fields>;
}

-keep public class com.sfmap.api.services.route.RouteSearch$BusRouteQuery {
      public protected <methods>;
      public protected <fields>;
}
-keep public class com.sfmap.api.services.route.RouteSearch$DriveRouteQuery {
      public protected <methods>;
      public protected <fields>;
}
-keep public class com.sfmap.api.services.route.RouteSearch$FromAndTo {
      public protected <methods>;
      public protected <fields>;
}
-keep public class com.sfmap.api.services.route.RouteSearch$WalkRouteQuery {
      public protected <methods>;
      public protected <fields>;
}

-keep public class com.sfmap.api.services.route.Route$FromAndTo {
      public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.services.route.Route {
      public protected <fields>;
     public protected <methods>;
}


-keep public class com.sfmap.api.services.route.Segment, com.sfmap.api.services.route.Segment$* {
      public protected <fields>;
     public protected <methods>;
}
-keep public interface com.sfmap.api.services.route.RouteSearch$OnRouteSearchListener{
     public protected <methods>;
     public protected <fields>;
}

#route end

#poisearch
-keep public class com.sfmap.api.services.poisearch.PoiPagedResult, com.sfmap.api.services.poisearch.PoiPagedResult$*{
      public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.services.poisearch.PoiResult, com.sfmap.api.services.poisearch.PoiResult$*{
     public protected <methods>;
     public protected <fields>;
}
-keep public class com.sfmap.api.services.poisearch.PoiItem, com.sfmap.api.services.poisearch.PoiItem$*{
      public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.services.poisearch.PoiSearch$Query {
      public protected <methods>;
      public protected <fields>;
}
-keep public class com.sfmap.api.services.poisearch.PoiSearch$SearchBound  {
      public protected <methods>;
      public protected <fields>;
}
-keep public class com.sfmap.api.services.poisearch.PoiSearch$OnPoiSearchListener  {
      public protected <methods>;
      public protected <fields>;
}
-keep public class com.sfmap.api.services.poisearch.PoiSearch {
      public protected <methods>;
      public protected <fields>;
}

-keep public class com.sfmap.api.services.poisearch.PoiTypeDef, com.sfmap.api.services.poisearch.PoiTypeDef$* {
      public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.api.services.poisearch.ComplexSearch {
      public protected <methods>;
      public protected <fields>;
}

-keep public class com.sfmap.api.services.poisearch.ComplexSearch$Query {
      public protected <methods>;
      public protected <fields>;
}

-keep public class com.sfmap.api.services.poisearch.ComplexSearch$OnComplexSearchListener {
      public protected <methods>;
      public protected <fields>;
}

-keep public class com.sfmap.api.services.poisearch.ComplexSearchResult {
      public protected <methods>;
      public protected <fields>;
}

# poisearch end


#cloud

-keep public class com.sfmap.api.services.cloud.CloudStorage,com.sfmap.api.services.cloud.CloudStorage$OnCloudStorageListener{
    public protected <methods>;
     public protected <fields>;
}

-keep public class com.sfmap.api.services.cloud.CloudStorageResult,com.sfmap.api.services.cloud.DBFieldInfo,com.sfmap.api.services.cloud.DBFieldInfo$FieldType{
    public protected <methods>;
     public protected <fields>;
}
-keep public class com.sfmap.api.services.cloud.CloudItem,com.sfmap.api.services.cloud.CloudDatasetItem{
    public protected <methods>;
     public protected <fields>;
}
-keep public class com.sfmap.api.services.cloud.CloudSearch,com.sfmap.api.services.cloud.CloudSearch$OnCloudSearchListener{
    public protected <methods>;
     public protected <fields>;
}
-keep public class com.sfmap.api.services.cloud.CloudSearch$SearchType,com.sfmap.api.services.cloud.CloudSearch$Query{
    public protected <methods>;
     public protected <fields>;
}
-keep public class com.sfmap.api.services.cloud.CloudDatasetSearch,com.sfmap.api.services.cloud.CloudDatasetSearch$OnCloudDatasetSearchListener{
    public protected <methods>;
     public protected <fields>;
}
-keep public class com.sfmap.api.services.cloud.CloudDatasetSearch$SearchType,com.sfmap.api.services.cloud.CloudDatasetSearch$Query{
    public protected <methods>;
     public protected <fields>;
}
-keep public class com.sfmap.api.services.cloud.CloudDatasetSearchResult,com.sfmap.api.services.cloud.CloudSearchResult{
    public protected <methods>;
     public protected <fields>;
}
#cloud end


-keep public class com.sfmap.api.services.core.ServerUrlSetting, com.sfmap.api.services.core.ServerUrlSetting$* {
      public protected <fields>;
      public protected <methods>;
}

# localpoisearh
-keep public class com.sfmap.api.services.localsearch.ADCodeLevel {
      public protected <fields>;
      public protected <methods>;
}
-keep public class com.sfmap.api.services.localsearch.LocalPoiResult {
      public protected <fields>;
      public protected <methods>;
}
-keep public class com.sfmap.api.services.localsearch.LocalPoiSearch, com.sfmap.api.services.localsearch.LocalPoiSearch$*{
      public protected <fields>;
      public protected <methods>;
}
-keep public class com.sfmap.api.services.localsearch.SearchType {
      public protected <fields>;
      public protected <methods>;
}
-keep public class com.sfmap.api.services.localsearch.model.SearchResultInfo {
      public protected <fields>;
      public protected <methods>;
}
-keep  class com.sfmap.api.services.localsearch.SearchCore {
      public protected <fields>;
      public protected <methods>;
}
