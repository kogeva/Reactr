package com.example.reactr.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.reactr.MainActivity;
import com.example.reactr.R;
import com.example.reactr.ReactrBase;
import com.example.reactr.reactr.models.FriendEntity;

import java.util.ArrayList;
import java.util.HashMap;

import reactr.adaptor.FriendListForMessageAdapter;
import reactr.adaptor.MyFrendsAdapter;
import reactr.network.ReactorApi;
import reactr.utils.FriendsDBManager;

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
    private EditText selectFriends;
    private ArrayList<FriendEntity> friends;
    boolean isNowOnline;

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
        selectFriends = (EditText) view.findViewById(R.id.editTextInSelectFriends);

        selectFriends.setOnFocusChangeListener( new MyFocusChangeListener());
        selectFriends.addTextChangedListener(new MyTextWatcher());
        sendMessageButton.setOnClickListener(sendClickListener);
        closeButton = (ImageButton) view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(closeClick);

        actionBarView = getSherlockActivity().getSupportActionBar().getCustomView();
        ((TextView) actionBarView.findViewById(R.id.barTitle)).setText("SEND TO...");
        ((ImageButton) actionBarView.findViewById(R.id.barItem)).setVisibility(View.VISIBLE);
        ((ImageButton) actionBarView.findViewById(R.id.barItem)).setImageResource(R.drawable.add_friend_btn);
        ((ImageButton) actionBarView.findViewById(R.id.barItem)).setOnClickListener(goToAddFriendClick);
        ((ImageButton) actionBarView.findViewById(R.id.toggleMenu)).setImageResource(R.drawable.to_menu);
        ((ImageButton) actionBarView.findViewById(R.id.toggleMenu)).setOnClickListener(((MainActivity) getSherlockActivity()).toogleMenu);

        isNowOnline=ReactrBase.isOnline(getSherlockActivity());
        handler = new Handler();
        contacts = ReactrBase.getContacts(getActivity());
        if(isNowOnline)
        {
            ReactrBase.showLoader(getSherlockActivity());
        new Thread(new Runnable() {
            @Override
            public void run() {
                api = ((MainActivity) getSherlockActivity()).getReactorApi();
                //**********
                FriendsDBManager friendsDBManager = new FriendsDBManager(getSherlockActivity());
                friends = ReactrBase.addInFriendContactName(api.getFriends(), contacts, friendsDBManager);
                //************
                friendListForMessageAdapter = new FriendListForMessageAdapter(getSherlockActivity(), friends);
                handler.post(updateFrendList);
            }
        }).start();
        }
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
            if(isNowOnline)
            {
            final String friendIds = friendListForMessageAdapter.getFriendIds();
            Log.d("latuhov", "friends "+friendIds);
            if(friendIds.equals(""))
                Toast.makeText(getActivity().getBaseContext(), "Choose at least one friend!", Toast.LENGTH_SHORT).show();
            else{
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
            }
        }
    };

    View.OnClickListener closeClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ReactrBase.switchFraagment(getSherlockActivity(), new CreatePhotoFragment());
        }
    };

    private class MyTextWatcher implements TextWatcher {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
           //**************************
            String str = String.valueOf(selectFriends.getText()).toLowerCase();
            ArrayList<FriendEntity> filterList = new ArrayList<FriendEntity>();
            for (int i = 0; i < friends.size(); i++) {
                if (friends.get(i).getUsername().toLowerCase().indexOf(str) != -1) {
                    filterList.add(friends.get(i));
                }
            }
            friendListForMessageAdapter = new FriendListForMessageAdapter(getSherlockActivity(), filterList);
           //***************************
            handler.post(updateFrendList);
        }
    }

    private class MyFocusChangeListener implements View.OnFocusChangeListener {

        public void onFocusChange(View v, boolean hasFocus){
            if(v.getId() == R.id.editText && !hasFocus) {
                InputMethodManager imm =  (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }

    View.OnClickListener goToAddFriendClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(isNowOnline)
            ReactrBase.switchFraagment(getSherlockActivity(), new FriendsFragment(photo, text));
        }
    };

}