package com.example.abhilash.newsforyou.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.gcm.TaskParams;

/**
 * Created by Abhilash on 5/25/2016.
 */
public class NewsIntentService extends IntentService {
public NewsIntentService (){
    super(NewsIntentService.class.getName());
}
    public NewsIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        TwitterTaskService twitterTaskService = new TwitterTaskService(this);
        Bundle args = new Bundle();
        if (intent.getStringExtra("tag").equals("add")){
            args.putString("tag", "add");
        }

        else if (intent.getStringExtra("tag").equals("search")){
            args.putString("tag", "search");
        }


            args.putString("SerachQuery", intent.getStringExtra("SerachQuery"));

        twitterTaskService.onRunTask(new TaskParams("SerachQuery", args));

    }
}
