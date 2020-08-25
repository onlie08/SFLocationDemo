/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfmap.api.services.route;

import org.json.JSONObject;

import java.io.Serializable;

public class XmlBean implements Serializable {
    private static final long serialVersionUID = 13454353651432431L;

    /**
     * xml格式字符串
     */
    private String soapContent;

    private String routeType;

    /**
     * 包名
     */
    private String appPackageName;

    /**
     * sha1
     */
    private String appCerSha1;


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

    public String getSoapContent() {
        return soapContent;
    }

    public void setSoapContent(String soapContent) {
        this.soapContent = soapContent;
    }

    public String getRouteType() {
        return routeType;
    }

    public void setRouteType(String routeType) {
        this.routeType = routeType;
    }

    @Override
    public String toString() {
        JSONObject obj = new JSONObject();
        try{
            //如果进入此分支，说明是获取服务器上的样式
            obj.put("soapContent",soapContent);
            obj.put("routeType",routeType);
            obj.put("appPackageName",appPackageName);
            obj.put("appCerSha1",appCerSha1);

        }catch (Exception e){
        }
        return obj.toString();
    }
}
