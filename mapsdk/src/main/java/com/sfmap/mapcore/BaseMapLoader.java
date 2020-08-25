package com.sfmap.mapcore;

import android.text.TextUtils;
import android.util.Log;

import com.sfmap.api.mapcore.util.AppInfo;
import com.sfmap.api.mapcore.util.LogManager;
import com.sfmap.api.maps.DesUtil;
import com.sfmap.api.maps.MapsInitializer;
import com.sfmap.api.maps.model.MobileBean;
import com.sfmap.api.maps.model.TmcBean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

public abstract class BaseMapLoader {
    private String TAG = "BaseMapLoader";
    public final static int DATA_SOURCE_TYPE_DATA_ROAD = 0; //基础道路图层
    public final static int DATA_SOURCE_TYPE_DATA_GEO_BUILDING = 1; //楼块地图数据
    public final static int DATA_SOURCE_TYPE_DATA_BMP_BASEMAP = 2; //v3 map //基础栅格地图数据 <12级使用
    public final static int DATA_SOURCE_TYPE_DATA_SATELLITE = 3; //卫星影像数据
    public final static int DATA_SOURCE_TYPE_DATA_VEC_TMC = 4; //矢量实时交通vmap数据
    public final static int DATA_SOURCE_TYPE_DATA_SCREEN = 5; //当前屏幕图块
    public final static int DATA_SOURCE_TYPE_DATA_3DOBJECT = 6; //当前屏幕需要的3dobject
    public final static int DATA_SOURCE_TYPE_DATA_STANDARD = 7; //v4 new data type
    public final static int DATA_SOURCE_TYPE_DATA_POI = 8;  //V4POI基础数据
    public final static int DATA_SOURCE_TYPE_DATA_VERSION = 9;  // 版本查询
    public final static int DATA_SOURCE_TYPE_DATA_INDOOR = 10;  // 室内地图数据
    public final static int DATA_SOURCE_TYPE_DATA_STREETVIEW = 11;//街景路网
    public final static int DATA_SOURCE_TYPE_DATA_BMP_USER_GIRDSMAP = 13;//客户栅格地图数据

    MapCore mapCore;
    BaseMapCallImplement mMapCallback;
    public ArrayList<MapSourceGridData> mapTiles = new ArrayList<MapSourceGridData>();
    long createtime;
    byte[] recievedDataBuffer;
    byte[] recievedDataBufferBackup = null;
    int recievedDataSize = 0;
    int receivedDataSizeBackup = 0;
    int nextImgDataLength = 0;
    boolean recievedHeader = false;
    int mCapaticy = 30720;
    int mCapaticyExt = 10240;
    volatile boolean inRequest = false;
    volatile boolean isFinished = false;
    volatile boolean mCanceled = false;
    int datasource = 0;
    long m_reqestStartLen;
    public HttpURLConnection httpURLConnection = null;

    protected abstract String getMapSvrPath();

    protected abstract String getGridParma();

    public abstract boolean isRequestValid();

    protected abstract String getMapAddress();

    protected abstract boolean processReceivedDataHeader(int paramInt);

    protected abstract boolean isNeedProcessReturn();

    protected abstract void processRecivedVersionOrScenicWidgetData();

    protected abstract void processRecivedDataByType();

    protected abstract String getUserGridURL(String mesh);

    protected void processReceivedTileDataV4(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
        int i = paramInt1;

        paramInt1 += 4;

        int j = paramArrayOfByte[(paramInt1++)];

        String str = "";
        if ((j > 0) && (paramInt1 + j - 1 < paramInt2)) {
            str = new String(paramArrayOfByte, paramInt1, j);
        }
        paramInt1 += j;
        if (!this.mapCore.isMapEngineValid()) {
            return;
        }
        if (paramInt2 <= i) {
            return;
        }
        int k = 0;

        k = !this.mMapCallback.isGridInScreen(this.datasource, str) ? 1 : 0;
        if (this.mapCore.putMapData(paramArrayOfByte, i, paramInt2 - i, this.datasource, 0)) {
            VMapDataCache.getInstance().putRecoder(null, str, this.datasource);
        }
        if (k != 0) {
            LogManager.writeLog(LogManager.productInfo, hashCode() + " tile is out of screen, tileName: " + str, 115);

            doCancel();
        }
    }

