/*
 * 
 */
package com.sfmap.api.location.client.bean;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author joseph
 */
public class LocationBean {
    @SerializedName("address")
    private AddressBean address;
    @SerializedName("addressDescription")
    private String addressDescription;
    @SerializedName("longitude")
    private double longitude;
    @SerializedName("latitude")
    private double latitude;
    @SerializedName("accuracy")
    private int accuracy;
    @SerializedName("validity")
    private byte validity;
    @SerializedName("time")
    private long time;

    public byte getValidity() {
        return validity;
    }

    public void setValidity(byte validity) {
        this.validity = validity;
    }
    
    public AddressBean getAddress() {
        return address;
    }

    public void setAddress(AddressBean address) {
        this.address = address;
    }

    public String getAddressDescription() {
        return addressDescription;
    }

    public void setAddressDescription(String addressDescription) {
        this.addressDescription = addressDescription;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public boolean isSuccess() {
        return latitude != 0 && longitude != 0;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
