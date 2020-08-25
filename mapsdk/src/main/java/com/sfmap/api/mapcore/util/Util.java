package com.sfmap.api.mapcore.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.RemoteException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sfmap.api.mapcore.IMapDelegate;
import com.sfmap.api.mapcore.ConfigableConstDecode;
import com.sfmap.api.maps.DesUtil;
import com.sfmap.api.maps.MapException;
import com.sfmap.api.maps.MapsInitializer;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.mapcore.DPoint;
import com.sfmap.mapcore.FPoint;
import com.sfmap.mapcore.FileUtil;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.microedition.khronos.opengles.GL10;

import org.json.JSONException;
import org.json.JSONObject;

/*
 * 工具类
 * 初始化地图本地文件夹 检测网络 单位转换等
 */
public class Util {
    static final int[] a = {4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
    static final double[] b = {7453.642D, 3742.9905D, 1873.333D, 936.89026D, 468.472D, 234.239D, 117.12D, 58.56D, 29.28D, 14.64D, 7.32D, 3.66D, 1.829D, 0.915D, 0.4575D, 0.228D, 0.1144D};
    public static Handler c = new Handler();
    public static float maxzoomlevel = 19F;
    public static float minzoomlevel = 3F;

    public static String  CONFIT_KEY_COMPANY    = "sfmap";
    private static String CONFIG_KEY_OFFLINE    = "com."+CONFIT_KEY_COMPANY+".offline";

    public static String PACKAGE_INFO_API_MAPS      = "com."+CONFIT_KEY_COMPANY+".api.maps";
    public static String PACKAGE_INFO_API_MAPCORE   = "com."+CONFIT_KEY_COMPANY+".api.mapcore";
    public static String PACKAGE_INFO_MAPCORE       = "com."+CONFIT_KEY_COMPANY+".mapcore";

    public static final String MAP_DATA_PATH                   = "data";
    public static final String MAP_DATA_OFFLINE_PATH_NAME      = "off";
    public static final String MAP_DATA_OFFLINE_TEMP_PATH_NAME = "temp";
    public static final String MAP_CACHE_PATH_NAME             = "cache";
    public static final String MAP_TILES_PATH_NAME             = "buff";
    public static final String MAP_RES_EXT_PATH_NAME           = "res";
    public static final String MAP_MAP_ASSETS_NAME             = "map_assets";
    public static final String MAP_CRASH_PATH             = "crash";

    public static final String CONFIG_API_KEY = "com."+CONFIT_KEY_COMPANY+".apikey";

    public static final Boolean AUTH_FOR_SATELLITE = true; //是否为卫片做离线鉴权

    public static final String TMC_PARAMETER = "TMCX";//实时交通取图参数控制

    public static boolean getOfflineStatus(Context context){
        boolean status = false;
        try {
            ApplicationInfo aapinfo = context
                    .getPackageManager().getApplicationInfo(
                            context.getPackageName(), PackageManager.GET_META_DATA);
            if (aapinfo == null||aapinfo.metaData==null) {
                return false;
            }
            status = aapinfo.metaData.getBoolean(CONFIG_KEY_OFFLINE);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return status;
    }

    public static Bitmap fromAsset(Context paramContext, String paramString) // a
    {
        try {
            AssetManager localAssetManager = ResourcesUtilDecode.getSelfAssets(paramContext);
            InputStream localInputStream = localAssetManager.open(paramString);
            Bitmap localBitmap = BitmapFactory.decodeStream(localInputStream);
            localInputStream.close();
            return localBitmap;
        } catch (Throwable localThrowable) {
            SDKLogHandler.exception(localThrowable, "Util", "fromAsset");
        }
        return null;
    }

    public static void a(Drawable paramDrawable) //a
    {
        if (paramDrawable != null) {
            paramDrawable.setCallback(null);
            paramDrawable = null;
        }
    }

    public static String a(String paramString, Object paramObject) //a
    {
        return paramString + "=" + String.valueOf(paramObject);
    }

    public static float checkTilt(float tilt, float zoom) //a
    {
        float newtilt = tilt;
        if (tilt > 40.0F) {
            if (zoom <= 15.0F) {
                newtilt = 40.0F;
            } else if (zoom <= 16.0F) {
                newtilt = 50.0F;
            } else if (zoom <= 17.0F) {
                newtilt = 54.0F;
            } else if (zoom <= 18.0F) {
                newtilt = 57.0F;
            } else {
                newtilt = 60.0F;
            }
        }
        return newtilt;
    }

    public static float checkZoomLevel(float zoomlevel) //a
    {
        if (zoomlevel > maxzoomlevel) {
            zoomlevel = maxzoomlevel;
        } else if (zoomlevel < minzoomlevel) {
            zoomlevel = minzoomlevel;
        }
        return zoomlevel;
    }

    public static FloatBuffer makeFloatBuffer(float[] floatArray) //a
    {
        try {
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(floatArray.length * 4);
            byteBuffer.order(ByteOrder.nativeOrder());
            FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
            floatBuffer.put(floatArray);
            floatBuffer.position(0);
            return floatBuffer;
        } catch (Throwable throwable) {
            SDKLogHandler.exception(throwable, "Util", "makeFloatBuffer1");
            throwable.printStackTrace();
        }
        return null;
    }

    public static FloatBuffer makeFloatBuffer(float[] floats, FloatBuffer buffer) //a
    {
        try {
            buffer.clear();
            buffer.put(floats);
            buffer.position(0);
            return buffer;
        } catch (Throwable throwable) {
            SDKLogHandler.exception(throwable, "Util", "makeFloatBuffer2");
            throwable.printStackTrace();
        }
        return null;
    }

    public static int loadTexture(GL10 gl, Bitmap paramBitmap) //a
    {
        return a(gl, paramBitmap, false);
    }

    public static int a(GL10 gl10, Bitmap paramBitmap, boolean paramBoolean) //a
    {
        return a(gl10, 0, paramBitmap, paramBoolean);
    }

    public static int a(GL10 gl, int paramInt, Bitmap paramBitmap) //a
    {
        return a(gl, paramInt, paramBitmap, false);
    }

    public static int a(GL10 gl, int paramInt, Bitmap paramBitmap, boolean paramBoolean) //a
    {
        paramInt = bindTexture(gl, paramInt, paramBitmap, paramBoolean);
        if (paramBitmap != null) {
            paramBitmap.recycle();
        }
        return paramInt;
    }

    public static int bindTexture(GL10 gl, int paramInt, Bitmap bitmap, boolean paramBoolean) //b
    {
        if ((bitmap == null) || (bitmap.isRecycled())) {
            return 0;
        }
        if (paramInt == 0) {
            int[] arrayOfInt = {0};
            gl.glGenTextures(1, arrayOfInt, 0);
            paramInt = arrayOfInt[0];
        }
        gl.glEnable(GL10.GL_TEXTURE_2D);

        gl.glBindTexture(GL10.GL_TEXTURE_2D, paramInt);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        if (paramBoolean) {
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);

            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
        } else {
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);

            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        }
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        gl.glDisable(GL10.GL_TEXTURE_2D);
        return paramInt;
    }

