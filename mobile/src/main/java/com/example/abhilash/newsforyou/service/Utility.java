package com.example.abhilash.newsforyou.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Switch;

/**
 * Created by Abhilash on 7/1/2016.
 */
public class Utility {
    public static String getCountry(Context context){
        String Country;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String market = prefs.getString("country", "en-IN");
        switch (market){
            case " en-IN" :
                Country="India";
                break;
            case "en-AU":
                Country="Australia";
                break;
            case  "en-CA" :
                Country="Canada";
                break;
            case "en-US" :
                Country="US";
                break;
            case "en-GB" :
                Country="UK";
                break;
            default:
                Country="India";
        }

        return Country;
    }
}
