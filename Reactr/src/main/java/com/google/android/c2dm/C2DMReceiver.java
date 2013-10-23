package com.google.android.c2dm;
import com.jelvix.reactr.MainActivity;
import com.jelvix.reactr.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class C2DMReceiver extends C2DMBaseReceiver {
    public C2DMReceiver(){
        super("<yourmail>@gmail.com");
    }

    @Override
    public void onRegistered(Context context, String registrationId) {
        Log.w("onRegistered", registrationId);
    }

    @Override
    public void onUnregistered(Context context) {
        Log.w("onUnregistered", "");
    }

    @Override
    public void onError(Context context, String errorId) {
        Log.w("onError", errorId);
    }

    @Override
    protected void onMessage(Context context, Intent receiveIntent)
    {
        String data = receiveIntent.getStringExtra("message");
        if(data != null)
        {
            Log.w("C2DMReceiver", data);

            Intent intent = new Intent(this,MainActivity.class);
            intent.putExtra("message", data);

            NotificationManager mManager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = new Notification(R.drawable.ic_launcher,
                    data, System.currentTimeMillis());
            notification.setLatestEventInfo(context,"Reactr","Hello world",
                    PendingIntent.getActivity(this.getBaseContext(), 0,
                            intent,PendingIntent.FLAG_CANCEL_CURRENT));
            mManager.notify(0, notification);
        }
    }
}