    public static int pow2(int size) // a
    {
        int small = (int) (Math.log(size) / Math.log(2.0D));
        if ((1 << small) >= size) {
            return 1 << small;
        }
        return 1 << (small + 1);
    }

    public static String joint(String... paramVarArgs) // a
    {
        StringBuilder localStringBuilder = new StringBuilder();
        int i = 0;
        for (String str : paramVarArgs) {
            localStringBuilder.append(str);
            if (i != paramVarArgs.length - 1) {
                localStringBuilder.append(",");
            }
            i++;
        }
        return localStringBuilder.toString();
    }

    public static int a(Object[] paramArrayOfObject) // a
    {
        return Arrays.hashCode(paramArrayOfObject);
    }

    public static Bitmap a(Bitmap paramBitmap, int paramInt1, int paramInt2) // a
    {
        if ((paramBitmap == null) || (paramBitmap.isRecycled())) {
            return null;
        }
        Bitmap localBitmap = Bitmap.createBitmap(paramInt1, paramInt2, paramBitmap
                .hasAlpha() ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas localCanvas = new Canvas(localBitmap);
        Paint localPaint = new Paint();
        localPaint.setAntiAlias(true);
        localPaint.setFilterBitmap(true);
        localCanvas.drawBitmap(paramBitmap, 0.0F, 0.0F, localPaint);
        return localBitmap;
    }

    public static Bitmap zoomBitmap(Bitmap paramBitmap, float paramFloat) // a
    {
        if (paramBitmap == null) {
            return null;
        }
        int i = (int) (paramBitmap.getWidth() * paramFloat);
        int j = (int) (paramBitmap.getHeight() * paramFloat);

        Bitmap localBitmap = Bitmap.createScaledBitmap(paramBitmap, i, j, true);

        return localBitmap;
    }

    public static String getMapRoot(Context context) // a
    {
        if (!Environment.getExternalStorageState().equals("mounted")) {
            return context.getCacheDir().toString() + File.separator;
        }
        File localFile1 = null;
        if ((MapsInitializer.sdcardDir == null) ||
                (MapsInitializer.sdcardDir.equals(""))) {
            File localFile2 = Environment.getExternalStorageDirectory();
            localFile1 = new File(localFile2, CONFIT_KEY_COMPANY);
        } else {
            localFile1 = new File(MapsInitializer.sdcardDir);
        }
        if (!localFile1.exists()) {
            localFile1.mkdirs();
        }
        return localFile1.toString() + File.separator;
    }

    public static String getOfflineMapDataTempPath(Context context)
    {
        String str = getMapRoot(context);
        if (str == null) {
            return null;
        }
        File localFile = new File(str, MAP_DATA_PATH + File.separator + MAP_DATA_OFFLINE_TEMP_PATH_NAME);
        if (!localFile.exists()) {
            localFile.mkdir();
        }
        return localFile.toString() + File.separator;
    }

    public static String distance2Text(int paramInt) //b
    {
        String str;
        if (paramInt < 1000) {
            str = paramInt + "m";
        } else {
            str = paramInt / 1000 + "km";
        }
        return str;
    }

    public static boolean checkNet(Context context) //c
    {
        if (context == null) {
            return false;
        }
        ConnectivityManager localConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (localConnectivityManager == null) {
            return false;
        }
        NetworkInfo localNetworkInfo = localConnectivityManager.getActiveNetworkInfo();
        if (localNetworkInfo == null) {
            return false;
        }
        NetworkInfo.State localState = localNetworkInfo.getState();
        if ((localState == null) || (localState == NetworkInfo.State.DISCONNECTED) || (localState == NetworkInfo.State.DISCONNECTING)) {
            return false;
        }
        return true;
    }

    public static boolean hasFroyo() // a
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() //b
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() //c
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() // d
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static void a(GL10 paramGL10, int paramInt) // a
    {
        paramGL10.glDeleteTextures(1, new int[]{paramInt}, 0);
    }

    public static String decodeAssetResData(InputStream paramInputStream) // a
    {
        String str = null;
        try {
            str = new String(getFileData(paramInputStream), "utf-8");
        } catch (Throwable localThrowable) {
            SDKLogHandler.exception(localThrowable, "Util", "decodeAssetResData");
            localThrowable.printStackTrace();
        }
        return str;
    }

    public static byte[] getFileData(InputStream inputStream) //b
            throws IOException {
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
        byte[] arrayOfByte = new byte['ࠀ'];
        int i = -1;
        while ((i = inputStream.read(arrayOfByte, 0, 2048)) != -1) {
            localByteArrayOutputStream.write(arrayOfByte, 0, i);
        }
        arrayOfByte = null;
        return localByteArrayOutputStream.toByteArray();
    }

    public static String readFile(File paramFile) // a
    {
        StringBuffer localStringBuffer = new StringBuffer();
        FileInputStream localFileInputStream = null;
        BufferedReader localBufferedReader = null;
        try {
            localFileInputStream = new FileInputStream(paramFile);
            localBufferedReader = new BufferedReader(new InputStreamReader(localFileInputStream, "utf-8"));
            String str = null;
            while ((str = localBufferedReader.readLine()) != null) {
                localStringBuffer.append(str);
            }
            try {
                if (localFileInputStream != null) {
                    localFileInputStream.close();
                }
            } catch (IOException localIOException2) {
                localIOException2.printStackTrace();
            } finally {
                if (localBufferedReader != null) {
                    try {
                        localBufferedReader.close();
                    } catch (IOException localIOException11) {
                        localIOException11.printStackTrace();
                    }
                }
            }
            //return localStringBuffer.toString();
        } catch (FileNotFoundException localFileNotFoundException) {
            SDKLogHandler.exception(localFileNotFoundException, "Util", "readFile fileNotFound");
            localFileNotFoundException.printStackTrace();
            try {
                if (localFileInputStream != null) {
                    localFileInputStream.close();
                }
            } catch (IOException localIOException5) {
                localIOException5.printStackTrace();
            } finally {
                if (localBufferedReader != null) {
                    try {
                        localBufferedReader.close();
                    } catch (IOException localIOException12) {
                        localIOException12.printStackTrace();
                    }
                }
            }
        } catch (IOException localIOException7) {
            SDKLogHandler.exception(localIOException7, "Util", "readFile io");
            localIOException7.printStackTrace();
            try {
                if (localFileInputStream != null) {
                    localFileInputStream.close();
                }
            } catch (IOException localIOException9) {
                localIOException9.printStackTrace();
            } finally {
                if (localBufferedReader != null) {
                    try {
                        localBufferedReader.close();
                    } catch (IOException localIOException13) {
                        localIOException13.printStackTrace();
                    }
                }
            }
        } finally {
            try {
                if (localFileInputStream != null) {
                    localFileInputStream.close();
                }
            } catch (IOException ioex) {
                ioex.printStackTrace();
            } finally {
                if (localBufferedReader != null) {
                    try {
                        localBufferedReader.close();
                    } catch (IOException localIOException17) {
                        localIOException17.printStackTrace();
                    }
                }
            }
        }

        return localStringBuffer.toString();
    }

    public static void paseAuthFailurJson(String jsonString) // a
            throws MapException {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            if (jsonObject.has("status")) {
                String str = jsonObject.getString("status");
                if (!str.equals("0")) {
                    throw new MapException(jsonObject.getString("message"));
                }
            }
        } catch (JSONException jsex) {
            SDKLogHandler.exception(jsex, "Util", "paseAuthFailurJson");
            jsex.printStackTrace();
        }
    }

