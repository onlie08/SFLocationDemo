package com.sfmap.api.mapcore.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/*
 * 工具类 获取服务地址、获取包名、sha1、ak
 */
public class AppInfo//bm
{
    private static String TAG = "AppInfo";
    public static float currentZoom;
    private static String appName = "";
    private static String packageName = "";
    private static String appVersion = "";
    private static int appVersionCode = 0;

    public static int NormalVersion = 0;
    public static int OrderVersion = 1;

    private static String sha1AndPackage = null;
    private static String sha1 = null;
    private static Context mContext;

    //地图数据和样式服务地址
    private static String sfMapURL = "";
    private static String sfMapDefURL = "https://gis.sf-express.com/mms/ds";

    //地图实时交通数据服务地址
    private static String sfTmcURL = "";
    private static String sfTmcDefURL = "http://111.230.237.93:9007/tmc";

    //鉴权
    private static String sfAuthURL = "";
    private static String sfAuthDefURL = "http://58.48.194.238:23331/v3/mobile/auth";

    //图咕系统配置的KEY
    private static String systemAk = "";
    private static String systemAkDef = "ec85d3648154874552835438ac6a02b2";

    /**
     * set方式注入context对象
     * @param cn
     */
    public static void setContext(Context cn) {
        mContext = cn;
    }

    public static String getPackageName() {
        return getPackageName(mContext);
    }
    public static String getSHA1() {
        return getSHA1(mContext);
    }
    public static String getAppApiKey() {
        return getAppApiKey(mContext);
    }

    public static String getAppKey(Context context) {
        try {
            return getSystemAk(context);
        } catch (Throwable localThrowable) {
            localThrowable.printStackTrace();
        }
        return "";
    }

    public static String getAppApiKey(Context context) {
        String key = Util.getMetaValue(context, Util.CONFIG_API_KEY);
        if (TextUtils.isEmpty(key)) {
            android.util.Log.e(TAG, "获取key失败");
            return "";
        }
        return key;
    }

    public static String getApplicationName(Context context) {
        PackageManager localPackageManager = null;
        ApplicationInfo localApplicationInfo = null;
        try {
            if (!"".equals(appName)) {
                return appName;
            }
            localPackageManager = context.getPackageManager();
            localApplicationInfo = localPackageManager.getApplicationInfo(context.getPackageName(), 0);

            appName = (String) localPackageManager.getApplicationLabel(localApplicationInfo);
        } catch (PackageManager.NameNotFoundException localNameNotFoundException) {
            BasicLogHandler.a(localNameNotFoundException, "AppInfo", "getApplicationName");
        } catch (Throwable localThrowable) {
            BasicLogHandler.a(localThrowable, "AppInfo", "getApplicationName");
        }
        return appName;
    }

    public static String getPackageName(Context context) {
        try {
            if ((packageName != null) && (!"".equals(packageName))) {
                return packageName;
            }
            packageName = context.getApplicationContext().getPackageName();
        } catch (Throwable localThrowable) {
            BasicLogHandler.a(localThrowable, "AppInfo", "getPackageName");
        }
        return packageName;
    }

    public static String getApplicationVersion(Context context) {
        PackageInfo localPackageInfo = null;
        try {
            if (!"".equals(appVersion)) {
                return appVersion;
            }
            PackageManager localPackageManager = context.getPackageManager();
            localPackageInfo = localPackageManager.getPackageInfo(context.getPackageName(), 0);

            appVersion = localPackageInfo.versionName;
        } catch (PackageManager.NameNotFoundException localNameNotFoundException) {
            BasicLogHandler.a(localNameNotFoundException, "AppInfo", "getApplicationVersion");
        } catch (Throwable localThrowable) {
            BasicLogHandler.a(localThrowable, "AppInfo", "getApplicationVersion");
        }
        return appVersion;
    }

    public static int getApplicationVersionCode(Context context) {
        PackageInfo localPackageInfo = null;
        try {
            if (appVersionCode != 0) {
                return appVersionCode;
            }
            PackageManager localPackageManager = context.getPackageManager();
            localPackageInfo = localPackageManager.getPackageInfo(context.getPackageName(), 0);

            appVersionCode = localPackageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException localNameNotFoundException) {
            BasicLogHandler.a(localNameNotFoundException, "AppInfo", "getApplicationVersion");
        } catch (Throwable localThrowable) {
            BasicLogHandler.a(localThrowable, "AppInfo", "getApplicationVersion");
        }
        return appVersionCode;
    }

