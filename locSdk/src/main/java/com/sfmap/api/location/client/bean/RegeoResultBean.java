package com.sfmap.api.location.client.bean;

import com.google.gson.annotations.SerializedName;

public class RegeoResultBean {
    @SerializedName("status")
    public int status;
    @SerializedName("message")
    public String mesage;
    @SerializedName("location")
    public LocationBean locationBean;

}