    public static boolean a(LatLng paramLatLng, List<LatLng> paramList) // a
    {
        boolean bool = false;
        double d1 = 1.0E-9D;
        int i = 0;

        double d4 = 180.0D;

        double d2 = paramLatLng.longitude;
        double d3 = paramLatLng.latitude;
        double d5 = paramLatLng.latitude;
        if (paramList.size() < 3) {
            return false;
        }
        if (!((LatLng) paramList.get(0)).equals(paramList.get(paramList.size() - 1))) {
            paramList.add(paramList.get(0));
        }
        for (int j = 0; j < paramList.size() - 1; j++) {
            double d6 = ((LatLng) paramList.get(j)).longitude;
            double d7 = ((LatLng) paramList.get(j)).latitude;
            double d8 = ((LatLng) paramList.get(j + 1)).longitude;
            double d9 = ((LatLng) paramList.get(j + 1)).latitude;
            if (b(d2, d3, d6, d7, d8, d9)) {
                return true;
            }
            if (Math.abs(d9 - d7) >= d1) {
                if (b(d6, d7, d2, d3, d4, d5)) {
                    if (d7 > d9) {
                        i++;
                    }
                } else if (b(d8, d9, d2, d3, d4, d5)) {
                    if (d9 > d7) {
                        i++;
                    }
                } else if (a(d6, d7, d8, d9, d2, d3, d4, d5)) {
                    i++;
                }
            }
        }
        if (i % 2 != 0) {
            bool = true;
        }
        return bool;
    }