    public static String getSHA1(Context context) {
        try {
            if ((sha1 != null) && (!sha1.isEmpty())) {
                return sha1;
            }

            PackageInfo localPackageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] arrayOfByte1 = localPackageInfo.signatures[0].toByteArray();
            MessageDigest localMessageDigest = MessageDigest.getInstance("SHA1");
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
        } catch (PackageManager.NameNotFoundException localNameNotFoundException) {
            BasicLogHandler.a(localNameNotFoundException, "AppInfo", "getSHA1AndPackage");
        } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
            BasicLogHandler.a(localNoSuchAlgorithmException, "AppInfo", "getSHA1AndPackage");
        } catch (Throwable localThrowable) {
            BasicLogHandler.a(localThrowable, "AppInfo", "getSHA1AndPackage");
        }
        return sha1AndPackage;
    }

    public static String getSHA1AndPackage(Context context) {
        try {
            if ((sha1AndPackage != null) && (!"".equals(sha1AndPackage))) {
                return sha1AndPackage;
            }
            PackageInfo localPackageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);

            byte[] arrayOfByte1 = localPackageInfo.signatures[0].toByteArray();
            MessageDigest localMessageDigest = MessageDigest.getInstance("SHA1");
            byte[] arrayOfByte2 = localMessageDigest.digest(arrayOfByte1);
            StringBuffer localStringBuffer = new StringBuffer();

            for (int i = 0; i < arrayOfByte2.length; i++) {
                String str = Integer.toHexString(0xFF & arrayOfByte2[i]).toUpperCase(Locale.US);
                if (str.length() == 1) {
                    localStringBuffer.append("0");
                }
                localStringBuffer.append(str);
                localStringBuffer.append(":");
            }

            localStringBuffer.append(localPackageInfo.packageName);
            sha1AndPackage = localStringBuffer.toString();
            return sha1AndPackage;
        } catch (PackageManager.NameNotFoundException localNameNotFoundException) {
            BasicLogHandler.a(localNameNotFoundException, "AppInfo", "getSHA1AndPackage");
        } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
            BasicLogHandler.a(localNoSuchAlgorithmException, "AppInfo", "getSHA1AndPackage");
        } catch (Throwable localThrowable) {
            BasicLogHandler.a(localThrowable, "AppInfo", "getSHA1AndPackage");
        }
        return sha1AndPackage;
    }

    static void setKey(String paramString) {
        systemAk = paramString;
    }

    public static String getKey(Context context) {
        try {
            return getSystemAk(context);
        } catch (Throwable localThrowable) {
            BasicLogHandler.a(localThrowable, "AppInfo", "getKey");
        }
        return "";
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
     * 地图数据服务地址
     * @param context
     * @return
     */
    public static String getSfMapURL(Context context) {
        if(getAppVersion(context) == OrderVersion){
            return getCustomOrDefaultURL(context, sfMapURL, sfMapDefURL, ConfigerHelper.SF_MAP_URL_TC);
        }else {
            return getCustomOrDefaultURL(context, sfMapURL, sfMapDefURL, ConfigerHelper.SF_MAP_URL);
        }
    }

    /*
     * 实时交通服务地址
     * @param context
     * @return
     */
    public static String getSfTmcURL(Context context) {
        return getCustomOrDefaultURL(context, sfTmcURL, sfTmcDefURL, ConfigerHelper.SF_TMC_URL);
    }

    /*
     * 地图鉴权服务地址
     * @param context
     * @return
     */
    public static String getSfAuthURL(Context context) {
        return getCustomOrDefaultURL(context, sfAuthURL, sfAuthDefURL, ConfigerHelper.SF_AUTH_URL);
    }

    /*
     * 图咕系统配置的KEY
     * @param context
     * @return
     */
    public static String getSystemAk(Context context) {
        return getCustomOrDefaultURL(context, systemAk, systemAkDef, ConfigerHelper.SYSTEM_AK);
    }

    /*
     * 控制地图版本类型 0:通用版本 1:内部定制版
     * @param context
     * @return
     */
    public static int getAppVersion(Context context) {
        return getPackageName(context).equals("com.sfmap.map.internal") ? OrderVersion:NormalVersion;
    }

    /*
     * 内部定制版地图数据和样式服务地址
     * @param context
     * @return
     */
    public static String getOrderMapURL(Context context) {
        return getCustomOrDefaultURL(context, "", "", ConfigerHelper.SF_ORKER_MAP_URL);
    }

}
