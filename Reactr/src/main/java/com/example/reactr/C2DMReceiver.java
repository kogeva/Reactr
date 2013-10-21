package com.example.reactr;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.example.reactr.fragments.MailBoxFragment;
import com.example.reactr.reactr.models.MessageEntity;
import com.google.android.c2dm.C2DMBaseReceiver;

import java.util.ArrayList;

import reactr.adaptor.MessageAdapter;
import reactr.network.ReactorApi;


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
        MessageAdapter messageAdapter = MailBoxFragment.getAdapter();
        if (messageAdapter != null)
            new LoadNewMessageAsyncTask(messageAdapter).execute();

        if(!MainActivity.isRunning())
        {
            String data = receiveIntent.getStringExtra("message");
            String photo = receiveIntent.getStringExtra("photo");
            String reactionPhoto = receiveIntent.getStringExtra("reactionPhoto");
            String fromUser = receiveIntent.getStringExtra("senderId");
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
                intent.putExtra("from_user", fromUser);
                intent.putExtra("photo", photo);

                NotificationManager mManager = (NotificationManager)
                        getSystemService(Context.NOTIFICATION_SERVICE);
                //****************
                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_large);

                Notification.Builder nb = new Notification.Builder(context)
                        .setContentTitle("Reactr")
                        .setContentText(data)
                        .setAutoCancel(true)
                        .setLargeIcon(largeIcon)
                        .setSmallIcon(R.drawable.ic_launcher_small)
                        .setTicker(data)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setContentIntent(PendingIntent.getActivity(this.getBaseContext(), 0,
                        intent, PendingIntent.FLAG_CANCEL_CURRENT));

                Notification notification = nb.build();
                notification.flags |= Notification.FLAG_AUTO_CANCEL;

                NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                nm.notify(0, notification);
            }
        }
    }

    class LoadNewMessageAsyncTask extends AsyncTask<Integer, Void, ArrayList<MessageEntity>> {

        MessageAdapter adapter;
        ReactorApi api = MainActivity.reactorApi;

        LoadNewMessageAsyncTask(MessageAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        protected ArrayList<MessageEntity> doInBackground(Integer... voids) {
            return api.getMessages(0, 15);
        }

        @Override
        protected void onPostExecute(ArrayList<MessageEntity> messageEntities) {
            adapter.refreshList(messageEntities);
            ReactrBase.hideLoader();
        }
    }
}