    public static double a(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6) // a
    {
        return (paramDouble3 - paramDouble1) * (paramDouble6 - paramDouble2) - (paramDouble5 - paramDouble1) * (paramDouble4 - paramDouble2);
    }

    public static boolean b(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6) //b
    {
        boolean bool = false;
        double d = 1.0E-9D;
        if ((Math.abs(a(paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6)) < d) && ((paramDouble1 - paramDouble3) * (paramDouble1 - paramDouble5) <= 0.0D) && ((paramDouble2 - paramDouble4) * (paramDouble2 - paramDouble6) <= 0.0D)) {
            bool = true;
        }
        return bool;
    }

    public static boolean a(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8) // a
    {
        boolean bool = false;
        double d1 = (paramDouble3 - paramDouble1) * (paramDouble8 - paramDouble6) - (paramDouble4 - paramDouble2) * (paramDouble7 - paramDouble5);
        if (d1 != 0.0D) {
            double d2 = ((paramDouble2 - paramDouble6) * (paramDouble7 - paramDouble5) - (paramDouble1 - paramDouble5) * (paramDouble8 - paramDouble6)) / d1;

            double d3 = ((paramDouble2 - paramDouble6) * (paramDouble3 - paramDouble1) - (paramDouble1 - paramDouble5) * (paramDouble4 - paramDouble2)) / d1;
            if ((d2 >= 0.0D) && (d2 <= 1.0D) && (d3 >= 0.0D) && (d3 <= 1.0D)) {
                bool = true;
            }
        }
        return bool;
    }

