package com.example.reactr.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.reactr.MainActivity;
import com.example.reactr.R;
import com.example.reactr.ReactrBase;
import com.example.reactr.reactr.models.FriendEntity;

import java.util.ArrayList;
import java.util.HashMap;

import reactr.adaptor.MyFrendsAdapter;
import reactr.network.ReactorApi;

public class MyFriendsFragment extends SherlockFragment{

    private ListView myFriendList;
    private ArrayList<FriendEntity> friends;
    private MyFrendsAdapter myFrendsAdapter;
    private ReactorApi api;
    private Handler mainHandler;
    private HashMap<Long, String> contacts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.my_friends_layout, container, false);
        myFriendList = (ListView) view.findViewById(R.id.my_friends_list);

        setHasOptionsMenu(true);
        mainHandler = new Handler();
        contacts = ReactrBase.getContacts(getActivity());
        api = ((MainActivity) getSherlockActivity()).getReactorApi();
        ReactrBase.showLoader(getSherlockActivity());
        new Thread(new Runnable() {
            @Override
            public void run() {

                friends = ReactrBase.addInFriendContactName(api.getFriends(), contacts);
                myFrendsAdapter = new MyFrendsAdapter(getActivity(),friends);
                mainHandler.post(updateFrendlist);
            }
        }).start();


        return view;
    }

    private Runnable updateFrendlist = new Runnable() {
        @Override
        public void run() {
            myFriendList.setAdapter(myFrendsAdapter);
            ReactrBase.hideLoader();
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add("Add Friend")
                .setIcon(R.drawable.rsz_add_friend_white).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == 0)
        {
            ReactrBase.switchFraagment(getSherlockActivity(), new FriendsFragment());
        }
        return true;
    }
}
