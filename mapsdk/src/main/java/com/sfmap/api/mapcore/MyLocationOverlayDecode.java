package com.sfmap.api.mapcore;

import android.content.Context;
import android.location.Location;
import android.os.RemoteException;

import com.sfmap.api.mapcore.util.SDKLogHandler;
import com.sfmap.api.maps.model.BitmapDescriptorFactory;
import com.sfmap.api.maps.model.CircleOptions;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.maps.model.Marker;
import com.sfmap.api.maps.model.MarkerOptions;
import com.sfmap.api.maps.model.MyLocationStyle;
import com.sfmap.mapcore.IPoint;
import com.sfmap.mapcore.MapProjection;

class MyLocationOverlayDecode {
    private IMapDelegate map; // a
    private Marker point; //b
    private ICircleDelegate c; //c
    private MyLocationStyle d; // d
    private LatLng e; //e
    private double f; //f
    private Context context; //g
    private SensorEventHelperDecode h; //h
    private int i = 1;  //i
    private boolean j = false; //j
    private final String k = "my_location_car.png"; //k
    private final String l = "my_location_car_3d.png"; //l
    private boolean m = false; //m

    MyLocationOverlayDecode(IMapDelegate paramaa, Context paramContext) {
        this.context = paramContext;
        this.map = paramaa;
        this.h = new SensorEventHelperDecode(this.context, paramaa);
    }

    public void setMyLocationStyle(MyLocationStyle paramMyLocationStyle) {
        try {
            this.d = paramMyLocationStyle;
            if ((this.point == null) && (this.c == null)) {
                return;
            }
            l();
            this.h.a(this.point);
            k();
        } catch (Throwable localThrowable) {
            SDKLogHandler.exception(localThrowable, "MyLocationOverlay", "setMyLocationStyle");
            localThrowable.printStackTrace();
        }
    }

    public void a(int paramInt) {
        this.i = paramInt;
        this.j = false;
        switch (this.i) {
            case 1:
                g();
                break;
            case 2:
                h();
                break;
            case 3:
                i();
                break;
        }
    }

    public void a() {
        if (this.h != null) {
            this.h.b();
        }
    }

    public void b() {
        if ((this.i == 3) && (this.h != null)) {
            this.h.a();
        }
    }

    private void g() {
        if (this.point != null) {
            c(0.0F);
            this.h.b();
            if (!this.m) {
                this.point.setIcon(BitmapDescriptorFactory.fromAsset("my_location_car.png"));
            }
            this.point.setFlat(false);
            b(0.0F);
        }
    }

    private void h() {
        if (this.point != null) {
            c(0.0F);
            this.h.b();
            if (!this.m) {
                this.point.setIcon(BitmapDescriptorFactory.fromAsset("my_location_car.png"));
            }
            this.point.setFlat(false);
            b(0.0F);
        }
    }

    private void i() {
        if (this.point != null) {
//      this.point.setRotateAngle(0.0F);
            this.h.a();
            if (!this.m) {
                this.point.setIcon(BitmapDescriptorFactory.fromAsset("my_location_car_3d.png"));
            }
            this.point.setFlat(true);
            try {
                this.map.moveCamera(CameraUpdateFactoryDelegate.zoomTo(17.0F));
                b(45.0F);
            } catch (RemoteException localRemoteException) {
                localRemoteException.printStackTrace();
            }
        }
    }

    public void unregisterSensorListener(){
        this.h.b();//解绑传感器，不让地图跟随旋转
    }

    /**
     * 当用户在跟随模式下移动地图，仿照高德地图操作，保留3D视图，但取消了跟随模式，且地图不再旋转
     */
    public void setCustomType() {
        this.h.b();//解绑传感器，不让地图跟随旋转
        if (this.point != null) {
            if (!this.m) {
                this.point.setIcon(BitmapDescriptorFactory.fromAsset("my_location_car_3d.png"));
            }
            this.point.setFlat(false);
            try {
                this.map.moveCamera(CameraUpdateFactoryDelegate.zoomTo(17.0F));
                b(45.0F);//立体展示地图
            } catch (RemoteException localRemoteException) {
                localRemoteException.printStackTrace();
            }
        }
    }

    private void b(float paramFloat) {
        if (this.map == null) {
            return;
        }
        try {
            this.map.moveCamera(CameraUpdateFactoryDelegate.changeTilt(paramFloat));
        } catch (RemoteException localRemoteException) {
            localRemoteException.printStackTrace();
        }
    }