    public static List<FPoint> a(IMapDelegate paramaa, List<FPoint> paramList) // a
    {
        ArrayList<FPoint> localArrayList1 = new ArrayList<FPoint>();
        ArrayList<FPoint> localArrayList2 = new ArrayList<FPoint>(paramList);
        FPoint[] arrayOfFPoint = a(paramaa);
        for (int i = 0; i < 4; i = (byte) (i + 1)) {
            localArrayList1.clear();
            for (int j = 0; j < localArrayList2.size() - 1; j++) {
                FPoint localFPoint1 = (FPoint) localArrayList2.get(j);
                FPoint localFPoint2 = (FPoint) localArrayList2.get(j + 1);
                if ((j == 0) &&
                        (a(localFPoint1, arrayOfFPoint[i], arrayOfFPoint[((i + 1) % 4)]))) {
                    localArrayList1.add(localFPoint1);
                }
                if (a(localFPoint1, arrayOfFPoint[i], arrayOfFPoint[((i + 1) % 4)])) {
                    if (a(localFPoint2, arrayOfFPoint[i], arrayOfFPoint[((i + 1) % 4)])) {
                        localArrayList1.add(localFPoint2);
                    } else {
                        localArrayList1.add(a(arrayOfFPoint[i], arrayOfFPoint[((i + 1) % 4)], localFPoint1, localFPoint2));
                    }
                } else if (a(localFPoint2, arrayOfFPoint[i], arrayOfFPoint[((i + 1) % 4)])) {
                    localArrayList1.add(a(arrayOfFPoint[i], arrayOfFPoint[((i + 1) % 4)], localFPoint1, localFPoint2));

                    localArrayList1.add(localFPoint2);
                }
            }
            localArrayList2.clear();
            for (int j = 0; j < localArrayList1.size(); j++) {
                localArrayList2.add(localArrayList1.get(j));
            }
        }
        return localArrayList2;
    }

