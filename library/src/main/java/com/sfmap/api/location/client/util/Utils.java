/*
 * 
 */
package com.sfmap.api.location.client.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.sfmap.api.location.BuildConfig;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/**
 *
 */
public class Utils {

    private static final String TAG = "Utils";

    static {
//        HttpsURLConnection.setDefaultHostnameVerifier(new LocHostNameVerifier());
        // Create an SSLContext that uses our TrustManager
        SSLContext context;
        try {
            context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[]{new LocTrustManager()}, null);

            // Tell the URLConnection to use a SocketFactory from our SSLContext
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            e.printStackTrace();
        }

    }

    public static String encode(String input, String encoding) {
        try {
            return URLEncoder.encode(input, encoding);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return input;
    }

    public static String decode(String input, String encoding) {
        try {
            return URLDecoder.decode(input, encoding);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return input;
    }

    /*
      * 获得耗时显示信息
     *
     * @param times 耗时毫秒数
     * @return
     */
    public static String getTimeSpan(long times) {
        int hours = (int) (times / 3600000);
        int minutes = (int) (times % 3600000) / 60000;
        int seconds = ((int) (times % 3600000) % 60000) / 1000;
        StringBuilder sb = new StringBuilder();
        sb.append(hours).append(" h,").append(minutes).append(" m,").append(seconds).append(" s");
        return sb.toString();
    }

    public static boolean isCnOperator(Context context) {
        TelephonyManager telephoneService = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if(telephoneService == null) {
            return true;
        }
        String[] mccMnc = getMccMnc(telephoneService);
        String mcc = mccMnc[0];
        if (TextUtils.isEmpty(mcc)) {
            String countryIso = telephoneService.getSimCountryIso();
            if(!TextUtils.isEmpty(countryIso)) {
                return countryIso.toLowerCase(Locale.US).contains("cn");
            }
            return true;
        } else {
            return mcc.startsWith("460");
        }
    }

    /**
     * 发送Post请求
     *
     * @param surl target url of post
     * @param headers custom post headers
     * @param content post body
     * @param encoding response encoding
     * @return response body
     */
    public static String post(String surl, Map<String, String> headers, String content, String encoding) {
        HttpURLConnection httpConnection = null;
        try {
            URL url = new URL(surl);

            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("POST");// 提交模式
            httpConnection.setReadTimeout(50000);
            httpConnection.setDoOutput(true);
            //
            //httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            if( headers != null){
            Set<String> headerKeys = headers.keySet();

                for (String key : headerKeys) {
                    //
                    httpConnection.setRequestProperty(key, headers.get(key));
                }
            }
            //
            httpConnection.getOutputStream().write(content.getBytes());// 输入参数
            //
            int nRead = 0;
            InputStream input = httpConnection.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            while ((nRead = input.read(b, 0, 1024)) > 0) {
                out.write(b, 0, nRead);
            }
            //
            String rs = new String(out.toByteArray(), encoding);
            return rs;
        } catch (Exception e) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                if (httpConnection != null) {
                    httpConnection.disconnect();
                }
            } catch (Exception e) {
                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, e);

            }
        }
        return null;
    }

    /**
     * 发送Get请求
     *
     * @param surl
     * @param headers
     * @param encoding
     * @return
     */
    public static String get(String surl, Map<String, String> headers, String encoding) {


        HttpURLConnection httpConnection = null;
        try {
            URL url = new URL(surl);

            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setReadTimeout(20 * 1000);
            httpConnection.setConnectTimeout(10 * 1000);
            if (headers != null) {
                Set<String> headerKeys = headers.keySet();
                for (String key : headerKeys) {
                    httpConnection.setRequestProperty(key, headers.get(key));
                }
            }

            int status = httpConnection.getResponseCode();
            if(status == HttpURLConnection.HTTP_OK) {
                int nRead = 0;
                InputStream input = httpConnection.getInputStream();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] b = new byte[1024];
                while ((nRead = input.read(b, 0, 1024)) > 0) {
                    out.write(b, 0, nRead);
                }
                input.close();
                //
                String rs = new String(out.toByteArray(), encoding);
                return rs;
            } else {
                int nRead = 0;
                InputStream input = httpConnection.getErrorStream();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] b = new byte[1024];
                while ((nRead = input.read(b, 0, 1024)) > 0) {
                    out.write(b, 0, nRead);
                }
                input.close();
                //
                String errorBody = new String(out.toByteArray(), encoding);
                Logger.getLogger(Utils.class.getName()).log(Level.WARNING, errorBody, new Exception());
            }

        } catch (Exception e) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                if (httpConnection != null) {
                    httpConnection.disconnect();
                }
            } catch (Exception e) {
                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return null;
    }

    /**
     * 下载数据
     *
     * @param sUrl URl地址
     * @param nStartPos 保存文件的开始位置
     * @param Filename 保存的文件名
     * @param headers 请求头信息
     * @return 下载的文件尺寸
     */
    public static int downData(String sUrl, int nStartPos, String Filename, Map<String, String> headers) {
        HttpURLConnection httpConnection = null;
        RandomAccessFile oSavedFile = null;
        try {
            URL url = new URL(sUrl);

            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setReadTimeout(5000);
            if (headers != null) {
                Set<Map.Entry<String, String>> hs = headers.entrySet();
                for (Map.Entry<String, String> h : hs) {
                    httpConnection.setRequestProperty(h.getKey(), h.getValue());
                }

            }
            oSavedFile = new RandomAccessFile(Filename, "rw");
            oSavedFile.seek(nStartPos);

            int nRead = 0;
            InputStream input = httpConnection.getInputStream();
            byte[] b = new byte[1024];
            while ((nRead = input.read(b, 0, 1024)) > 0) {

                oSavedFile.write(b, 0, nRead);
                nStartPos += nRead;

            }

        } catch (Exception e) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                if (httpConnection != null) {
                    httpConnection.disconnect();
                }
                if (oSavedFile != null) {
                    oSavedFile.close();
                }
            } catch (Exception e) {
                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, e);

            }
        }

        return nStartPos;
    }

    /**
     * 移动文件
     *
     * @param srcFile
     * @param dstFile
     */
    public static void moveFile(File srcFile, File dstFile) {
        if (!srcFile.renameTo(dstFile)) {
            FileInputStream fin = null;
            FileOutputStream fout = null;
            try {
                //采用文件内容拷贝方式
                fin = new FileInputStream(srcFile);
                byte[] buffer = new byte[2048];
                fout = new FileOutputStream(dstFile);
                int len;
                while ((len = fin.read(buffer, 0, 2048)) != -1) {
                    fout.write(buffer, 0, len);
                }
                //删除原始文件
                srcFile.delete();
                //
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (fin != null) {
                    try {
                        fin.close();
                    } catch (IOException ex) {
                        Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (fout != null) {
                    try {
                        fout.close();
                    } catch (IOException ex) {
                        Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        }
    }

    public static int getInt(String str, int dvalue) {
        int rs = dvalue;
        try {
            rs = Integer.parseInt(str);
        } catch (Exception e) {

        }
        return rs;
    }

    public static double getDouble(String str, double dvalue) {
        double rs = dvalue;
        try {
            rs = Double.parseDouble(str);
        } catch (Exception e) {

        }
        return rs;
    }

    public static byte[] gzip(byte[] in) {
        GZIPOutputStream gout = null;
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            gout = new GZIPOutputStream(bout);
            ByteArrayInputStream bin = new ByteArrayInputStream(in);
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = bin.read(buffer)) > 0) {
                gout.write(buffer, 0, count);
            }
            gout.finish();
            gout.close();
            return bout.toByteArray();
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                gout.close();
            } catch (IOException ex) {
                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    /**
     * 解压文件到指定目录
     *
     * @param path 输入的Zip文件
     * @param outPath 解压的目录
     * @throws IOException
     */
    public static void unZip(String path, String outPath) throws IOException {
        int buffer = 2048;
        String savepath = outPath;// path.substring(0, path.lastIndexOf("\\")) + "\\";

        ZipInputStream zis = null;
        try {
            int count = -1;
            int index = -1;
            ZipEntry entry = null;
            BufferedOutputStream bos = null;
            FileInputStream fis = null;
            fis = new FileInputStream(path);
            zis = new ZipInputStream(new BufferedInputStream(fis));

            while ((entry = zis.getNextEntry()) != null) {
                byte data[] = new byte[buffer];

                String temp = entry.getName();

                index = temp.lastIndexOf("/");
                if (index > -1) {
                    temp = temp.substring(index + 1);
                }
                temp = savepath + "/" + temp;
                try {
                    File f = new File(temp);
                    f.createNewFile();

                    FileOutputStream fos = new FileOutputStream(f);
                    bos = new BufferedOutputStream(fos, buffer);

                    while ((count = zis.read(data, 0, buffer)) != -1) {
                        bos.write(data, 0, count);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bos != null) {
                        bos.flush();
                        bos.close();
                    }
                }

            }

        } finally {
            if (zis != null) {
                try {
                    zis.close();
                } catch (IOException ex) {
                    Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * Test main method
     * @param args entrance args
     */
    public static void main(String[] args) {
        //
        Map<String, String> headers = new HashMap();
        headers.put("host", "vector0.map.bdimg.com");
        headers.put("User-Agent", "MobileMap");
        headers.put("Content-type", "application/xhtml+xml");
        headers.put("Mimetype", "application/octet-stream");
        //
        String url = "http://vector0.map.bdimg.com/vecdata/?qt=vFile&c=cityidx&v=61&fv=1&mb=HTC%20802t&os=Android17&sv=8.5.0&net=1&resid=01&cuid=319C26019323481FEDADC0B5CD2449FA%7C999010050968553&bduid=&channel=1008617b&screen=%281080%2C1920%29&dpi=%28474%2C474%29&ver=1&ctm=1436528517.231000";
        String rs = Utils.get(url, headers, "gbk");
        System.out.println("rs=" + rs);
        //

    }

    /**
     * 读取设备 Mobile Country Code，MCC 和 Mobile Network Code，MNC
     * @param telephonyManager TelephonyManager service object
     * @return 2 dimension string array [mcc, mnc]
     */
    public static String[] getMccMnc(TelephonyManager telephonyManager) {
        String strNetworkOperator = telephonyManager.getNetworkOperator();
        String[] saMccMnc = {"", ""};
        if (strNetworkOperator.length() > 4) {
            saMccMnc[0] = strNetworkOperator.substring(0, 3);
            saMccMnc[1] = strNetworkOperator.substring(3, 5);
        } else {
            if(BuildConfig.DEBUG) {
                Log.e(TAG, "get mcc and mnc error");
            }
        }
        return saMccMnc;
    }

    /**
     * 将手机信号强度由asu(GSM) 转换成dBm
     *
     * @param asu Arbitrary Strength Unit in GSM
     * @return int dbm
     */
    public static int asu2Dbm(int asu) {
        return (-113 + 2 * asu);
    }

    /**
     *将手机信号强度由dBm 转换成asu(GSM)
     * @param dbm dbm
     * @return Arbitrary Strength Unit in GSM
     */
    public static int dbm2Asu(int dbm) {
        return (dbm + 113) / 2;
    }

    public static boolean isNetworkConnected(Context context) {
        try {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivityManager == null) {
                Utils.saveGpsInfo("网络问题-isNetworkConnected - connectivityManager ==null");
                return false;
            }
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }catch (Exception e){
            Utils.saveGpsInfo("网络问题-isNetworkConnected - Exception"+e.getMessage().toString());
            return false;
        }
    }

    public static void saveGpsInfo(String infos){
        infos = getGpsLoaalTime(System.currentTimeMillis()) + ": " +infos + "\n";

        String gpsLogPath = Environment
                .getExternalStorageDirectory().getAbsolutePath() + "/sflocation/" + getTimeStampFormat("yyyy-MM-dd") +"_"+"_GpsLog.txt";
        File file = new File(gpsLogPath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "rw");
            raf.seek(file.length());

            raf.write(infos.getBytes());
            raf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getTimeStampFormat(String format) {
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(dt);
    }

    public static String getGpsLoaalTime(long gpsTime){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(gpsTime);
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String datestring = df.format(calendar.getTime());
        return datestring;
    }
}
