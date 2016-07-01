package com.example.abhilash.newsforyou.API;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abhilash on 5/24/2016.
 */
public class Value implements Parcelable {

    @SerializedName("name")
    private String name;
    @SerializedName("url")
    private String url;
    @SerializedName("image")
    private Image image;
    @SerializedName("description")
    private String description;
    @SerializedName("about")
    private List<About> about = new ArrayList<About>();
    @SerializedName("mentions")
    private List<Mention> mentions = new ArrayList<Mention>();
    @SerializedName("provider")
    private List<Provider> provider = new ArrayList<Provider>();
    @SerializedName("datePublished")
    private String datePublished;
    @SerializedName("category")
    private String category;


    protected Value(Parcel in) {
        name = in.readString();
        url = in.readString();
        description = in.readString();
        datePublished = in.readString();
        category = in.readString();
        in.readTypedList(provider,Provider.CREATOR);
    }

    public static final Creator<Value> CREATOR = new Creator<Value>() {
        @Override
        public Value createFromParcel(Parcel in) {
            return new Value(in);
        }

        @Override
        public Value[] newArray(int size) {
            return new Value[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public List<About> getAbout() {
        return about;
    }

    public void setAbout(List<About> about) {
        this.about = about;
    }


    public List<Mention> getMentions() {
        return mentions;
    }

    public void setMentions(List<Mention> mentions) {
        this.mentions = mentions;
    }


    public List<Provider> getProvider() {
        return provider;
    }


    public void setProvider(List<Provider> provider) {
        this.provider = provider;
    }

    public String getDatePublished() {
        return datePublished;
    }


    public void setDatePublished(String datePublished) {
        this.datePublished = datePublished;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(url);
        dest.writeString(description);
        dest.writeString(datePublished);
        dest.writeString(category);
        dest.readTypedList(provider, Provider.CREATOR);
    }
}




