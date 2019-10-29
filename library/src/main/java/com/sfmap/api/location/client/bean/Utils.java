/*
 * 
 */
package com.sfmap.api.location.client.bean;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joseph
 */
public class Utils {

    public static final String strKey = "openmap007spas$#@!888888";

    /**
     *
     * @param wifi
     * @return
     */
    public static long getWifiLong(String wifi) {
        String mac = wifi.replaceAll("[:-]", "");
        try {
            long l = Long.parseLong(mac, 16);
            return l;
        } catch (Exception ex) {

        }
        return 0;
    }

    public static int getInt(String str, int defaultValue) {
        int rs = defaultValue;
        try {
            rs = Integer.parseInt(str);
        } catch (Exception e) {

        }
        return rs;
    }

    public static long getLong(String str, long defaultValue) {
        long rs = defaultValue;
        try {
            rs = Long.parseLong(str);
        } catch (Exception e) {

        }
        return rs;
    }

    public static boolean getBoolean(String str, boolean defaultValue) {
        boolean rs = defaultValue;
        try {
            rs = Boolean.parseBoolean(str);
        } catch (Exception e) {

        }
        return rs;
    }

    public static double getDouble(String str, double defaultValue) {
        double rs = defaultValue;
        try {
            rs = Double.parseDouble(str);
        } catch (Exception e) {

        }
        return rs;
    }

    public static short getShort(String str, short defaultValue) {
        short rs = defaultValue;
        try {
            rs = Short.parseShort(str);
        } catch (Exception e) {

        }
        return rs;
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

    public static String change2GBK(String str) {
        if (str != null) {
            //用源字符编码解码字符串

            try {
                byte[] bs = str.getBytes("UTF-8");
                return new String(bs, "GBK");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public static String changeCharset(String str, String oldCharset, String newCharset) throws UnsupportedEncodingException {
        if (str != null) {
            //用源字符编码解码字符串
            byte[] bs = str.getBytes(oldCharset);
            return new String(bs, newCharset);
        }
        return null;
    }

    public static List<Long> getWifiLongs(List<String> wifis) {
        List<Long> rs = new ArrayList();
        for (String wifi : wifis) {
            try {
                long mac = Long.parseLong(wifi.replaceAll("[:-]", ""));
                rs.add(mac);
            } catch (Exception ex) {
                ex.printStackTrace();
                rs.add(0L);
            }
        }
        return rs;
    }

    /**
     *
     * @param wifis
     * @return
     */
    public static List<byte[]> getWifiByteArrays(List<String> wifis) {
        List<byte[]> rs = new ArrayList();
        for (String wifi : wifis) {
            try {
                long mac = Long.parseLong(wifi.replaceAll("[:-]", ""));
                rs.add(ByteUtils.long2Bytes(mac));
            } catch (Exception ex) {
                ex.printStackTrace();
                rs.add(ByteUtils.long2Bytes(0));
            }
        }
        return rs;
    }

    public static Map<Integer, List<byte[]>> getWifisByPartion(List<String> wifis) {
        //TODO
        return new HashMap();
    }

    public static void main(String[] args) {
        Long l = Utils.getWifiLong("ae:00:67-4f:ab:88");
        System.out.println("l=" + Long.toHexString(l));
    }
}
