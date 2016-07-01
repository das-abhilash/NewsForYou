package com.example.abhilash.newsforyou.API;

import android.os.Parcel;
import android.os.Parcelable;
import android.renderscript.Sampler;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abhilash on 5/24/2016.
 */
public class News implements Parcelable {


    @SerializedName("_type")
    private String type;
    @SerializedName("value")
    private List<Value> value = new ArrayList<Value>();

    protected News(Parcel in) {
        this();
        type = in.readString();
        in.readTypedList(value,Value.CREATOR);
    }


    public static final Creator<News> CREATOR = new Creator<News>() {
        @Override
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };

   /* public News(Parcel in) {

    }*/


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }

    public List<Value> getValue() {
        return value;
    }

    public void setValue(List<Value> value) {
        this.value = value;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeTypedList(value);
    }

    public News(){
        value = new ArrayList<Value>();

    }

}
