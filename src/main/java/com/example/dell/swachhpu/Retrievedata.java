package com.example.dell.swachhpu;

public class Retrievedata {

    public String name;
    public String Description;
    public String Department;
    public String Floor;
    public String Image ;
    Double longitude , latitude ;
    public String Status;
    public String Date;
    public String pId;

    public Retrievedata(String name, String description, String image, String status) {
        this.name = name;
        Description = description;
        Image = image;
        Status = status;
    }

    public String getpId() {
        return pId;
    }

    public String getDate() {
        return Date;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return Description;
    }

    public String getDepartment() {
        return Department;
    }

    public String getFloor() {
        return Floor;
    }

    public String getImage() {
        return Image;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public String getStatus() {
        return Status;
    }

    public Retrievedata(){}


}

