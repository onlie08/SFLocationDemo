package com.sfmap.api.services.geocoder;

import org.json.JSONObject;

/**
 * Created by 01377555 on 2018/9/21.
 */
public class GeoBean {


    private String address;

    private String city;

    private String opt;


    private String cc;

    private String normal;


    private String exact;


    private String span;

    /**
     * 包名
     */
    private String appPackageName;

    /**
     * sha1
     */
    private String appCerSha1;



    /**
     * Set & Get
     */
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getOpt() {
        return opt;
    }

    public void setOpt(String opt) {
        this.opt = opt;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getNormal() {
        return normal;
    }

    public void setNormal(String normal) {
        this.normal = normal;
    }

    public String getExact() {
        return exact;
    }

    public void setExact(String exact) {
        this.exact = exact;
    }

    public String getSpan() {
        return span;
    }

    public void setSpan(String span) {
        this.span = span;
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


    @Override
    public String toString() {
        JSONObject obj = new JSONObject();
        try{
            obj.put("address",address);
            obj.put("city",city);
            obj.put("appPackageName",appPackageName);
            obj.put("appCerSha1",appCerSha1);
        }catch (Exception e){
        }
        return obj.toString();
    }
}
