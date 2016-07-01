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
            case "es-AR":
                Country="Australia";
                break;
            case  "en-AU" :
                Country="Australia";
                break;
            case " de-AT" :
                Country="Austria";
                break;
            case "nl-BE" :
                Country="Belgium";
                break;
            case " fr-BE" :
                Country="Belgium";
                break;
            case "pt-BR" :
                Country="Brazil";
                break;
            case  "en-CA" :
                Country="Canada";
                break;
            case  "fr-CA" :
                Country="Canada";
                break;
            default:
                Country="India";
        }

        return Country;
    }
}