    private static FPoint[] a(IMapDelegate paramaa)  // a
    {
        FPoint[] arrayOfFPoint = new FPoint[4];
        FPoint localFPoint1 = new FPoint();
        paramaa.win2Map(-100, -100, localFPoint1);
        arrayOfFPoint[0] = localFPoint1;
        FPoint localFPoint2 = new FPoint();
        paramaa.win2Map(paramaa.getMapWidth() + 100, -100, localFPoint2);
        arrayOfFPoint[1] = localFPoint2;
        FPoint localFPoint3 = new FPoint();
        paramaa.win2Map(paramaa.getMapWidth() + 100, paramaa.getMapHeight() + 100, localFPoint3);
        arrayOfFPoint[2] = localFPoint3;
        FPoint localFPoint4 = new FPoint();
        paramaa.win2Map(-100, paramaa.getMapHeight() + 100, localFPoint4);
        arrayOfFPoint[3] = localFPoint4;
        return arrayOfFPoint;
    }

    private static FPoint a(FPoint paramFPoint1, FPoint paramFPoint2, FPoint paramFPoint3, FPoint paramFPoint4) // a
    {
        FPoint localFPoint = new FPoint(0.0F, 0.0F);

        double d1 = (paramFPoint2.y - paramFPoint1.y) * (paramFPoint1.x - paramFPoint3.x) - (paramFPoint2.x - paramFPoint1.x) * (paramFPoint1.y - paramFPoint3.y);

        double d2 = (paramFPoint2.y - paramFPoint1.y) * (paramFPoint4.x - paramFPoint3.x) - (paramFPoint2.x - paramFPoint1.x) * (paramFPoint4.y - paramFPoint3.y);

        localFPoint.x = ((float) (paramFPoint3.x + (paramFPoint4.x - paramFPoint3.x) * d1 / d2));
        localFPoint.y = ((float) (paramFPoint3.y + (paramFPoint4.y - paramFPoint3.y) * d1 / d2));
        return localFPoint;
    }

    private static boolean a(FPoint paramFPoint1, FPoint paramFPoint2, FPoint paramFPoint3) // a
    {
        double d = (paramFPoint3.x - paramFPoint2.x) * (paramFPoint1.y - paramFPoint2.y) - (paramFPoint1.x - paramFPoint2.x) * (paramFPoint3.y - paramFPoint2.y);
        if (d >= 0.0D) {
            return true;
        }
        return false;
    }

    public static float a(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) // a
    {
        float f = 0.0F;
        if (paramDouble1 <= 0.0D) {
            if (paramDouble2 > 0.0D) {
                f = (float) a(paramDouble2, paramDouble4);
            }
        } else {
            f = (float) a(paramDouble1, paramDouble3);
            if (paramDouble2 > 0.0D) {
                f = (float) Math.min(f,
                        a(paramDouble2, paramDouble4));
            }
        }
        return f;
    }

    public static double a(double paramDouble1, double paramDouble2) // a
    {
        double d = 0.0D;
        if (paramDouble2 > 0.0D) {
            d = Math.log(paramDouble1 * 1048576.0D / paramDouble2) / Math.log(2.0D);
        }
        return d;
    }

    public static SDKInfo getSDKInfo() //e
    {
        try {
            if (ConfigableConstDecode.sdkInfo == null) {
                return new SDKInfo.createSDKInfo(ConfigableConstDecode.product, MapsInitializer.getVersion(), ConfigableConstDecode.userAgent,"")
                        .setPackageName(new String[]{PACKAGE_INFO_API_MAPS}).a();
            }
        } catch (Throwable localThrowable) {
            return null;
        }
        return ConfigableConstDecode.sdkInfo;
    }

    private static void b(View paramView) //b
    {
        if ((paramView instanceof ViewGroup)) {
            for (int i = 0; i < ((ViewGroup) paramView).getChildCount(); i++) {
                b(((ViewGroup) paramView).getChildAt(i));
            }
        } else if ((paramView instanceof TextView)) {
            ((TextView) paramView).setHorizontallyScrolling(false);
        }
    }

