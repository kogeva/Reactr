package com.example.reactr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import java.util.HashMap;

import reactr.network.ReactorApi;


public class LoadActivity extends Activity
{

    private ReactorApi reactorApi;
    private HashMap<String, String> preference;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load_layout);
        handler = new Handler();

        preference = ReactrBase.getAppProperty(getApplicationContext());

        new Thread(new Runnable() {
            @Override
            public void run() {
                reactorApi = ReactorApi.init(new Integer(preference.get("user_id")), preference.get("session_hash"));
                if (reactorApi.getFriends() != null)
                {
                    switchActivity("MainActivity");
                } else {
                    switchActivity("StartActivity");
                }
            }
        }).start();
    }

    private void switchActivity(String activityName)
    {
        if (activityName.equals("MainActivity")) {
            startActivity(new Intent(LoadActivity.this, MainActivity.class));

        } else if (activityName.equals("StartActivity")) {
            startActivity(new Intent(LoadActivity.this, StartActivity.class));
        }
    }
}
