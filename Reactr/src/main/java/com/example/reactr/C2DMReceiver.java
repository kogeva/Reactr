package com.example.reactr;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.c2dm.C2DMBaseReceiver;


public class C2DMReceiver extends C2DMBaseReceiver {
    public C2DMReceiver(){
        super("ash@eyepinch.com");
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
        String photo = receiveIntent.getStringExtra("photo");
        String reactionPhoto = receiveIntent.getStringExtra("reactionPhoto");
        String messageId = receiveIntent.getStringExtra("message_id");
        String text = receiveIntent.getStringExtra("text");
        if(data != null)
        {
            Log.w("C2DMReceiver", data);

            Intent intent = new Intent(this,MainActivity.class);
            intent.putExtra("message", data);
            intent.putExtra("messageId", messageId);
            intent.putExtra("text", text);
            intent.putExtra("reactionPhoto", reactionPhoto);
            intent.putExtra("photo", photo);

            NotificationManager mManager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = new Notification(R.drawable.ic_launcher,
                    data, System.currentTimeMillis());
            notification.setLatestEventInfo(context,"Reactr",data,
                    PendingIntent.getActivity(this.getBaseContext(), 0,
                            intent, PendingIntent.FLAG_CANCEL_CURRENT));
            mManager.notify(0, notification);
        }
    }
}