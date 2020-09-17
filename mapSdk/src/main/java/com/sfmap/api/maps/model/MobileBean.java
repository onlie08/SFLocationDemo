package com.sfmap.api.maps.model;


import org.json.JSONObject;
import java.io.Serializable;


/**
 * 外部参数对象
 *
 * @author 01373280 2018-08-07 09:52:33
 */

public class MobileBean implements Serializable {
    private static final long serialVersionUID = 13454353651432432L;
    private String appCerSha1;
    private String appPackageName;
    private String t;
    private String ent;
    private String sv;
    private String cv;
    private String name;
    private String mesh;
    private String type;

    public MobileBean() {
    }


    //wenbaolin 20180912 重构toString方法，由原来的拼接为json字符串调整为使用对象转换
    @Override
    public String toString() {
        JSONObject obj = new JSONObject();

        try{
            if(null == mesh || mesh.isEmpty()){
                //如果进入此分支，说明是获取服务器上的样式
                obj.put("type",type);
                obj.put("t",t);
                obj.put("name",name);
                obj.put("sv",sv);
                obj.put("cv",cv);
                obj.put("ent","2");
                obj.put("appPackageName",appPackageName);
                obj.put("appCerSha1",appCerSha1);
            }else {
                //请求分幅数据
                obj.put("type",type);
                obj.put("t",t);
                obj.put("mesh",mesh);
                obj.put("appPackageName",appPackageName);
                obj.put("appCerSha1",appCerSha1);
            }
        }catch (Exception e){
        }


        return obj.toString();
        //return "{\"type\":\""+type+"\",\"t\":\""+t+"\",\"mesh\":\""+mesh+"\",\"appPackageName\":\""+appPackageName+"\",\"appCerSha1\":\""+appCerSha1+"\"}";
    }


    public String getAppCerSha1() {
        return appCerSha1;
    }

    public void setAppCerSha1(String appCerSha1) {
        this.appCerSha1 = appCerSha1;
    }

    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    public String getEnt() {
        return ent;
    }

    public void setEnt(String ent) {
        this.ent = ent;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public String getCv() {
        return cv;
    }

    public void setCv(String cv) {
        this.cv = cv;
    }

    public String getSv() {
        return sv;
    }

    public void setSv(String sv) {
        this.sv = sv;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMesh() {
        return mesh;
    }

    public void setMesh(String mesh) {
        this.mesh = mesh;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}