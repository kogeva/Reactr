package com.example.reactr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

import reactr.network.ReactorApi;


public class LoadActivity extends Activity
{

    private ReactorApi reactorApi;
    private HashMap<String, String> preference;
    private Handler handler;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler();
        preference = ReactrBase.getAppProperty(getApplicationContext());

        /////

    //    ConnectivityManager connectivityManager = (ConnectivityManager)
     //           getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE );
    //    NetworkInfo activeNetInfoGSM = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
   //     NetworkInfo activeNetInfoWIFI = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
   //     boolean isConnected = (activeNetInfoGSM != null) || (activeNetInfoWIFI!=null);
       // if (isOnline(getBaseContext()))
       // {
           // Log.i("INET", "connected" + isConnected);
        new Thread(new Runnable() {
            @Override
            public void run() {
                reactorApi = ReactorApi.init(new Integer(preference.get("user_id")), preference.get("session_hash"));
                if (reactorApi.getFriends() != null && ReactrBase.isOnline(LoadActivity.this))
                {
                    switchActivity("MainActivity");
                } else {
                    switchActivity("StartActivity");
                }
                }


        }).start();
        setContentView(R.layout.load_layout);
          //  Toast.makeText(getBaseContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
      //  }
      /*  else
        {
            Toast.makeText(getBaseContext(), "No internet!", Toast.LENGTH_SHORT).show();
           // setContentView(R.layout.load_layout);
         finish();
        }*/
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