    protected String getURL(String address, String svrPath, String param) {
        String str2 = "";

        String str1 = "";
        str1 = param;
        str2 = address + svrPath + str1;
        return str2;
    }

    protected void initTestTime() {
        this.m_reqestStartLen = System.currentTimeMillis();
    }

    protected void privteTestTime(String paramString1, String paramString2) {
    }

    protected boolean isAssic(String paramString) {
        if (paramString == null) {
            return false;
        }
        char[] arrayOfChar = paramString.toCharArray();
        for (int i = 0; i < arrayOfChar.length; i++) {
            if ((arrayOfChar[i] >= 'Ā') || (arrayOfChar[i] <= 0)) {
                return false;
            }
        }
        return true;
    }

    protected boolean containllegal(String paramString) {
        if ((paramString.contains("<")) || (paramString.contains("["))) {
            return true;
        }
        return false;
    }

    public void OnException(int paramInt) {
        privteTestTime("", " network error:" + paramInt);

        LogManager.writeLog(LogManager.productInfo, hashCode() + "MapLoader Exception happened error code: " + paramInt, 115);
        this.isFinished = true;
        if ((this.datasource != 6) && (this.datasource != 4) && (this.datasource != 1)) {
            if (!this.mCanceled) {
            }
        }
        this.isFinished = true;
    }

    public synchronized boolean hasFinished() {
        return (this.mCanceled) || (this.isFinished);
    }

    public synchronized void doCancel() {
        LogManager.writeLog(LogManager.productInfo, this.hashCode() + " MapLoader cancel download thread id: " + Thread.currentThread().getId(), 111);
        if (!this.mCanceled && !this.isFinished) {
            this.mCanceled = true;

            try {
                if (this.httpURLConnection != null && this.inRequest) {
                    this.httpURLConnection.disconnect();
                }
            } catch (Throwable var5) {
                ;
            } finally {
                this.onConnectionOver();
            }

        }
    }


    private synchronized void onConnectionOver() {
        processRecivedVersionOrScenicWidgetData();
        this.recievedDataBuffer = null;
        this.nextImgDataLength = 0;
        this.recievedDataSize = 0;
        try {
            for (int i = 0; i < this.mapTiles.size(); i++) {
                this.mMapCallback.tileProcessCtrl.remove(((MapSourceGridData) this.mapTiles.get(i)).keyGridName);
            }
        } catch (Exception localException) {
        }
        this.isFinished = true;
    }

