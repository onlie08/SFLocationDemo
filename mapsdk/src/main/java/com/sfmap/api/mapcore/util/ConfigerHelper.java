package com.sfmap.api.mapcore.util;

/**
 * Created by 01377555 on 2018/10/29.
 */
import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * 提取一些URL作为常量，便于复用
 */

public class ConfigerHelper {

    private static ConfigerHelper instance = null;
    private static final String CONFIGER_FILE = "sfmap_configer.data";
    //配置信息容器
    HashMap<String, String> mConfStrList;

    public static final String SF_MAP_URL = "sf_map_url";                   //生产地图数据和样式服务地址
    public static final String SF_MAP_URL_TC = "sf_map_url_tc";                   //腾讯云地图数据和样式服务地址
    public static final String SF_TMC_URL = "sf_tmc_url";                   //地图实时交通服务地址
    public static final String SF_AUTH_URL = "sf_auth_url";                 //地图鉴权
    public static final String SYSTEM_AK = "system_ak";                     //图咕系统配置的KEY
    public static final String SF_ORKER_MAP_URL = "sf_order_map_url";         //内部定制版地图数据和样式服务地址


    public static ConfigerHelper getInstance(Context context) {
        if (instance == null) {
            instance = new ConfigerHelper(context);
        }
        return instance;
    }

    private ConfigerHelper(Context context) {
        mConfStrList = new HashMap<String, String>();
        readConfiger(context);
    }

    public String getKeyValue(String key) {
        String val = mConfStrList.get(key);
        return val == null ? "" : val;
    }

    /**
     * 读取配置文件
     *
     * @return
     */
    public InputStream GetConfigerFile(Context appContext) {
        InputStream tmpStream = null;
        try {
            tmpStream = appContext.getResources().getAssets()
                    .open(CONFIGER_FILE);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                return appContext.getResources().getAssets()
                        .open(CONFIGER_FILE);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return tmpStream;
    }

    private void readConfiger(Context appContext) {
        try {
            InputStream tmpStream = GetConfigerFile(appContext);
            BufferedReader bufReader = new BufferedReader(
                    new InputStreamReader(tmpStream, "UTF-8"));
            String str;
            while ((str = bufReader.readLine()) != null) {
                if (str != null && str.length() > 0
                        && str.startsWith("#") == false) {
                    String[] tmp = str.split("=");
                    if (tmp != null && tmp.length >= 2) {
                        String name = tmp[0];
                        String value = tmp[1];
                        for (int i = 0; i < tmp.length - 2; i++) {
                            value += "=";
                            value += tmp[i + 2];
                        }
                        if (name != null && value != null) {
                            name = name.trim();
                            value = value.trim();
                            if (name.length() > 0 && value.length() > 0)
                                mConfStrList.put(name, value);
                        }
                    }
                }
            }
            bufReader.close();
            tmpStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
