/*
 * 
 */
package com.sfmap.api.location.client.bean;

import com.google.gson.annotations.SerializedName;

/**
 * 数据库内对应的基站对象
 *
 * @author
 * @version 1.0
 */
public class CellBean {
    @SerializedName("mcc")
    public short mcc;
    @SerializedName("mnc")
    public short mnc;
    @SerializedName("lac")
    public int lac;
    @SerializedName("ci")
    public int ci;
    @SerializedName("lat")
    public int lat;
    @SerializedName("lng")
    public int lng;
    @SerializedName("acc")
    public short acc;
    @SerializedName("validity")
    public byte validity;

    public boolean isValid() {
        if (mcc > 0 && lac > 0 && ci > 0 && lng != 0 && lat != 0) {
            return true;
        }
        return false;
    }

    /**
     * 获取Key的字节数组表达
     *
     * @return Key的字节数组表达
     */
    public byte[] getKeyBytes() {
        byte[] rs = new byte[12];
        //
        rs[0] = (byte) (mcc & 0xFF);
        rs[1] = (byte) (mcc >> 8 & 0xFF);
        //
        rs[2] = (byte) (mnc & 0xFF);
        rs[3] = (byte) (mnc >> 8 & 0xFF);
        //
        rs[4] = (byte) (lac & 0xFF);
        rs[5] = (byte) (lac >> 8 & 0xFF);
        rs[6] = (byte) (lac >> 16 & 0xFF);
        rs[7] = (byte) (lac >> 24 & 0xFF);
        //
        rs[8] = (byte) (ci & 0xFF);
        rs[9] = (byte) (ci >> 8 & 0xFF);
        rs[10] = (byte) (ci >> 16 & 0xFF);
        rs[11] = (byte) (ci >> 24 & 0xFF);
        return rs;
    }

    /**
     * 获取Value的字节数组表达
     *
     * @return Value的字节数组表达
     */
    public byte[] getValueBytes() {
        byte[] rs = new byte[11];
        //
        rs[0] = (byte) (lat & 0xFF);
        rs[1] = (byte) (lat >> 8 & 0xFF);
        rs[2] = (byte) (lat >> 16 & 0xFF);
        rs[3] = (byte) (lat >> 24 & 0xFF);
        //
        rs[4] = (byte) (lng & 0xFF);
        rs[5] = (byte) (lng >> 8 & 0xFF);
        rs[6] = (byte) (lng >> 16 & 0xFF);
        rs[7] = (byte) (lng >> 24 & 0xFF);
        //
        rs[8] = (byte) (acc & 0xFF);
        rs[9] = (byte) (acc >> 8 & 0xFF);
        rs[10] = this.validity;
        return rs;
    }

    @Override
    public String toString() {
        return "CellBean{" + "mcc=" + mcc + ", mnc=" + mnc + ", lac=" + lac + ", ci=" + ci + ", lat=" + lat + ", lng=" + lng + ", acc=" + acc + ", validity=" + validity + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + this.mcc;
        hash = 17 * hash + this.mnc;
        hash = 17 * hash + this.lac;
        hash = 17 * hash + this.ci;
        return hash;
    }

    /**
     * 针对Key的比较
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CellBean other = (CellBean) obj;
        if (this.mcc != other.mcc) {
            return false;
        }
        if (this.mnc != other.mnc) {
            return false;
        }
        if (this.lac != other.lac) {
            return false;
        }
        if (this.ci != other.ci) {
            return false;
        }
        return true;
    }

    /**
     * 全部内容比较
     *
     * @param obj
     * @return
     */
    public boolean allEquals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CellBean other = (CellBean) obj;
        if (this.mcc != other.mcc) {
            return false;
        }
        if (this.mnc != other.mnc) {
            return false;
        }
        if (this.lac != other.lac) {
            return false;
        }
        if (this.ci != other.ci) {
            return false;
        }
        if (this.lat != other.lat) {
            return false;
        }
        if (this.lng != other.lng) {
            return false;
        }
        if (this.acc != other.acc) {
            return false;
        }
        if (this.validity != other.validity) {
            return false;
        }
        return true;
    }

}