    public void doRequest() {
        if ((this.mCanceled) || (this.isFinished)) {
            return;
        }
        if (!isRequestValid()) {
            LogManager.writeLog(LogManager.productInfo, hashCode() + " request is invalid, cancel download", 115);
            doCancel();
            return;
        }
        if (!MapsInitializer.getNetWorkEnable()) {
            return;
        }
        String address = null;
        InputStream localInputStream = null;
        String param = null;

        address = getMapAddress();

        if (address == null) {
            return;
        }

        param = getGridParma();
        String[] par = param.split("&");
        String[] type = par[1].split("type=");
        String[] mesh = par[2].split("mesh=");
        if (TextUtils.isEmpty(param)) {
            return;
        }
        this.inRequest = true;
        try {
            String url = "";
            try {

                if (datasource == DATA_SOURCE_TYPE_DATA_VEC_TMC) {
                    String meshes = "";
                    for (int i = 0; i < mapTiles.size(); ++i) {
                        if (i != 0) {
                            meshes += ";";
                        }
                        meshes += mapTiles.get(i).getGridName();
                    }
                    TmcBean tmcBean = new TmcBean();
                    String packageName = AppInfo.getPackageName(mapCore.getContext());
                    String Sha1 = AppInfo.getSHA1(mapCore.getContext());
                    String ak = AppInfo.getAppMetaApikey(mapCore.getContext());

                    tmcBean.setMesh(meshes);
                    tmcBean.setAppPackageName(packageName);
                    tmcBean.setAppCerSha1(Sha1);

//                    tmcBean.setAppPackageName("com.sfmap.api.mobile.demo");
//                    tmcBean.setAppCerSha1("D7:F2:9D:15:B8:7D:17:14:3C:B8:EB:96:EB:DA:52:D5:D9:C0:46:63");
//                    String ak ="1cb63da24a7d4e48bb827dd4eb64c25e";
                    String params = new DesUtil().encrypt(tmcBean.toString());
//                    url = AppInfo.getSfTmcURL(mapCore.getContext()) + "?param="+ params +"&ak="+ak;
                    url = AppInfo.getSfTmcURL(mapCore.getContext()) + "?mesh=" + meshes;
                    Log.d("doRequest", "实时交通请求地址url:" + url);
                    Log.d("doRequest", "实时交通packageName:" + packageName);
                } else {
                    MobileBean mobileBean = new MobileBean();
                    mobileBean.setT("VMMV4");
                    mobileBean.setType(type[1]);
                    mobileBean.setMesh(mesh[1]);
                    String packageName = AppInfo.getPackageName(mapCore.getContext());
                    String Sha1 = AppInfo.getSHA1(mapCore.getContext());
                    mobileBean.setAppPackageName(packageName);
                    mobileBean.setAppCerSha1(Sha1);
                    String ak = AppInfo.getAppMetaApikey(mapCore.getContext());

//                    mobileBean.setAppPackageName("com.ch.cs_collectiontool");
//                    mobileBean.setAppCerSha1("D7:F2:9D:15:B8:7D:17:14:3C:B8:EB:96:EB:DA:52:D5:D9:C0:46:63");
//                    String ak ="e2bcef63bcda45f1a66346b34465f64e";
                    String para = mobileBean.toString();
                    String param1 = DesUtil.getInstance().encrypt(para);
                    url = AppInfo.getSfMapURL(mapCore.getContext()) + "?" + "param=" + param1 + "&ak=" + ak;
                    Log.d("doRequest", "地图数据服务,地址url:" + url.substring(0,50));
                    Log.d("doRequest", "packageName:" + packageName + " \nSha1:" + Sha1 + " \nak:" + ak);
                    //内部高精版地图数据请求
//                    if("1".equals(AppInfo.getNormalOrOrder(mapCore.getContext()))){
//                        url = getURL(AppInfo.getOrderMapURL(mapCore.getContext()) + "?", "", param);
//                    }
                }
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            }
            Proxy localProxy = null;//ProxyUtil.getProxy(this.mMapCallback.getContext());
//            String url = null;
//            url = getURL(AppInfo.getSfMapURL(mapCore.getContext())+"?", "", param);
//            Log.i(TAG,"debug-url:"+url +" mCanceled:"+this.mCanceled);
            if (localProxy != null) {
                this.httpURLConnection = ((HttpURLConnection) new URL(url).openConnection(localProxy));
            } else {
                this.httpURLConnection = ((HttpURLConnection) new URL(url).openConnection());
            }
            this.httpURLConnection.setConnectTimeout(20000);
            this.httpURLConnection.setRequestMethod("GET");

            LogManager.writeLog(LogManager.productInfo, hashCode() + " MapLoader doRequest threadId: " + Thread.currentThread().getId() + " url:" + param, 111);

//            int i = 1;
            if (this.httpURLConnection != null) {
                this.httpURLConnection.connect();
                if (this.httpURLConnection.getResponseCode() == 200) {
                    localInputStream = this.httpURLConnection.getInputStream();

                    onConnectionOpened();
                    byte[] arrayOfByte = new byte[512];
                    int j = -1;
                    while ((j = localInputStream.read(arrayOfByte)) > -1) {
//                        if (i != 0) {
//                            privteTestTime("recievedFirstByte:", "");
//                            i = 0;
//                        }
                        if (this.mCanceled) {
                            break;
                        }
                        onConnectionRecieveData(arrayOfByte, j);
                    }
                    if (isNeedProcessReturn()) {
                        return;
                    }
                    if ((!this.recievedHeader) &&
                            (!processReceivedDataHeader(this.recievedDataSize))) {
                        return;
                    }
                    processRecivedDataByType();
                } else {
                    OnException(1002);
                }
            } else {
                OnException(1002);
            }
            return;
        } catch (IllegalArgumentException localIllegalArgumentException) {
        } catch (SecurityException localSecurityException) {
        } catch (OutOfMemoryError localOutOfMemoryError) {
        } catch (IllegalStateException localIllegalStateException) {
        } catch (IOException localIOException6) {
            OnException(1002);
        } catch (NullPointerException localNullPointerException) {
        } finally {
            onConnectionOver();
            if ((localInputStream != null) && (!this.mCanceled)) {
                try {
                    localInputStream.close();
                } catch (IOException localIOException9) {
                    OnException(1002);
                }
            }
            disConnectHttpConnection();
        }
    }

