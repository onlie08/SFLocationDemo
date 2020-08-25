package com.sfmap.api.services.geocoder;

import org.json.JSONObject;

/**
 * Created by 01377555 on 2018/9/21.
 */
public class RgeoBean {

    private String x;


    private String y;


    private String cc;

    private String opt;

    /**
     * 包名
     */
    private String appPackageName;

    /**
     * sha1
     */
    private String appCerSha1;

    public String getOpt() {
        return opt;
    }

    public void setOpt(String opt) {
        this.opt = opt;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    public String getAppCerSha1() {
        return appCerSha1;
    }

    public void setAppCerSha1(String appCerSha1) {
        this.appCerSha1 = appCerSha1;
    }

    public String toString() {
        JSONObject obj = new JSONObject();
        try{
            obj.put("x",x);
            obj.put("y",y);
            obj.put("appPackageName",appPackageName);
            obj.put("appCerSha1",appCerSha1);
        }catch (Exception e){
        }
        return obj.toString();
    }
}
