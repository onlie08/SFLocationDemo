package com.sfmap.api.maps.model;

import org.json.JSONObject;

import java.io.Serializable;

public class TmcBean implements Serializable {
    private static final long serialVersionUID = 13454353651432432L;

    //多个以分号分开
    private String mesh;

    /**
     * 包名
     */
    private String appPackageName;

    /**
     * sha1
     */
    private String appCerSha1;

    public String getMesh() {
        return mesh;
    }

    public void setMesh(String mesh) {
        this.mesh = mesh;
    }


    public TmcBean() {
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
            //如果进入此分支，说明是获取服务器上的样式
            obj.put("mesh",mesh);
            obj.put("appPackageName",appPackageName);
            obj.put("appCerSha1",appCerSha1);

        }catch (Exception e){
        }
        return obj.toString();
    }

}

