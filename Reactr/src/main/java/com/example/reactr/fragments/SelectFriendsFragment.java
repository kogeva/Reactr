package com.example.reactr.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.reactr.MainActivity;
import com.example.reactr.R;
import com.example.reactr.ReactrBase;

import java.util.HashMap;

import reactr.adaptor.FriendListForMessageAdapter;
import reactr.network.ReactorApi;

public class SelectFriendsFragment extends SherlockFragment {

    private Bitmap photo;
    private String text;
    private ListView friendList;
    private ImageButton sendMessageButton;
    private ImageButton closeButton;
    private FriendListForMessageAdapter friendListForMessageAdapter;
    private HashMap<Long, String> contacts;
    private ReactorApi api;
    private Handler handler;
    private View actionBarView;

    public SelectFriendsFragment(Bitmap photo, String text)
    {
        this.photo = photo;
        this.text = text;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friend_list_for_message, container, false);
        friendList = (ListView) view.findViewById(R.id.friend_list);
        sendMessageButton = (ImageButton) view.findViewById(R.id.sendButton);
        sendMessageButton.setOnClickListener(sendClickListener);
        closeButton = (ImageButton) view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(closeClick);

        actionBarView = getSherlockActivity().getSupportActionBar().getCustomView();
        ((TextView) actionBarView.findViewById(R.id.barTitle)).setText("SEND TO...");

        handler = new Handler();

        contacts = ReactrBase.getContacts(getActivity());

        ReactrBase.showLoader(getSherlockActivity());
        new Thread(new Runnable() {
            @Override
            public void run() {
                api = ((MainActivity) getSherlockActivity()).getReactorApi();
                friendListForMessageAdapter = new FriendListForMessageAdapter(getSherlockActivity(), ReactrBase.addInFriendContactName(api.getFriends(), contacts));
                handler.post(updateFrendList);
            }
        }).start();

        return view;
    }

    private Runnable updateFrendList = new Runnable() {
        @Override
        public void run() {
            friendList.setAdapter(friendListForMessageAdapter);
            ReactrBase.hideLoader();
        }
    };

    View.OnClickListener sendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String friendIds = friendListForMessageAdapter.getFriendIds();
            ReactrBase.showLoader(getSherlockActivity());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    api.sendMessages(friendIds, text, photo, null);
                    ReactrBase.hideLoader();
                    ((MainActivity) getSherlockActivity()).switchContent(new MailBoxFragment());
                }
            }).start();
        }
    };

    View.OnClickListener closeClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ReactrBase.switchFraagment(getSherlockActivity(), new CreatePhotoFragment());
        }
    };
}