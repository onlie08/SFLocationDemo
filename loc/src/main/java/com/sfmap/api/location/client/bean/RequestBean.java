/*
 * 
 */
package com.sfmap.api.location.client.bean;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author joseph
 */
public class RequestBean {

    private static final int REGEO_OFF = 0;
    private static final int REGEO_ON = 1;

    private static final int WGS_84 = 0;
    private static final int GCJ_02 = 1;

    @SerializedName("mnctype")
    private String mnctype;
    @SerializedName("netType")
    private int netType;
    @SerializedName("celltowers")
    private List<CellTowerBean> celltowers;
    @SerializedName("wifilist")
    private List<WifiDeviceBean> wifilist;
    @SerializedName("appPackageName")
    private String appPackageName;
    @SerializedName("appCerSha1")
    private String appCerSha1;
    @SerializedName("opt")
    private int reGeoCodeOn = REGEO_OFF;
    @SerializedName("gcj02")
    private int coordType = GCJ_02;

    public void setNeedAddress(boolean needAddress) {
        reGeoCodeOn = needAddress ? REGEO_ON : REGEO_OFF;
    }

    public void setUseGcj02(boolean useGcj02) {
        this.coordType = useGcj02 ? GCJ_02 : WGS_84;
    }

    public int getNetType() {
        return netType;
    }

    public void setNetType(int netType) {
        this.netType = netType;
    }

    public String getMnctype() {
        return mnctype;
    }

    public void setMnctype(String mnctype) {
        this.mnctype = mnctype;
    }

    public List<CellTowerBean> getCelltowers() {
        return celltowers;
    }

    public void setCelltowers(List<CellTowerBean> celltowers) {
        this.celltowers = celltowers;
    }

    public List<WifiDeviceBean> getWifilist() {
        return wifilist;
    }

    public void setWifilist(List<WifiDeviceBean> wifilist) {
        this.wifilist = wifilist;
    }

    public void purgeWifiList() {
        if(wifilist != null) {
            WifiDeviceBean wifi = wifilist.get(0);
            wifilist.clear();
            wifilist.add(wifi);
        }
    }

    public boolean isValid() {
        return hasWifi() || hasCell();
    }

    private boolean hasCell() {
        return celltowers != null && !celltowers.isEmpty();
    }

    private boolean hasWifi() {
        return wifilist != null && !wifilist.isEmpty();
    }

    public boolean isSimilar(RequestBean other) {
        if(other == null) {
            return false;
        }

        if(celltowers == null && other.celltowers != null) {
            return false;
        }

        if(celltowers != null && other.celltowers == null) {
            return false;
        }

        boolean cellSimilar = false;
        if(other.celltowers != null && celltowers != null ) {
            //假设只有一个基站
            cellSimilar = celltowers.get(0).isSimilar(other.celltowers.get(0));
        } else {
            cellSimilar = true; //both have no cell Info
        }

        if(!cellSimilar) {
            return false;
        }


        if(wifilist == null || other.wifilist == null ||
                wifilist.isEmpty() || other.wifilist.isEmpty()) {
            return false;
        }

        int total = wifilist.size();
        int equals = 0;
        for(WifiDeviceBean wifi : wifilist) {
            if(other.wifilist.contains(wifi)) {
                equals++;
            }
        }

        return total > 5 && equals > total * 0.8;


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
}
