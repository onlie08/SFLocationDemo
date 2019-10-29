/*
 * 
 */
package com.sfmap.api.location.client.bean;

import android.os.Build;
import android.os.SystemClock;

import com.google.gson.annotations.SerializedName;
import com.sfmap.api.location.SfMapLocation;
import com.sfmap.api.location.client.util.Utils;

/**
 *
 * @author joseph
 */
public class ResponseBean {
    @SerializedName("ErrCode")
    private String ErrCode;
    @SerializedName("access_token")
    private String access_token;
    @SerializedName("location")
    private LocationBean location;
    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrCode() {
        return ErrCode;
    }

    public void setErrCode(String ErrCode) {
        this.ErrCode = ErrCode;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public LocationBean getLocation() {
        return location;
    }

    public void setLocation(LocationBean location) {
        this.location = location;
    }

    public boolean isSuccess() {
        return ErrCode == null && location != null && location.isSuccess();
    }

    public static void main(String[] args) {

    }

    public SfMapLocation getSfLocation() { LocationBean locationBean = getLocation();
        if(locationBean == null) {
            return null;
        }
        SfMapLocation sfLocation = new SfMapLocation();
        sfLocation.setAccuracy(locationBean.getAccuracy());
        sfLocation.setLatitude(locationBean.getLatitude());
        sfLocation.setLongitude(locationBean.getLongitude());
        sfLocation.setTime(System.currentTimeMillis());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            sfLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        }
        if(locationBean.getTime() != 0){
            sfLocation.setTime(locationBean.getTime());
        }
        sfLocation.setAddress(locationBean.getAddressDescription());
        AddressBean addressBean = locationBean.getAddress();
        if(addressBean != null) {
            sfLocation.setRegion(addressBean.getRegion());
            sfLocation.setCountry(addressBean.getCountry());
            sfLocation.setCity(addressBean.getCity());
            sfLocation.setCounty(addressBean.getCounty());
            sfLocation.setStreet(addressBean.getStreet());
            sfLocation.setStreetNumber(addressBean.getStreet_number());
            sfLocation.setmAdcode(addressBean.getAdcode());
        }
        return sfLocation;
    }
}
