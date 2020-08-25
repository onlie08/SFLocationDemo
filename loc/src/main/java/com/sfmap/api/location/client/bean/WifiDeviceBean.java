/*
 * 
 */
package com.sfmap.api.location.client.bean;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author joseph
 */
public class WifiDeviceBean {
    @SerializedName("macaddress")
    private String macaddress;
    @SerializedName("time")
    private int time;
    @SerializedName("singalstrength")
    private short singalstrength;

    public String getMacaddress() {
        return macaddress;
    }

    public void setMacaddress(String macaddress) {
        this.macaddress = macaddress;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public short getSingalstrength() {
        return singalstrength;
    }

    public void setSingalstrength(short singalstrength) {
        this.singalstrength = singalstrength;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WifiDeviceBean that = (WifiDeviceBean) o;

        return macaddress != null ? macaddress.equals(that.macaddress) : that.macaddress == null;
    }

    @Override
    public int hashCode() {
        return macaddress != null ? macaddress.hashCode() : 0;
    }

}
