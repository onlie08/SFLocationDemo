/*
 *
 */
package com.sfmap.api.location.client.bean;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author joseph
 */
public class AddressBean {
    @SerializedName("region")
    private String region;
    @SerializedName("county")
    private String county;
    @SerializedName("city")
    private String city;
    @SerializedName("street")
    private String street;
    @SerializedName("street_number")
    private String street_number;
    @SerializedName("country")
    private String country;
    @SerializedName("adcode")
    private int adcode;

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreet_number() {
        return street_number;
    }

    public void setStreet_number(String street_number) {
        this.street_number = street_number;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getAdcode() {
        return adcode;
    }

    public void setAdcode(int adcode) {
        this.adcode = adcode;
    }
}
