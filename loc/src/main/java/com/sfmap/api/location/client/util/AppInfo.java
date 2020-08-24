package com.sfmap.api.location.client.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import org.w3c.dom.Text;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 * AppInfo
 * 工具类 获取服务地址、获取包名、sha1、ak
 *
 * @author
 * @version 1.0 2018.10.29<br>
 */
public class AppInfo {
    private static String scode = "";
    private static String packageName = "";
    private static String sha1AndPackage = null;
    private static String sha1 = null;
    public static String CONFIG_MAP_API_KEY = "com.sfmap.apikey";

    //网络定位服务地址
    private static String netLocationURL = "";
    private static String netLocationDefURL = "https://gis.sf-express.com/nloc/locationapi";

    //GPS定位服务地址
    private static String gpsLocationURL = "";
    private static String gpsLocationDefURL = "https://gis.sf-express.com/nloc/gpsapi";

    //图咕系统配置的KEY
    private static String systemAk = "";
    private static String systemAkDef = "";
    private static double lng ;
    private static double lat;

    public static String getSpUrl() {
        return netLocationURL;
    }

    public static void setSpUrl(String spUrl) {
        netLocationURL = spUrl;
    }

    public static void setSystemAkDef(String systemAkDef) {
        AppInfo.systemAkDef = systemAkDef;
    }

