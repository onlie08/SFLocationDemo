package com.sfmap.api.location.client.bean;

import com.google.gson.annotations.SerializedName;

public class RegeoRequest {
    @SerializedName("latitude")
    public double latitude;
    @SerializedName("longitude")
    public double longitude;
    @SerializedName("appPackageName")
    public String appPackageName;
    @SerializedName("appCerSha1")
    public String appCerSha1;
    @SerializedName("ak")
    public String apiKey;
}
