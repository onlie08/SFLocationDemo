/*
 * Mapqoo 2017
 */
package com.sfmap.api.location.client.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Wifi 热点信息
 * @author joseph
 */
public class WifiInfoBean {
    @SerializedName("mac")
    private String mac;
    @SerializedName("name")
    private String name;
    @SerializedName("signal")
    private short signal;

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short getSignal() {
        return signal;
    }

    public void setSignal(short signal) {
        this.signal = signal;
    }

}
