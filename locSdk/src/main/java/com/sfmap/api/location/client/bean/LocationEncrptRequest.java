package com.sfmap.api.location.client.bean;

import com.google.gson.annotations.SerializedName;

/**
 * 
 */
public class LocationEncrptRequest {
    @SerializedName("src")
    private String src;
    @SerializedName("sreq")
    private String sreq;
    @SerializedName("ak")
    private String ak ="c55e6757d8174a4a9857f5eba3720bc6";
    
    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getSreq() {
        return sreq;
    }

    public void setSreq(String sreq) {
        this.sreq = sreq;
    }

    public String getAk() {
        return ak;
    }

    public void setAk(String ak) {
        this.ak = ak;
    }
    
    
    
    
}
