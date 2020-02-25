package com.example.Demo.ui.model.request;

public class AddressRequestModel {
    private String city;
    private String country;
    private String streetName;
    private String postelCode;
    private String type;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getPostelCode() {
        return postelCode;
    }

    public void setPostelCode(String postelCode) {
        this.postelCode = postelCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
