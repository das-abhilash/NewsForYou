package com.example.abhilash.newsforyou.API;

/**
 * Created by Abhilash on 5/23/2016.
 */


import com.google.gson.annotations.SerializedName;

public class About {
    @SerializedName("readLink")
    private String readLink;

    @SerializedName("name")
    private String name;


    public String getReadLink() {
        return readLink;
    }


    public void setReadLink(String readLink) {
        this.readLink = readLink;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }}
    