    private void c(float paramFloat) {
        if (this.map == null) {
            return;
        }
        try {
            this.map.moveCamera(CameraUpdateFactoryDelegate.changeBearing(paramFloat));
        } catch (RemoteException localRemoteException) {
            localRemoteException.printStackTrace();
        }
    }

    public void setCentAndRadius(Location paramLocation) {
        if (paramLocation == null) {
            return;
        }
        this.e = new LatLng(paramLocation.getLatitude(), paramLocation.getLongitude());

        this.f = paramLocation.getAccuracy();
        if ((this.point == null) && (this.c == null)) {
            k();
        }
        if (this.point != null) {
            this.point.setPosition(this.e);
        }
        if (this.c != null) {
            try {
                this.c.setCenter(this.e);
                if (this.f != -1.0D) {
                    this.c.setRadius(this.f);
                }
            } catch (RemoteException localRemoteException) {
                SDKLogHandler.exception(localRemoteException, "MyLocationOverlay", "setCentAndRadius");
                localRemoteException.printStackTrace();
            }
            j();
            if (this.i != 3) {
//        b(paramLocation);
            }
            this.j = true;
        }
    }

    private void b(Location paramLocation) {
        float f1 = paramLocation.getBearing();

        f1 %= 360.0F;
        if (f1 > 180.0F) {
            f1 -= 360.0F;
        } else if (f1 < -180.0F) {
            f1 += 360.0F;
        }
        if (this.point != null) {
            this.point.setRotateAngle(-f1);
        }
    }

    private void j() {
        if ((this.i == 1) && (this.j)) {
            return;
        }
        try {
            IPoint localIPoint = new IPoint();
            if(this.map.getNeedToCenter()){
                MapProjection.lonlat2Geo(this.e.longitude, this.e.latitude, localIPoint);
                this.map.animateCamera(CameraUpdateFactoryDelegate.changeGeoCenter(localIPoint));
            }
        } catch (RemoteException localRemoteException) {
            SDKLogHandler.exception(localRemoteException, "MyLocationOverlay", "locaitonFollow");
            localRemoteException.printStackTrace();
        }
    }

    private void k() {
        if (this.d == null) {
            this.d = new MyLocationStyle();
            this.d.myLocationIcon(BitmapDescriptorFactory.fromAsset("my_location_car.png"));
            m();
        } else {
            this.m = true;
            m();
        }
    }

    public void remove()  //c
            throws RemoteException {
        l();
        if (this.h != null) {
            this.h.b();
            this.h = null;
        }
    }

    private void l() {
        if (this.c != null) {
            try {
                this.map.removeGLOverlay(this.c.getId());
            } catch (RemoteException localRemoteException) {
                SDKLogHandler.exception(localRemoteException, "MyLocationOverlay", "locationIconRemove");
                localRemoteException.printStackTrace();
            }
            this.c = null;
        }
        if (this.point != null) {
            this.point.remove();
            this.point.destroy();
            this.point = null;
            //this.h.a(null);
            this.h.a((Marker) null);
        }
    }

    private void m() {
        try {
            this.c = this.map.addCircle(new CircleOptions()
                    .strokeWidth(this.d.getStrokeWidth())
                    .fillColor(this.d.getRadiusFillColor())
                    .strokeColor(this.d.getStrokeColor())
                    .center(new LatLng(0.0D, 0.0D)).zIndex(1.0F));
            if (this.e != null) {
                this.c.setCenter(this.e);
            }
            this.c.setRadius(this.f);
            this.point = this.map.addMarker(new MarkerOptions().visible(false)
                    .anchor(this.d.getAnchorU(), this.d.getAnchorV())
                    .icon(this.d.getMyLocationIcon())
                    .position(new LatLng(0.0D, 0.0D)));
            a(this.i);
            if (this.e != null) {
                this.point.setPosition(this.e);
                this.point.setVisible(true);
            }
            this.h.a(this.point);
        } catch (RemoteException localRemoteException) {
            SDKLogHandler.exception(localRemoteException, "MyLocationOverlay", "myLocStyle");
            localRemoteException.printStackTrace();
        }
    }

    public void setRotateAngle(float paramFloat) {
        if (this.point != null) {
            this.point.setRotateAngle(paramFloat);
        }
    }

    public String getId() {
        if (this.point != null) {
            return this.point.getId();
        }
        return null;
    }

    public String e()
            throws RemoteException {
        if (this.c != null) {
            return this.c.getId();
        }
        return null;
    }

    public void f() {
        this.c = null;
        this.point = null;
    }
}
