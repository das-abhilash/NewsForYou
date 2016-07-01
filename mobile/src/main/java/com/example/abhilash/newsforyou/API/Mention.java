package com.example.abhilash.newsforyou.API;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Abhilash on 6/10/2016.
 */
public class Mention {

    @SerializedName("name")
    private String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