    public static Bitmap getBitmapFromView(View paramView) // a
    {
        try {
            b(paramView);
            paramView.destroyDrawingCache();
            paramView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            paramView.layout(0, 0, paramView.getMeasuredWidth(), paramView.getMeasuredHeight());

            Bitmap localBitmap = paramView.getDrawingCache().copy(Bitmap.Config.ARGB_8888, false);
            paramView.destroyDrawingCache();
            return localBitmap;
        } catch (Throwable localThrowable) {
            SDKLogHandler.exception(localThrowable, "Utils", "getBitmapFromView");
            localThrowable.printStackTrace();
        }
        return null;
    }

    public static DPoint a(LatLng latLng) // a
    {
        double x = latLng.longitude / 360.0D + 0.5D;
        double d2 = Math.sin(Math.toRadians(latLng.latitude));
        double y = 0.5D * Math.log((1.0D + d2) / (1.0D - d2)) / -Math.PI*2 + 0.5D;

        return new DPoint(x * 1.0D, y * 1.0D);
    }

    public static float[] makeFloatArray(IMapDelegate mapDelegate, int paramInt1,float pointX,float pointY, float rotateAngle, int paramInt2, int paramInt3, float paramFloat2, float paramFloat3)
        throws RemoteException {
        float f1 = pointX;
        float f2 = pointY;

        float[] arrayOfFloat1 = new float[12];

        int i = 0;
        float f3 = mapDelegate.getProjection().toOpenGLWidth(paramInt2);
        float f4 = mapDelegate.getProjection().toOpenGLWidth(paramInt3);
        float[] arrayOfFloat2 = new float[16];
        float[] arrayOfFloat3 = new float[4];

        Matrix.setIdentityM(arrayOfFloat2, 0);
        float rotate;
        if (paramInt1 == 3) {
            Matrix.translateM(arrayOfFloat2, 0, f1, f2, 0.0F);
            rotate = mapDelegate.getMapProjection().getMapAngle();
            Matrix.rotateM(arrayOfFloat2, 0, rotate, 0.0F, 0.0F, 1.0F);
            Matrix.translateM(arrayOfFloat2, 0, -f1, -f2, 0.0F);

            Matrix.translateM(arrayOfFloat2, 0, f1 - f3 / 2.0F, f2 - f4 / 2.0F, 0.0F);

            rotate = mapDelegate.getMapProjection().getCameraHeaderAngle();
            Matrix.rotateM(arrayOfFloat2, 0, -rotate, 1.0F, 0.0F, 0.0F);
            Matrix.translateM(arrayOfFloat2, 0, f3 / 2.0F - f1, f4 / 2.0F - f2, 0.0F);
        } else if (paramInt1 == 1) {
            Matrix.translateM(arrayOfFloat2, 0, f1, f2, 0.0F);
            rotate = rotateAngle;
            Matrix.rotateM(arrayOfFloat2, 0, rotate, 0.0F, 0.0F, 1.0F);
            Matrix.translateM(arrayOfFloat2, 0, -f1, -f2, 0.0F);
        } else {
            Matrix.translateM(arrayOfFloat2, 0, f1, f2, 0.0F);
            rotate = mapDelegate.getMapProjection().getMapAngle();
            Matrix.rotateM(arrayOfFloat2, 0, rotate, 0.0F, 0.0F, 1.0F);
            rotate = mapDelegate.getMapProjection().getCameraHeaderAngle();
            Matrix.rotateM(arrayOfFloat2, 0, -rotate, 1.0F, 0.0F, 0.0F);
            Matrix.rotateM(arrayOfFloat2, 0, rotateAngle, 0.0F, 0.0F, 1.0F);
            Matrix.translateM(arrayOfFloat2, 0, -f1, -f2, 0.0F);
        }
        float[] arrayOfFloat4 = new float[4];

        arrayOfFloat3[0] = (f1 - f3 * paramFloat2);
        arrayOfFloat3[1] = (f2 + f4 * (1.0F - paramFloat3));
        arrayOfFloat3[2] = 0.0F;
        arrayOfFloat3[3] = 1.0F;
        Matrix.multiplyMV(arrayOfFloat4, 0, arrayOfFloat2, 0, arrayOfFloat3, 0);
        arrayOfFloat1[(i * 3 + 0)] = arrayOfFloat4[0];
        arrayOfFloat1[(i * 3 + 1)] = arrayOfFloat4[1];
        arrayOfFloat1[(i * 3 + 2)] = arrayOfFloat4[2];
        i++;

        arrayOfFloat3[0] = (f1 + f3 * (1.0F - paramFloat2));
        arrayOfFloat3[1] = (f2 + f4 * (1.0F - paramFloat3));
        arrayOfFloat3[2] = 0.0F;
        arrayOfFloat3[3] = 1.0F;
        Matrix.multiplyMV(arrayOfFloat4, 0, arrayOfFloat2, 0, arrayOfFloat3, 0);
        arrayOfFloat1[(i * 3 + 0)] = arrayOfFloat4[0];
        arrayOfFloat1[(i * 3 + 1)] = arrayOfFloat4[1];
        arrayOfFloat1[(i * 3 + 2)] = arrayOfFloat4[2];
        i++;

        arrayOfFloat3[0] = (f1 + f3 * (1.0F - paramFloat2));
        arrayOfFloat3[1] = (f2 - f4 * paramFloat3);
        arrayOfFloat3[2] = 0.0F;
        arrayOfFloat3[3] = 1.0F;
        Matrix.multiplyMV(arrayOfFloat4, 0, arrayOfFloat2, 0, arrayOfFloat3, 0);
        arrayOfFloat1[(i * 3 + 0)] = arrayOfFloat4[0];
        arrayOfFloat1[(i * 3 + 1)] = arrayOfFloat4[1];
        arrayOfFloat1[(i * 3 + 2)] = arrayOfFloat4[2];
        i++;

        arrayOfFloat3[0] = (f1 - f3 * paramFloat2);
        arrayOfFloat3[1] = (f2 - f4 * paramFloat3);
        arrayOfFloat3[2] = 0.0F;
        arrayOfFloat3[3] = 1.0F;
        Matrix.multiplyMV(arrayOfFloat4, 0, arrayOfFloat2, 0, arrayOfFloat3, 0);
        arrayOfFloat1[(i * 3 + 0)] = arrayOfFloat4[0];
        arrayOfFloat1[(i * 3 + 1)] = arrayOfFloat4[1];
        arrayOfFloat1[(i * 3 + 2)] = arrayOfFloat4[2];

        return arrayOfFloat1;
    }

    public static float[] makeFloatArray(IMapDelegate mapDelegate, int paramInt1, FPoint fPoint, float rotate, int paramInt2, int paramInt3, float paramFloat2, float paramFloat3)
            throws RemoteException {
        return makeFloatArray(mapDelegate,paramInt1,fPoint.x,fPoint.y,rotate,paramInt2,paramInt3,paramFloat2,paramFloat3);
    }
    public static float[] makeFloatArray(IMapDelegate mapDelegate, int paramInt1, PointF fPoint, float rotate, int paramInt2, int paramInt3, float paramFloat2, float paramFloat3)
            throws RemoteException {
        return makeFloatArray(mapDelegate,paramInt1,fPoint.x,fPoint.y,rotate,paramInt2,paramInt3,paramFloat2,paramFloat3);
    }

    public static String getMetaValue(Context context,String strMetaKey) {
        ApplicationInfo appInfo = null;
        String msg = "";
        try {
            appInfo = context
                    .getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            msg = appInfo.metaData.getString(strMetaKey);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public static String encryptPara(String srcPara){
        String param1 = null;
        try {
            param1 = new DesUtil().encrypt(srcPara);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return param1;
    }

}
