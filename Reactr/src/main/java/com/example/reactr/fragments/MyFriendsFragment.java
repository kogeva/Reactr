package com.example.reactr.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
    private EditText searchText;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.my_friends_layout, container, false);
        myFriendList = (ListView) view.findViewById(R.id.my_friends_list);
        searchText=(EditText)view.findViewById(R.id.editText);

        searchText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String str= String.valueOf(searchText.getText());
                ArrayList<FriendEntity> filterList=new ArrayList<FriendEntity>();
                for(int i=0;i<friends.size();i++){
                    if(friends.get(i).getUsername().indexOf(str)!=-1){
                        filterList.add(friends.get(i));

                    }

                }
                myFrendsAdapter = new MyFrendsAdapter(getActivity(),filterList);
                mainHandler.post(updateFrendlist);

            }
        });

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
            ReactrBase.switchFraagment(getSherlockActivity(), new MyFriendsFragment());
        }
        return true;
    }
}