    private void disConnectHttpConnection() {
        if (this.httpURLConnection != null) {
            this.httpURLConnection.disconnect();
            this.httpURLConnection = null;
        }
    }

    public void doGridRequest() {
        if ((this.mCanceled) || (this.isFinished)) {
            return;
        }
        if (!isRequestValid()) {
            LogManager.writeLog(LogManager.productInfo, hashCode() + " request is invalid, cancel download", 115);
            doCancel();
            return;
        }
        if (!MapsInitializer.getNetWorkEnable()) {
            return;
        }

        InputStream localInputStream = null;

        String param = null;
        this.inRequest = true;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            Proxy localProxy = null;//ProxyUtil.getProxy(this.mMapCallback.getContext());
            String gridName = mapTiles.get(0).getGridName();
            String url = getUserGridURL(gridName);
            if (localProxy != null) {
                this.httpURLConnection = ((HttpURLConnection) new URL(url).openConnection(localProxy));
            } else {
                this.httpURLConnection = ((HttpURLConnection) new URL(url).openConnection());
            }
            this.httpURLConnection.setConnectTimeout(20000);
            this.httpURLConnection.setRequestMethod("GET");

            LogManager.writeLog(LogManager.productInfo, hashCode() + " MapLoader doRequest threadId: " + Thread.currentThread().getId() + " url:" + param, 111);

//      int i = 1;
            if (this.httpURLConnection != null) {
                this.httpURLConnection.connect();
                if (this.httpURLConnection.getResponseCode() == 200) {
                    localInputStream = this.httpURLConnection.getInputStream();
                    byteArrayOutputStream = new ByteArrayOutputStream();
                    onConnectionOpened();
                    byte[] tempbyte = new byte[512];
                    int len = -1;
                    while ((len = localInputStream.read(tempbyte)) > -1) {

                        if (this.mCanceled) {
                            break;
                        }
                        byteArrayOutputStream.write(tempbyte, 0, len);
                    }
                    this.recievedDataBuffer = byteArrayOutputStream.toByteArray();
                    onRecieveData(byteArrayOutputStream.toByteArray(), gridName.getBytes(), gridName);
                } else {
                    OnException(1002);
                }
            } else {
                OnException(1002);
            }
            return;
        } catch (IllegalArgumentException localIllegalArgumentException) {
        } catch (SecurityException localSecurityException) {
        } catch (OutOfMemoryError localOutOfMemoryError) {
        } catch (IllegalStateException localIllegalStateException) {
        } catch (IOException localIOException6) {
            OnException(1002);
        } catch (NullPointerException localNullPointerException) {
        } finally {
            onConnectionOver();
            if ((localInputStream != null) && (!this.mCanceled)) {
                try {
                    localInputStream.close();
                } catch (IOException localIOException9) {
                    OnException(1002);
                }
            }
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (Exception ex) {

                }
            }
        }
    }

    public void onConnectionError(BaseMapLoader paramBaseMapLoader, int paramInt, String paramString) {
    }

    protected void onConnectionOpened() {
        this.recievedDataBuffer = new byte[this.mCapaticy];
        this.nextImgDataLength = 0;
        this.recievedDataSize = 0;
        this.recievedHeader = false;
    }

    public void addReuqestTiles(MapSourceGridData paramMapSourceGridData) {
        this.mapTiles.add(paramMapSourceGridData);
    }

    private void onRecieveData(byte[] mapdata, byte gridNameByte[], String gridName) {

        try {
            int headLength = 5, gridNameLength = gridNameByte.length;
            headLength += gridNameLength;
            recievedDataBuffer = new byte[headLength + mapdata.length];
            recievedDataBuffer[0] = 0;
            recievedDataBuffer[1] = 0;
            recievedDataBuffer[2] = 0;
            recievedDataBuffer[3] = 0;

            recievedDataBuffer[4] = (byte) gridNameLength;
            System.arraycopy(gridNameByte, 0, recievedDataBuffer, 5, gridNameLength);
            System.arraycopy(mapdata, 0, this.recievedDataBuffer, headLength, mapdata.length);
            this.recievedDataSize = this.recievedDataBuffer.length;
        } catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {
            doCancel();
            LogManager.writeLog(LogManager.productInfo, hashCode() + " MapLoader Exception happened, " + "cancel download, Exception Message: " + localArrayIndexOutOfBoundsException
                    .getMessage(), 115);
            return;
        } catch (Exception localException) {
            doCancel();
            LogManager.writeLog(LogManager.productInfo, hashCode() + " MapLoader Exception happened, " + "cancel download, Exception Message: " + localException
                    .getMessage(), 115);
            return;
        }

        try {
            if (!this.mapCore.isMapEngineValid()) {
                return;
            }
            int k = !this.mMapCallback.isGridInScreen(this.datasource, gridName) ? 1 : 0;
            if (this.mapCore.putMapData(this.recievedDataBuffer, 0, this.recievedDataSize, this.datasource, 0)) {
                VMapDataCache.getInstance().putRecoder(null, gridName, this.datasource);
            }
            if (k != 0) {
                doCancel();
            }
        } catch (Exception ex) {
            doCancel();
            return;
        }
    }

    private void onConnectionRecieveData(byte[] mapdata, int dataLength) {
        if (this.mCapaticy < this.recievedDataSize + dataLength) {
            try {
                this.mCapaticy += this.mCapaticyExt;
                byte[] arrayOfByte = new byte[this.mCapaticy];

                System.arraycopy(this.recievedDataBuffer, 0, arrayOfByte, 0, this.recievedDataSize);

                this.recievedDataBuffer = arrayOfByte;
            } catch (OutOfMemoryError localOutOfMemoryError) {
                doCancel();
                LogManager.writeLog(LogManager.productInfo, hashCode() + " MapLoader Exception happened, " + "cancel download, Exception Message: " + localOutOfMemoryError
                        .getMessage(), 115);
                return;
            }
        }
        try {
            System.arraycopy(mapdata, 0, this.recievedDataBuffer, this.recievedDataSize, dataLength);

            if (this.datasource == DATA_SOURCE_TYPE_DATA_VERSION) {
                this.recievedDataBufferBackup = new byte[dataLength];

                System.arraycopy(mapdata, 0, this.recievedDataBufferBackup, 0, dataLength);
                this.receivedDataSizeBackup = dataLength;
            }

        } catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {
            doCancel();
            LogManager.writeLog(LogManager.productInfo, hashCode() + " MapLoader Exception happened, " + "cancel download, Exception Message: " + localArrayIndexOutOfBoundsException
                    .getMessage(), 115);
            return;
        } catch (Exception localException) {
            doCancel();
            LogManager.writeLog(LogManager.productInfo, hashCode() + " MapLoader Exception happened, " + "cancel download, Exception Message: " + localException
                    .getMessage(), 115);
            return;
        }
        this.recievedDataSize += dataLength;
//        if (isNeedProcessReturn()) {
//            return;
//        }
//        if ((!this.recievedHeader) &&
//                (!processReceivedDataHeader(dataLength))) {
//            return;
//        }
//        processRecivedDataByType();
    }

    protected void processRecivedData() {
        if (this.nextImgDataLength == 0) {
            if (this.recievedDataSize >= 8) {
                this.nextImgDataLength = (Convert.getInt(this.recievedDataBuffer, 0) + 8);

                processRecivedData();
            }
        } else if (this.recievedDataSize >= this.nextImgDataLength) {
            int i = Convert.getInt(this.recievedDataBuffer, 0);
            int j = Convert.getInt(this.recievedDataBuffer, 4);
            if (j == 0) {
                processRecivedTileData(this.recievedDataBuffer, 8, i + 8);
            } else {
                try {
                    ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(this.recievedDataBuffer, 8, i);

                    GZIPInputStream localGZIPInputStream = new GZIPInputStream(localByteArrayInputStream);

                    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
                    byte[] arrayOfByte1 = new byte[''];
                    int k = -1;
                    while ((k = localGZIPInputStream.read(arrayOfByte1)) > -1) {
                        localByteArrayOutputStream.write(arrayOfByte1, 0, k);
                    }
                    byte[] arrayOfByte2 = localByteArrayOutputStream.toByteArray();
                    processRecivedTileData(arrayOfByte2, 0, j);
                } catch (Exception localException) {
                    localException.printStackTrace();
                }
            }
            if (this.nextImgDataLength > 0) {
                Convert.moveArray(this.recievedDataBuffer, this.nextImgDataLength, this.recievedDataBuffer, 0, this.recievedDataSize - this.nextImgDataLength);
            }
            this.recievedDataSize -= this.nextImgDataLength;
            this.nextImgDataLength = 0;
            processRecivedData();
        }
    }

    protected void processReceivedDataV4() {
        if (this.nextImgDataLength == 0) {
            if (this.recievedDataSize >= 8) {
                this.nextImgDataLength = (Convert.getInt(this.recievedDataBuffer, 0) + 8);

                processReceivedDataV4();
            }
        } else if (this.recievedDataSize >= this.nextImgDataLength) {
            processReceivedTileDataV4(this.recievedDataBuffer, 8, this.nextImgDataLength);

            Convert.moveArray(this.recievedDataBuffer, this.nextImgDataLength, this.recievedDataBuffer, 0, this.recievedDataSize - this.nextImgDataLength);

            this.recievedDataSize -= this.nextImgDataLength;
            this.nextImgDataLength = 0;
            processReceivedDataV4();
        }
    }

    void processRecivedTileData(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
        int i = paramInt1;

        paramInt1 += 2;

        paramInt1 += 2;

        paramInt1 += 4;
        int j = paramArrayOfByte[(paramInt1++)];

        String str = "";
        if ((j > 0) && (paramInt1 + j - 1 < paramInt2)) {
            str = new String(paramArrayOfByte, paramInt1, j);
        }
        paramInt1 += j;
        if (!this.mapCore.isMapEngineValid()) {
            return;
        }
        if (paramInt2 <= i) {
            return;
        }
        int k = !this.mMapCallback.isGridInScreen(this.datasource, str) ? 1 : 0;
        VMapDataCache.getInstance().putRecoder(null, str, this.datasource);
        this.mapCore.putMapData(paramArrayOfByte, i, paramInt2 - i, this.datasource, 0);
        if (k != 0) {
            LogManager.writeLog(LogManager.productInfo, hashCode() + " tile is out of screen, tileName: " + str, 115);
            doCancel();
        }
    }

    void processRecivedVersionData(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
        if ((0 < paramInt2) && (paramInt2 <= paramArrayOfByte.length)) {
            int i = 0;
            int j = Convert.getInt(paramArrayOfByte, i);
            i += 4;
            if (0 != j) {
                return;
            }
            int k = Convert.getInt(paramArrayOfByte, i);
            i += 4;
            if (0 != k) {
                return;
            }
            int m = Convert.getInt(paramArrayOfByte, i);
            i += 4;

            int n = 1;
            ArrayList<String> localArrayList = new ArrayList<String>();
            for (int i1 = 0; i1 < m; i1++) {
                int i2 = 0;
                String str2 = "";
                if (i < paramInt2) {
                    i2 = paramArrayOfByte[(i++)];
                } else {
                    n = 0;
                    break;
                }
                if ((i2 > 0) && (i + i2 < paramInt2)) {
                    str2 = new String(paramArrayOfByte, i, i2);
                    localArrayList.add(str2);
                } else {
                    n = 0;
                    break;
                }
                i += i2;

                i += 4;
            }
            if (n != 0) {
                for (int i1 = 0; i1 < localArrayList.size(); i1++) {
                    String str1 = (String) localArrayList.get(i1);
                    VMapDataCache.getInstance().putRecoder(null, str1, this.datasource);
                }
                this.mapCore.putMapData(paramArrayOfByte, 0, paramInt2, this.datasource, 0);
            }
        }
    }


}