    /**
     * 获取终端身份信息
     *
     * @param context
     * @return 终端身份信息的md5
     */
    protected static String getScode(Context context) {
        try {
            if ((scode != null) && (!"".equals(scode))) {
                return scode;
            }
            PackageInfo localPackageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 64);

            byte[] arrayOfByte1 = localPackageInfo.signatures[0].toByteArray();
            MessageDigest localMessageDigest = MessageDigest
                    .getInstance("SHA1");
            byte[] arrayOfByte2 = localMessageDigest.digest(arrayOfByte1);
            StringBuffer localStringBuffer = new StringBuffer();
            for (int i = 0; i < arrayOfByte2.length; i++) {
                String str = Integer.toHexString(0xFF & arrayOfByte2[i])
                        .toUpperCase(Locale.US);

                if (str.length() == 1)
                    localStringBuffer.append("0");
                localStringBuffer.append(str);
                localStringBuffer.append(":");
            }
            localStringBuffer.append(localPackageInfo.packageName);
            scode = MD5(localStringBuffer.toString());
            return scode;
        } catch (NameNotFoundException localNameNotFoundException) {

        } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {

        } catch (Throwable localThrowable) {

        }
        return scode;
    }

    public static void setApiKey(String systemAk) {
        AppInfo.systemAk = systemAk;
    }

    public static boolean IsEmptyOrNullString(String s) {
        return (s == null) || (s.trim().length() == 0);
    }

    /**
     * 获取字符串的md5值。
     *
     * @param s 字符串。
     * @return 字符串md5结果。
     */
    public final static String MD5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("deprecation")
    public static Proxy getProxy(Context cnt) {
        Proxy proxy = null;
        ConnectivityManager connMgr = (ConnectivityManager) cnt
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connMgr.getActiveNetworkInfo();
        if (null != info) {
            String proxyHost = null;
            int proxyPort = 0;
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                // WIFI: global http proxy
                proxyHost = android.net.Proxy.getHost(cnt);
                proxyPort = android.net.Proxy.getPort(cnt);
            } else {
                // GPRS: APN http proxy
                proxyHost = android.net.Proxy.getDefaultHost();
                proxyPort = android.net.Proxy.getDefaultPort();
            }

            if (proxyHost != null) {
                proxy = new Proxy(Proxy.Type.HTTP,
                        new InetSocketAddress(proxyHost, proxyPort));
            }
        }
        return proxy;
    }

    public static void Log(String tag, String msg) {
        Log.e(tag, msg);
    }

    public static int dipToPixel(Context context, int dipValue) {
        float pixelFloat = android.util.TypedValue.applyDimension(
                android.util.TypedValue.COMPLEX_UNIT_DIP, dipValue, context
                        .getResources().getDisplayMetrics());
        return (int) pixelFloat;
    }

    public static boolean isNull(Object obj) {
        if (obj == null)
            return true;
        return false;
    }

    public static String getMetaValue(Context context, String strMetaKey) {
        ApplicationInfo appInfo = null;
        String msg = null;
        try {
            appInfo = context
                    .getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            msg = appInfo.metaData.getString(strMetaKey);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public static String getPackageName(Context context) {
        if ((packageName != null) && (!"".equals(packageName))) {
            return packageName;
        }
        packageName = context.getApplicationContext().getPackageName();
        return packageName;
    }

    public static void setPackageName(String packageName) {
        AppInfo.packageName = packageName;
    }

    public static void setSha1(String sha1) {
        AppInfo.sha1 = sha1;
    }

    public static String getSHA1(Context context) {
        try {
            if ((sha1 != null) && (!sha1.isEmpty())) {
                return sha1;
            }
            PackageInfo localPackageInfo = null;
            localPackageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] arrayOfByte1 = localPackageInfo.signatures[0].toByteArray();
            MessageDigest localMessageDigest = null;
            localMessageDigest = MessageDigest.getInstance("SHA1");
            byte[] arrayOfByte2 = localMessageDigest.digest(arrayOfByte1);
            StringBuffer localStringBuffer = new StringBuffer();

            for (int i = 0; i < arrayOfByte2.length; i++) {
                String str = Integer.toHexString(0xFF & arrayOfByte2[i]).toUpperCase(Locale.US);
                if (str.length() == 1) {
                    localStringBuffer.append("0");
                }
                localStringBuffer.append(str);
                if (i != arrayOfByte2.length - 1) {
                    //不是最后一个，就添加冒号
                    localStringBuffer.append(":");
                }
            }

            sha1 = localStringBuffer.toString();
            return sha1;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sha1AndPackage;
    }

    /*
     * 获取服务地址方法 说明：优先采用app中设置的地址 获取不到用sdk中设置的地址
     * @param context application上下文
     * @param curValue app中sfmap_configer中的地址
     * @param defaultValue sdk中默认的地址
     * @param metaKey 获取app中地址用的标识
     * @return 服务地址
     */
    private static String getCustomOrDefaultURL(Context context, String curValue, String defaultValue, String metaKey) {
        try {
            if ((curValue == null) || (curValue.equals(""))) {
                curValue = ConfigerHelper.getInstance(context).getKeyValue(metaKey);
                if (curValue == null || curValue.equals("")) {
                    curValue = defaultValue;
                }
            }
        } catch (Exception e) {
            curValue = defaultValue;
        }
        return curValue;
    }

    /*
     * 网络定位服务地址
     * @param context
     * @return
     */
    public static String getNetLocationUrl(Context context) {
        if (TextUtils.isEmpty(netLocationURL)) {
            return getCustomOrDefaultURL(context, netLocationURL, netLocationDefURL, ConfigerHelper.NET_LOCATION_URL);
        } else {
            return netLocationURL;
        }
    }

    /*
     * GPS定位服务地址
     * @param context
     * @return
     */
    public static String getGpsLocationUrl(Context context) {
        if (TextUtils.isEmpty(netLocationURL)) {
            return getCustomOrDefaultURL(context, netLocationURL, netLocationDefURL, ConfigerHelper.NET_LOCATION_URL);
        } else {
            return netLocationURL;
        }
    }

    /*
     * 图咕系统配置的KEY
     * @param context
     * @return
     */
    public static String getSystemAk(Context context) {
        return getCustomOrDefaultURL(context, systemAk, systemAkDef, ConfigerHelper.SYSTEM_AK);
    }

    public static double getLng() {
        return lng;
    }

    public static void setLng(double lng) {
        AppInfo.lng = lng;
    }

    public static double getLat() {
        return lat;
    }

    public static void setLat(double lat) {
        AppInfo.lat = lat;
    }
}
