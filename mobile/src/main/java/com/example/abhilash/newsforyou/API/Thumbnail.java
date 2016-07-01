package com.example.abhilash.newsforyou.API;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Abhilash on 5/24/2016.
 */
public class Thumbnail {


    @SerializedName("contentUrl")
    private String contentUrl;
    @SerializedName("width")
    private Integer width;
    @SerializedName("height")
    private Integer height;


    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }


    public Integer getHeight() {
        return height;
    }


    public void setHeight(Integer height) {
        this.height = height;
    }
}
