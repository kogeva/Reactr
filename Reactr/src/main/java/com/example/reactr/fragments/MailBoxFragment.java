package com.example.reactr.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.reactr.MainActivity;
import com.example.reactr.R;
import com.example.reactr.ReactrBase;
import com.example.reactr.reactr.models.FriendEntity;
import com.example.reactr.reactr.models.MessageEntity;

import java.util.ArrayList;

import reactr.adaptor.MessageAdapter;
import reactr.network.ReactorApi;


public class MailBoxFragment extends SherlockFragment {

    private ReactorApi api;
    private ArrayList<MessageEntity> messageArray;
    private ArrayList<FriendEntity> friendEntities;
    private Handler handler;
    private MessageAdapter adapter;
    private ListView messageList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mail_box_layout, container, false);
        messageList = (ListView) view.findViewById(R.id.message_list);


        handler = new Handler();
        MainActivity ma = (MainActivity) getSherlockActivity();
        api = ma.getReactorApi();
        ReactrBase.showLoader(getSherlockActivity());

        new Thread(new Runnable() {
            @Override
            public void run() {
                messageArray = api.getMessages();
                friendEntities = api.getFriends();
                adapter = new MessageAdapter(getSherlockActivity(), messageArray, friendEntities);
                handler.post(updateMessageList);
            }
        }).start();
        return view;
    }

    Runnable updateMessageList = new Runnable() {
        @Override
        public void run() {
            messageList.setAdapter(adapter);
            ReactrBase.hideLoader();
        }
    };
}
