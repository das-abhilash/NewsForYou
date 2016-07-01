package com.example.abhilash.newsforyou.API;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Abhilash on 6/10/2016.
 */
public class Provider implements Parcelable{

    @SerializedName("_type")
    private String type;
    @SerializedName("name")
    private String name;


    protected Provider(Parcel in) {
        type = in.readString();
        name = in.readString();
    }

    public static final Creator<Provider> CREATOR = new Creator<Provider>() {
        @Override
        public Provider createFromParcel(Parcel in) {
            return new Provider(in);
        }

        @Override
        public Provider[] newArray(int size) {
            return new Provider[size];
        }
    };

    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(name);
    }
}