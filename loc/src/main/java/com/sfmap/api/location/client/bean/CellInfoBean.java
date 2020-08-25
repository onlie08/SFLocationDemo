/*
 * Mapqoo 2017
 */
package com.sfmap.api.location.client.bean;

import com.google.gson.annotations.SerializedName;

/**
 * 基站信息
 *
 * @author joseph
 */
public class CellInfoBean {
    @SerializedName("mcc")
    private int mcc;
    @SerializedName("mnc")
    private int mnc;
    @SerializedName("lac")
    private int lac;
    @SerializedName("cellid")
    private int cellid;
    @SerializedName("signal")
    private short signal;
    @SerializedName("lng")
    private double lng;
    @SerializedName("lat")
    private double lat;

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public int getMcc() {
        return mcc;
    }

    public void setMcc(int mcc) {
        this.mcc = mcc;
    }

    public int getMnc() {
        return mnc;
    }

    public void setMnc(int mnc) {
        this.mnc = mnc;
    }

    public int getLac() {
        return lac;
    }

    public void setLac(int lac) {
        this.lac = lac;
    }

    public int getCellid() {
        return cellid;
    }

    public void setCellid(int cellid) {
        this.cellid = cellid;
    }

    public short getSignal() {
        return signal;
    }

    public void setSignal(short signal) {
        this.signal = signal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CellInfoBean that = (CellInfoBean) o;

        if (mcc != that.mcc) return false;
        if (mnc != that.mnc) return false;
        if (lac != that.lac) return false;
        return cellid == that.cellid;
    }

    @Override
    public int hashCode() {
        int result = (int) mcc;
        result = 31 * result + mnc;
        result = 31 * result + lac;
        result = 31 * result + cellid;
        return result;
    }

}
