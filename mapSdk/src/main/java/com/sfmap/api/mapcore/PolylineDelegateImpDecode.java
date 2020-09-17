package com.sfmap.api.mapcore;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.RemoteException;
import android.util.Log;

import com.sfmap.api.mapcore.util.LogManager;
import com.sfmap.api.mapcore.util.Util;
import com.sfmap.api.mapcore.util.SDKLogHandler;
import com.sfmap.api.maps.MapUtils;
import com.sfmap.api.maps.model.BitmapDescriptor;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.maps.model.LatLngBounds;
import com.sfmap.mapcore.NativeLineRenderer;
import com.sfmap.mapcore.FPoint;
import com.sfmap.mapcore.IPoint;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import javax.microedition.khronos.opengles.GL10;

class PolylineDelegateImpDecode
  implements IPolylineDelegateDecode
{
  private GLOverlayLayerDecode layer;
  private String sid;
  private List<IPoint> geoPoints = new ArrayList<IPoint>();
  private List<LatLng> points = new ArrayList<LatLng>();
  private List<BitmapDescriptor> f = new ArrayList<BitmapDescriptor>();
  private List<Integer> g = new ArrayList<Integer>();
  private List<Integer> h = new ArrayList<Integer>();
  private int [] pointArray;
  private FloatBuffer i;
  private BitmapDescriptor bitmapDescriptor = null;
  private LatLngBounds bounds = null;
  private Object synObj = new Object();
  private boolean visible = true;
  private boolean n = true;
  private boolean isGeodesic = false;
  private boolean isDottedLine = false;
  private boolean isGetTexsureId = false;
  private boolean r = false;
  private boolean useTexture = true;
  private boolean t = false;
  private int drawType = 0;//绘制类型
  private int texsureId = 0;
  private int color = -16777216;
  private int geoPointsSize = 0;
  private float width = 10.0F;
  private float zIndex = 0.0F;
  private float A = 0.0F;
  private float alpha;
  private float red_color;
  private float green_color;
  private float blue_color;
  private float transparency = 0.0F;//透明度
  private float G = 0.0F;
  //  private float[] H;
  private int[] I;
  private int[] J;
  private double K = 5.0D;
  
  public void setUseTexture(boolean useTexture)
  {
    if(!useTexture){
      this.drawType = 0;
    }
    this.useTexture = useTexture;
    this.layer.mapDelegate.setRunLowFrame(false);
  }
  
  public void geodesic(boolean isGeodesic)
    throws RemoteException
  {
    this.isGeodesic = isGeodesic;
    this.layer.mapDelegate.setRunLowFrame(false);
  }
  
  public boolean isGeodesic()
  {
    return this.isGeodesic;
  }
  
  public void setDottedLine(boolean isDottedLine)
  {
    if ((this.drawType == 2) || (this.drawType == 0))
    {
      this.isDottedLine = isDottedLine;
      if ((isDottedLine) && (this.n)) {
        this.drawType = 2;
      }
      this.layer.mapDelegate.setRunLowFrame(false);
    }
  }
  
  public boolean isDottedLine()
  {
    return this.isDottedLine;
  }
  
  public PolylineDelegateImpDecode(GLOverlayLayerDecode paramv)
  {
    this.layer = paramv;
    try
    {
      this.sid = getId();
    }
    catch (RemoteException localRemoteException)
    {
      SDKLogHandler.exception(localRemoteException, "PolylineDelegateImp", "create");
      localRemoteException.printStackTrace();
    }
  }
  
  void b(List<LatLng> srcPoints)
    throws RemoteException
  {
    ArrayList<IPoint> desPoints = new ArrayList<IPoint>();
    LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
    LatLng lastPoint;
    if (srcPoints != null)
    {
      lastPoint = null;
      for (LatLng currentPoint : srcPoints) {
        if ((currentPoint != null) && (!currentPoint.equals(lastPoint)))
        {
          IPoint curGeoPoint;//国测局坐标
          if (!this.isGeodesic)//非大地线
          {
            if(lastPoint ==null){
              curGeoPoint = new IPoint();
              this.layer.mapDelegate.latlon2Geo(currentPoint.latitude, currentPoint.longitude, curGeoPoint);
              desPoints.add(curGeoPoint);
              boundsBuilder.include(currentPoint);
            }else{
              //判断是否插值
              insert1(lastPoint,currentPoint,desPoints,boundsBuilder);
            }
          }
          else if (lastPoint != null)
          {
            if (Math.abs(currentPoint.longitude - ((LatLng)lastPoint).longitude) < 0.01D)
            {
              curGeoPoint = new IPoint();
              this.layer.mapDelegate.latlon2Geo(((LatLng)lastPoint).latitude, ((LatLng)lastPoint).longitude, curGeoPoint);
              
              desPoints.add(curGeoPoint);
              boundsBuilder.include((LatLng) lastPoint);
              IPoint localIPoint2 = new IPoint();
              this.layer.mapDelegate.latlon2Geo(currentPoint.latitude, currentPoint.longitude, localIPoint2);
              
              desPoints.add(localIPoint2);
              boundsBuilder.include(currentPoint);
            }
            else
            {
              insertPoint((LatLng) lastPoint, currentPoint, desPoints, boundsBuilder);
            }
          }
          lastPoint = currentPoint;
        }
      }
    }
    this.geoPoints = desPoints;
    this.geoPointsSize = geoPoints.size();
    if (this.geoPoints.size() > 0) {
      this.bounds = boundsBuilder.build();
    }
    this.layer.mapDelegate.setRunLowFrame(false);
  }

  /**
   * 非大地线是否插值计算。
   * @param firstPoint
   * @param secondPoint
   * @param desPoints
   * @param boundsBuilder
   */
  private void insert1(LatLng firstPoint, LatLng secondPoint, List<IPoint> desPoints, LatLngBounds.Builder boundsBuilder){
    IPoint firstGeoPoint = new IPoint();
    this.layer.mapDelegate.latlon2Geo(firstPoint.latitude, firstPoint.longitude, firstGeoPoint);

    IPoint secondGeoPoint = new IPoint();
    this.layer.mapDelegate.latlon2Geo(secondPoint.latitude, secondPoint.longitude, secondGeoPoint);

    int count = 0;
    long disSquare = 0;
    if(true) {
      disSquare = getDisSquare(firstGeoPoint.x, firstGeoPoint.y, secondGeoPoint.x, secondGeoPoint.y);
      count = (int) (disSquare / 65000000000l);
    }
//    Log.e("insert1","____count:"+count);
    if(count > 0){
      count += 1;
      int dx = ( secondGeoPoint.x - firstGeoPoint.x )/count;
      int dy = ( secondGeoPoint.y - firstGeoPoint.y )/count;
      for( int i = 1; i < count; i++ ){
        IPoint insertPoint = new IPoint(firstGeoPoint.x + (i * dx),firstGeoPoint.y + (i * dy));
        desPoints.add(insertPoint);
      }
    }
    desPoints.add(secondGeoPoint);
    boundsBuilder.include(secondPoint);
  }
  IPoint a(IPoint firstGeoPoint, IPoint secondGeoPoint, IPoint centerGeoPoint, double paramDouble, int paramInt)
  {
    IPoint localIPoint = new IPoint();
    double d1 = secondGeoPoint.x - firstGeoPoint.x;
    double d2 = secondGeoPoint.y - firstGeoPoint.y;
    double d3 = d2 * d2 / (d1 * d1) + 1.0D;
    localIPoint.y = ((int)(paramInt * paramDouble / Math.sqrt(d3) + centerGeoPoint.y));
    localIPoint.x = ((int)((centerGeoPoint.y - localIPoint.y) * d2 / d1 + centerGeoPoint.x));
    
    return localIPoint;
  }
  void insertPoint(List<IPoint> paramList1, List<IPoint> paramList2, double paramDouble,int count)
  {
    if (paramList1.size() != 3) {
      return;
    }
    float f1 = 1F;
    for (int i1 = 0; i1 <= count; i1 = (int)(i1 + f1))
    {
      float f2 = i1 / (float)count;
      IPoint localIPoint = new IPoint();

      double d1 = (1.0D - f2) * (1.0D - f2) * ((IPoint)paramList1.get(0)).x + 2.0F * f2 * (1.0D - f2) * ((IPoint)paramList1.get(1)).x * paramDouble + f2 * f2 * ((IPoint)paramList1.get(2)).x;

      double d2 = (1.0D - f2) * (1.0D - f2) * ((IPoint)paramList1.get(0)).y + 2.0F * f2 * (1.0D - f2) * ((IPoint)paramList1.get(1)).y * paramDouble + f2 * f2 * ((IPoint)paramList1.get(2)).y;

      double d3 = (1.0D - f2) * (1.0D - f2) + 2.0F * f2 * (1.0D - f2) * paramDouble + f2 * f2;
      double d4 = (1.0D - f2) * (1.0D - f2) + 2.0F * f2 * (1.0D - f2) * paramDouble + f2 * f2;

      localIPoint.x = ((int)(d1 / d3));
      localIPoint.y = ((int)(d2 / d4));

      paramList2.add(localIPoint);
    }
  }
  void insertPoint(List<IPoint> paramList1, List<IPoint> desPoints, double paramDouble)
  {
    if (paramList1.size() != 3) {
      return;
    }
    float f1 = 1F;
    for (int i1 = 0; i1 <= 10; i1 = (int)(i1 + f1))
    {
      float f2 = i1 / 10.0F;
      IPoint localIPoint = new IPoint();
      
      double d1 = (1.0D - f2) * (1.0D - f2) * ((IPoint)paramList1.get(0)).x + 2.0F * f2 * (1.0D - f2) * ((IPoint)paramList1.get(1)).x * paramDouble + f2 * f2 * ((IPoint)paramList1.get(2)).x;
      
      double d2 = (1.0D - f2) * (1.0D - f2) * ((IPoint)paramList1.get(0)).y + 2.0F * f2 * (1.0D - f2) * ((IPoint)paramList1.get(1)).y * paramDouble + f2 * f2 * ((IPoint)paramList1.get(2)).y;
      
      double d3 = (1.0D - f2) * (1.0D - f2) + 2.0F * f2 * (1.0D - f2) * paramDouble + f2 * f2;
      double d4 = (1.0D - f2) * (1.0D - f2) + 2.0F * f2 * (1.0D - f2) * paramDouble + f2 * f2;
      
      localIPoint.x = ((int)(d1 / d3));
      localIPoint.y = ((int)(d2 / d4));
      
      desPoints.add(localIPoint);
    }
  }
  
  void insertPoint(LatLng firstPoint, LatLng secondPoint, List<IPoint> desPoints, LatLngBounds.Builder boundsBuilder)
  {
    double d1 = Math.abs(firstPoint.longitude - secondPoint.longitude) * Math.PI / 180.0D;
    
    LatLng centerPoint = new LatLng((secondPoint.latitude + firstPoint.latitude) / 2.0D, (secondPoint.longitude + firstPoint.longitude) / 2.0D, false);
    
    boundsBuilder.include(firstPoint).include(centerPoint).include(secondPoint);
    
    int i1 = centerPoint.latitude > 0.0D ? -1 : 1;
    
    IPoint firstGeoPoint = new IPoint();
    this.layer.mapDelegate.latlon2Geo(firstPoint.latitude, firstPoint.longitude, firstGeoPoint);
    
    IPoint secondGeoPoint = new IPoint();
    this.layer.mapDelegate.latlon2Geo(secondPoint.latitude, secondPoint.longitude, secondGeoPoint);

    IPoint centerGeoPoint = new IPoint();
    this.layer.mapDelegate.latlon2Geo(centerPoint.latitude, centerPoint.longitude, centerGeoPoint);
    
    double d2 = Math.cos(d1 * 0.5D);
    double d3 = Math.hypot(firstGeoPoint.x - secondGeoPoint.x, firstGeoPoint.y - secondGeoPoint.y) * 0.5D * Math.tan(d1 * 0.5D);
    
    IPoint center = a(firstGeoPoint, secondGeoPoint, centerGeoPoint, d3, i1);
    
    ArrayList<IPoint> points = new ArrayList<IPoint>();
    points.add(firstGeoPoint);
    points.add(center);
    points.add(secondGeoPoint);
    int count = 0;
    if(isDottedLine) {
      long disSquare = getDisSquare(firstGeoPoint.x, firstGeoPoint.y, secondGeoPoint.x, secondGeoPoint.y);
      count = (int) (disSquare / 65000000000l);
    }
    if(count>10){
      insertPoint(points, desPoints, d2,count);
    }else{
      insertPoint(points, desPoints, d2);
    }
  }

  private long getDisSquare(long x1,long y1,long x2,long y2){
    return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
  }
  public void remove()
    throws RemoteException
  {
    this.layer.removeOverlay(getId());
    if ((this.J != null) && (this.J.length > 0))
    {
      for (int i1 = 0; i1 < this.J.length; i1++) {
        this.layer.a(Integer.valueOf(this.J[i1]));
      }
      this.J = null;
    }
    if (this.texsureId > 0)
    {
      this.layer.a(Integer.valueOf(this.texsureId));
      this.texsureId = 0;
    }
    this.layer.mapDelegate.setRunLowFrame(false);
  }
  
  public String getId()
    throws RemoteException
  {
    if (this.sid == null) {
      //this.b = texsureId.a("Polyline");
      this.sid = GLOverlayLayerDecode.CreateId("Polyline");
    }
    return this.sid;
  }

  public void addPoint(LatLng point)throws RemoteException{
    if(point  == null){
      return;
    }
    int len = points.size();
    if(len>0&&point.equals(points.get(len-1))){
      return;
    }
    this.points.add(point);
    try{
      synchronized (this.synObj) {
        IPoint curGeoPoint = new IPoint();
        this.layer.mapDelegate.latlon2Geo(point.latitude, point.longitude, curGeoPoint);
        LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
        boundsBuilder.include(bounds.northeast);
        boundsBuilder.include(bounds.southwest);
        boundsBuilder.include(point);
        geoPoints.add(curGeoPoint);
        this.geoPointsSize = this.geoPoints.size();
        if ( geoPointsSize > 0) {
          this.bounds = boundsBuilder.build();
        }
        getGeoArray();
      }
      this.layer.mapDelegate.setRunLowFrame(false);
    }catch (Throwable throwable)
    {
      SDKLogHandler.exception(throwable, "PolylineDelegateImp", "addPoint");
      this.geoPoints.clear();
      throwable.printStackTrace();
    }
  }
  public void setPoints(List<LatLng> points)
    throws RemoteException
  {
    try
    {
      this.points = points;
      synchronized (this.synObj)
      {
        b(points);
        getGeoArray();
        calMapFPoint();
      }
      this.layer.mapDelegate.setRunLowFrame(false);
    }
    catch (Throwable throwable)
    {
      SDKLogHandler.exception(throwable, "PolylineDelegateImp", "setPoints");
      this.geoPoints.clear();
      throwable.printStackTrace();
    }
  }
  private void getGeoArray(){
      int size = this.geoPoints.size();
      int [] pointArray = new int[size * 2];
      for(int i = 0 ; i < size ; i ++){
        pointArray[i*2] = this.geoPoints.get(i).x;
        pointArray[i*2+1] = this.geoPoints.get(i).y;
      }
      this.pointArray = pointArray;
  }
  public List<LatLng> getPoints()
    throws RemoteException
  {
    return this.points;
  }
  
  public void setWidth(float width)
    throws RemoteException
  {
    this.width = width;
    this.layer.mapDelegate.setRunLowFrame(false);
  }
  
  public float getWidth()
    throws RemoteException
  {
    return this.width;
  }
  
  public void setColor(int color)
  {
    if ((this.drawType == 0) || (this.drawType == 2))
    {
      this.color = color;
      this.alpha = (Color.alpha(color) / 255.0F);
      this.red_color = (Color.red(color) / 255.0F);
      this.green_color = (Color.green(color) / 255.0F);
      this.blue_color = (Color.blue(color) / 255.0F);
      if (this.n) {
        this.drawType = 0;
      }
      this.layer.mapDelegate.setRunLowFrame(false);
    }
  }
  
  public int getColor()
    throws RemoteException
  {
    return this.color;
  }
  
  public void setZIndex(float zIndex)
    throws RemoteException
  {
    this.zIndex = zIndex;
    this.layer.b();
    this.layer.mapDelegate.setRunLowFrame(false);
  }
  
  public float getZIndex()
    throws RemoteException
  {
    return this.zIndex;
  }
  
  public void setVisible(boolean visible)
    throws RemoteException
  {
    this.visible = visible;
    this.layer.mapDelegate.setRunLowFrame(false);
  }
  
  public boolean isVisible()
    throws RemoteException
  {
    return this.visible;
  }
  
  public boolean equals(IOverlayDelegateDecode paramai)
    throws RemoteException
  {
    if ((equals(paramai)) || 
      (paramai.getId().equals(getId()))) {
      return true;
    }
    return false;
  }
  
  public int hashCodeRemote()
    throws RemoteException
  {
    return super.hashCode();
  }
  
  public boolean a()
  {
    if (this.bounds == null) {
      return false;
    }
    LatLngBounds bounds = this.layer.mapDelegate.getMapBounds();
    if (bounds == null) {
      return true;
    }
    return (bounds.contains(this.bounds)) || (this.bounds.intersects(bounds));
  }
  
  public void calMapFPoint()
    throws RemoteException
  {


  }
  
  private void o()
  {
    if ((this.geoPointsSize > 5000) && (this.A <= 12.0F))
    {
      float f1 = this.width / 2.0F + this.A / 2.0F;
      f1 = f1 <= 200.0F ? f1 : 200.0F;
      this.G = this.layer.mapDelegate.getMapProjection().getMapLenWithWin((int)f1);
    }
    else
    {
      this.G = this.layer.mapDelegate.getMapProjection().getMapLenWithWin(10);
    }
  }
  
  private boolean a(FPoint paramFPoint1, FPoint paramFPoint2)
  {
    return (Math.abs(paramFPoint2.x - paramFPoint1.x) >= this.G) || (Math.abs(paramFPoint2.y - paramFPoint1.y) >= this.G);
  }
  
  public void a(BitmapDescriptor bitmapDescriptor)
  {
    this.n = false;
    this.drawType = 1;
    this.bitmapDescriptor = bitmapDescriptor;
    this.layer.mapDelegate.setRunLowFrame(false);
  }
  
  public void draw(GL10 gl10)
    throws RemoteException
  {
    if ((this.geoPoints == null) || (this.geoPoints.size() == 0) || (this.width <= 0.0F)) {
      return;
    }
    if (this.geoPointsSize > 0) {
        drawLine(gl10);
    }
    this.r = true;
  }
  
  private void drawLine(GL10 gl10)
  {
    float f1 = this.layer.mapDelegate.getMapProjection().getMapLenWithWin((int)this.width);
    switch (this.drawType)
    {
    case 0: //简单实现
      drawSampleLine(gl10, f1);
      break;
    case 2://绘制虚线
      drawDottedLine(gl10, f1);
      break;
    case 1: //纹理绘制
      drawTextureLine(gl10, f1);
      break;
    case 3: 
//      c(gl10, f1);
      break;
    case 4: 
//      b(gl10, f1);
      break;
    case 5: 
//      draw(gl10, f1);
      break;
    }
  }

  private void drawTextureLine(GL10 gl10, float lineWidth)
  {
    //原有实现
    if (!this.isGetTexsureId)
    {
      this.texsureId = this.layer.mapDelegate.getTexsureId();
      if (this.texsureId == 0)
      {
        int [] localObject1 = { 0 };
        gl10.glGenTextures(1, localObject1, 0);
        this.texsureId = localObject1[0];
      }
      if (this.bitmapDescriptor != null) {
        Util.bindTexture(gl10, this.texsureId, this.bitmapDescriptor.getBitmap(), true);
      }
      this.isGetTexsureId = true;
    }

    int texid = this.texsureId; //获取纹理ID

    long stateInstance = this.layer.mapDelegate.getMapProjection().getInstanceHandle();
    //TODO 需要在创建纹理时初始化本信息
    int texOrgPixelLen = 64;
    float texLen = this.layer.mapDelegate.getMapProjection().getMapLenWithWin(texOrgPixelLen);
    LatLng latLng = points.get(0);
    int finalcolor = ((int)(this.alpha *255)) << 24 | ((int)(this.red_color * 255)) << 16 | ((int)(this.green_color * 255)) << 8 | ((int)(this.blue_color * 255)) ; //不需要使用颜色
    boolean usecolor = false; //不使用颜色
    boolean complexTex = false;
    NativeLineRenderer.nativeDrawLineByTextureID(pointArray, 10, stateInstance, texLen, lineWidth, texid, finalcolor, complexTex, usecolor);
  }
  
  private void drawDottedLine(GL10 gl10, float lineWidth)
  {
    long stateInstance = this.layer.mapDelegate.getMapProjection().getInstanceHandle();
    //TODO 需要在创建纹理时初始化本信息
    int texOrgPixelLen = 32;
    float texLen = this.layer.mapDelegate.getMapProjection().getMapLenWithWin(texOrgPixelLen); //系统默认的纹理像素宽高都是32像素
    int texid = this.layer.mapDelegate.getDottedLineTextureID(); //获得系统默认的虚线纹理


    //虚线的颜色
    int finalcolor = ((int)(this.alpha *255)) << 24 | ((int)(this.red_color * 255)) << 16 | ((int)(this.green_color * 255)) << 8 | ((int)(this.blue_color * 255)) ;
    boolean usecolor = true; //使用颜色替换纹理中的颜色
    boolean complexTex = false; //简单纹理
    NativeLineRenderer.nativeDrawLineByTextureID(pointArray, pointArray.length, stateInstance, texLen, lineWidth, texid, finalcolor, complexTex, usecolor);
  }
  
  private void drawSampleLine(GL10 gl10, float lineWidth)
  {
    try
    {

      //如下是绘制带有颜色的实线
      long stateInstance = this.layer.mapDelegate.getMapProjection().getInstanceHandle();
      //TODO 需要在创建纹理时初始化本信息
      int texOrgPixelLen = 32;
      float texLen = this.layer.mapDelegate.getMapProjection().getMapLenWithWin(texOrgPixelLen); //系统默认的纹理像素宽高都是32像素
      int texid = this.layer.mapDelegate.getLineTextureID(); //系统默认的实线纹理ID
      int finalcolor = ((int)(this.alpha *255)) << 24 | ((int)(this.red_color * 255)) << 16 | ((int)(this.green_color * 255)) << 8 | ((int)(this.blue_color * 255)) ;
      boolean usecolor = true;  //使用颜色替换纹理中的颜色
      boolean complexTex = false; //简单纹理
      NativeLineRenderer.nativeDrawLineByTextureID(pointArray, pointArray.length, stateInstance, texLen, lineWidth, texid, finalcolor, complexTex, usecolor);
    }
    catch (Throwable localThrowable)
    {
      LogManager.writeLog(LogManager.productInfo, hashCode() + " drawSingleColorLine exception: " + localThrowable.getMessage(), 115);
    }
  }
  
  private boolean p()
  {
    boolean boolvalue = false;
    try
    {
      this.A = this.layer.mapDelegate.getCameraPosition().zoom;
      o();
      if ((this.A <= 10.0F) || (this.drawType > 2)) {
        return false;
      }
    }
    catch (RemoteException localRemoteException)
    {
      localRemoteException.printStackTrace();
    }
    try
    {
      if (this.layer.mapDelegate != null)
      {
        Rect localRect = new Rect(-100, -100, this.layer.mapDelegate.getMapWidth() + 100, this.layer.mapDelegate.getMapHeight() + 100);
        LatLng localLatLng1 = this.bounds.northeast;
        LatLng localLatLng2 = this.bounds.southwest;
        IPoint localIPoint1 = new IPoint();
        this.layer.mapDelegate.getLatLng2Pixel(localLatLng1.latitude, localLatLng2.longitude, localIPoint1);
        
        IPoint localIPoint2 = new IPoint();
        this.layer.mapDelegate.getLatLng2Pixel(localLatLng1.latitude, localLatLng1.longitude, localIPoint2);
        
        IPoint localIPoint3 = new IPoint();
        this.layer.mapDelegate.getLatLng2Pixel(localLatLng2.latitude, localLatLng1.longitude, localIPoint3);
        
        IPoint localIPoint4 = new IPoint();
        this.layer.mapDelegate.getLatLng2Pixel(localLatLng2.latitude, localLatLng2.longitude, localIPoint4);
        if ((localRect.contains(localIPoint1.x, localIPoint1.y)) && (localRect.contains(localIPoint2.x, localIPoint2.y)) && 
          (localRect.contains(localIPoint3.x, localIPoint3.y)) && 
          (localRect.contains(localIPoint4.x, localIPoint4.y))) {
        	boolvalue = false;
        }
      }
      return true; //???????
    }
    catch (Throwable localThrowable) {}
    return false; //???????
  }
  
  public void destroy()
  {
    try
    {
      remove();
//      if (this.H != null) {
//        this.H = null;
//      }
      if (this.i != null)
      {
        this.i.clear();
        this.i = null;
      }
      if ((this.f != null) && (this.f.size() > 0)) {
        for (BitmapDescriptor localBitmapDescriptor : this.f) {
          localBitmapDescriptor.recycle();
        }
      }
      if (this.bitmapDescriptor != null) {
        this.bitmapDescriptor.recycle();
      }
      if (this.h != null)
      {
        this.h.clear();
        this.h = null;
      }
      if (this.g != null)
      {
        this.g.clear();
        this.g = null;
      }
    }
    catch (Throwable localThrowable)
    {
      SDKLogHandler.exception(localThrowable, "PolylineDelegateImp", "destroy");
      localThrowable.printStackTrace();
      Log.d("destroy erro", "PolylineDelegateImp destroy");
    }
  }
  
  public boolean checkInBounds()
  {
    return this.r;
  }
  
  public LatLng getNearestLatLng(LatLng paramLatLng)
  {
    if (paramLatLng == null) {
      return null;
    }
    if ((this.points == null) || (this.points.size() == 0)) {
      return null;
    }
    try
    {
      int i1 = 0;
      float f1 = 0.0F;float f2 = 0.0F;
      for (int i2 = 0; i2 < this.points.size(); i2++) {
        if (i2 == 0)
        {
          f1 = MapUtils.calculateLineDistance(paramLatLng,
                  (LatLng) this.points.get(i2));
        }
        else
        {
          f2 = MapUtils.calculateLineDistance(paramLatLng,
                  (LatLng) this.points.get(i2));
          if (f1 > f2)
          {
            f1 = f2;
            i1 = i2;
          }
        }
      }
      return (LatLng)this.points.get(i1);
    }
    catch (Throwable localThrowable)
    {
      SDKLogHandler.exception(localThrowable, "PolylineDelegateImp", "getNearestLatLng");
      
      localThrowable.printStackTrace();
    }
    return null;
  }
  
  public boolean b(LatLng paramLatLng)
  {
//    float[] arrayOfFloat = new float[this.H.length];
//    System.arraycopy(this.H, 0, arrayOfFloat, 0, this.H.length);
//    int i1 = arrayOfFloat.length / 3;
    int i1 = 0;
    if (i1 < 2) {
      return false;
    }
    ArrayList<FPoint> localArrayList = null;
    try
    {
      localArrayList = q();
      if ((localArrayList == null) || (localArrayList.size() < 1)) {
        return false;
      }
    }
    catch (Exception localException)
    {
      return false;
    }
    double d1 = this.layer.mapDelegate.getMapProjection().getMapLenWithWin((int)this.width / 4);
    
    double d2 = this.layer.mapDelegate.getMapProjection().getMapLenWithWin((int)this.K);
    
    FPoint localFPoint1 = c(paramLatLng);
    
    Object localObject = null;
    FPoint localFPoint2 = null;
    for (int i2 = 0; i2 < localArrayList.size() - 1; i2++)
    {
      if (i2 == 0) {
        localObject = (FPoint)localArrayList.get(i2);
      } else {
        localObject = localFPoint2;
      }
      localFPoint2 = (FPoint)localArrayList.get(i2 + 1);
      
      double d3 = a(localFPoint1, (FPoint)localObject, localFPoint2);
      if (d2 + d1 - d3 >= 0.0D)
      {
        localArrayList.clear();
        return true;
      }
    }
    localArrayList.clear();
    
    return false;
  }
  
  private ArrayList<FPoint> q()
  {
    ArrayList<FPoint> localArrayList = new ArrayList<FPoint>();
//    for (int i1 = 0; i1 < this.H.length; i1++)
//    {
//      float f1 = this.H[i1];
//      i1++;
//      float f2 = this.H[i1];
//      i1++;
//      FPoint localFPoint = new FPoint(f1, f2);
//
//      localArrayList.add(localFPoint);
//    }
    return localArrayList;
  }
  
  private double a(FPoint paramFPoint1, FPoint paramFPoint2, FPoint paramFPoint3)
  {
    return a(paramFPoint1.x, paramFPoint1.y, paramFPoint2.x, paramFPoint2.y, paramFPoint3.x, paramFPoint3.y);
  }
  
  private FPoint c(LatLng paramLatLng)
  {
    IPoint localIPoint = new IPoint();
    this.layer.mapDelegate.latlon2Geo(paramLatLng.latitude, paramLatLng.longitude, localIPoint);
    FPoint localFPoint = new FPoint();
    this.layer.mapDelegate.geo2Map(localIPoint.y, localIPoint.x, localFPoint);
    
    return localFPoint;
  }
  
  private double a(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6)
  {
    double d1 = (paramDouble5 - paramDouble3) * (paramDouble1 - paramDouble3) + (paramDouble6 - paramDouble4) * (paramDouble2 - paramDouble4);
    if (d1 <= 0.0D) {
      return Math.sqrt((paramDouble1 - paramDouble3) * (paramDouble1 - paramDouble3) + (paramDouble2 - paramDouble4) * (paramDouble2 - paramDouble4));
    }
    double d2 = (paramDouble5 - paramDouble3) * (paramDouble5 - paramDouble3) + (paramDouble6 - paramDouble4) * (paramDouble6 - paramDouble4);
    if (d1 >= d2) {
      return Math.sqrt((paramDouble1 - paramDouble5) * (paramDouble1 - paramDouble5) + (paramDouble2 - paramDouble6) * (paramDouble2 - paramDouble6));
    }
    double d3 = d1 / d2;
    double d4 = paramDouble3 + (paramDouble5 - paramDouble3) * d3;
    double d5 = paramDouble4 + (paramDouble6 - paramDouble4) * d3;
    return Math.sqrt((paramDouble1 - d4) * (paramDouble1 - d4) + (d5 - paramDouble2) * (d5 - paramDouble2));
  }
  
  public void setTransparency(float transparency)
  {
    this.transparency = transparency;
    this.layer.mapDelegate.setRunLowFrame(false);
  }
  
  public void setTextureList(List<BitmapDescriptor> paramList)
  {
    if ((paramList == null) || (paramList.size() == 0)) {
      return;
    }
    if (paramList.size() > 1)
    {
      this.n = false;
      this.drawType = 5;
      this.f = paramList;
      this.layer.mapDelegate.setRunLowFrame(false);
    }
    else
    {
      a((BitmapDescriptor)paramList.get(0));
    }
  }
  
  public void setTextureIndex(List<Integer> paramList)
  {
    if ((paramList == null) || (paramList.size() == 0)) {
      return;
    }
    this.g = g(paramList);
  }
  //多个颜色
  public void e(List<Integer> paramList)
  {
    if ((paramList == null) || (paramList.size() == 0)) {
      return;
    }
    if (paramList.size() > 1)
    {
      this.n = false;
      this.h = g(paramList);
      this.drawType = 3;
      this.layer.mapDelegate.setRunLowFrame(false);
    }
    else
    {
      setColor(((Integer)paramList.get(0)).intValue());
    }
  }
  //是否渐变
  public void e(boolean paramBoolean)
  {
    if ((paramBoolean) && (this.h != null) && (this.h.size() > 1))
    {
      this.t = paramBoolean;
      this.drawType = 4;
      this.layer.mapDelegate.setRunLowFrame(false);
    }
  }
  
  private List<Integer> g(List<Integer> paramList)
  {
    int[] arrayOfInt = new int[paramList.size()];
    ArrayList<Integer> localArrayList = new ArrayList<Integer>();
    int i1 = 0;int i2 = 0;int i3 = 0;
    for (int i4 = 0; i4 < paramList.size(); i4++)
    {
      i2 = ((Integer)paramList.get(i4)).intValue();
      if (i4 == 0)
      {
        localArrayList.add(Integer.valueOf(i2));
      }
      else
      {
        if (i2 == i1) {
          continue;
        }
        localArrayList.add(Integer.valueOf(i2));
      }
      arrayOfInt[i3] = i4;
      i1 = i2;
      i3++;
    }
    this.I = new int[localArrayList.size()];
    System.arraycopy(arrayOfInt, 0, this.I, 0, this.I.length);
    return localArrayList;
  }
}
