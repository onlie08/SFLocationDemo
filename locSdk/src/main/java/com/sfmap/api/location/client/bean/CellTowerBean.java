/*
 * 
 */
package com.sfmap.api.location.client.bean;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author joseph
 */
public class CellTowerBean {
    @SerializedName("mcc")
    private int mcc;
    @SerializedName("mnc")
    private int mnc;
    @SerializedName("lac")
    private int lac;
    @SerializedName("cell_id")
    private int cell_id;
    @SerializedName("signalstrength")
    private short signalstrength;
    @SerializedName("timingadvance")
    private int timingadvance;
    @SerializedName("time")
    private int time;

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

    public int getCell_id() {
        return cell_id;
    }

    public void setCell_id(int cell_id) {
        this.cell_id = cell_id;
    }

    public short getSignalstrength() {
        return signalstrength;
    }

    public void setSignalstrength(short signalstrength) {
        this.signalstrength = signalstrength;
    }

    public int getTimingadvance() {
        return timingadvance;
    }

    public void setTimingadvance(int timingadvance) {
        this.timingadvance = timingadvance;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public boolean isSimilar(CellTowerBean other) {
        if(other == null) {
            return false;
        }

        return this.mcc == other.mcc &&
                this.mnc == other.mnc &&
                this.lac == other.lac &&
                this.cell_id == other.cell_id;
    }
}
