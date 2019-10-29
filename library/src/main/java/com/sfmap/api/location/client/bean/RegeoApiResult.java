package com.sfmap.api.location.client.bean;

import com.google.gson.annotations.SerializedName;

public class RegeoApiResult {
    @SerializedName("result")
    public RegeoEncryptData regeoEncryptData;
    @SerializedName("status")
    public int status;

    public boolean isSuccess(){
        if(status == 0){
            return true;
        }
        return false;
    }
}
