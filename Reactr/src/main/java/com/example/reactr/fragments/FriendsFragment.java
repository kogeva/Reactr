package com.example.reactr.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TabHost;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.reactr.MainActivity;
import com.example.reactr.R;
import com.example.reactr.ReactrBase;
import com.example.reactr.reactr.models.FriendEntity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import reactr.adaptor.ContactsListAdaptor;
import reactr.adaptor.FriendsAddedAdapter;
import reactr.adaptor.SearchFriendAdapter;
import reactr.network.ReactorApi;


public class FriendsFragment extends SherlockFragment {

    private ReactorApi api;
    private TabHost tabHost;
    private ListView friendsAdded;
    private ExpandableListView friendsInContact;
    private ListView searchFriendsView;
    private ArrayList<FriendEntity> friendCollection;
    private ArrayList<FriendEntity> whoAddMe;
    private ArrayList<FriendEntity> searchFriendsCollection;
    private ArrayList<ArrayList<JSONObject>> contactUserGroups;
    private EditText searchEditText;
    private ContactsListAdaptor contactsListAdaptor;
    private FriendsAddedAdapter friendsAddedAdapter;
    private SearchFriendAdapter searchFriendAdapter;
    private HashMap<Long, String> contacts;
    private Handler uiHandler;
    private MainActivity mainActivity;

    public FriendsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friends_layout, container, false);
        uiHandler = new Handler();

        tabHost = (TabHost) view.findViewById(R.id.tabHost);
        friendsAdded = (ListView) view.findViewById(R.id.myFriends);
        friendsInContact = (ExpandableListView) view.findViewById(R.id.contactList);
        searchFriendsView = (ListView) view.findViewById(R.id.searchFriendsList);
        searchEditText = (EditText) view.findViewById(R.id.searchEditText);

        searchEditText.setOnKeyListener(searchFriendOutFocus);

        setHasOptionsMenu(true);
        tabHost.setup();
        setTabSelector("tag1", "FRIENDS", R.id.tab1);
        setTabSelector("tag2", "CONTACTS", R.id.tab2);
        setTabSelector("tag3", "SEARCH", R.id.tab3);
        tabHost.setCurrentTab(0);
        contacts = ReactrBase.getContacts(getActivity());

        final String contactStr = ReactrBase.getPhonesString(contacts);

        mainActivity = (MainActivity) getSherlockActivity();
        api = mainActivity.getReactorApi();

        ReactrBase.showLoader(getSherlockActivity());
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<FriendEntity> myFriends = api.getFriends();

                contactUserGroups = ReactrBase.buildDataForContactLIst(api.checkUserInSystem(contactStr), myFriends, contacts);

                contactsListAdaptor = new ContactsListAdaptor(getSherlockActivity(), contactUserGroups, api);

                friendCollection = ReactrBase.addInFriendContactName(myFriends, contacts);
                whoAddMe = ReactrBase.addInFriendContactName(api.getWhoAddMe(), contacts);
                whoAddMe = ReactrBase.mergeWhoAddMe(whoAddMe, friendCollection);
                friendsAddedAdapter = new FriendsAddedAdapter(getActivity(), whoAddMe);
                uiHandler.post(updateFriendList);
            }
        }).start();

        friendsAdded.setAdapter(friendsAddedAdapter);
        return view;
    }

    private void setTabSelector(String tag, String tabName, int layoutResource)
    {
        TabHost.TabSpec tabSpec = tabHost.newTabSpec(tag);
        tabSpec.setContent(layoutResource);
        tabSpec.setIndicator(tabName);
        tabHost.addTab(tabSpec);
    }

    private Runnable updateFriendList = new Runnable() {
        @Override
        public void run() {
            friendsAdded.setAdapter(friendsAddedAdapter);
            friendsInContact.setAdapter(contactsListAdaptor);
            friendsInContact.expandGroup(0);
            friendsInContact.expandGroup(1);
            ReactrBase.hideLoader();
        }
    };

    private Runnable updateSearchFrendList = new Runnable() {
        @Override
        public void run() {
            ReactrBase.hideLoader();
            searchFriendsView.setAdapter(searchFriendAdapter);
        }
    };

    Runnable searchFriends = new Runnable() {
        @Override
        public void run() {
            searchFriendsCollection = api.searchFriends(searchEditText.getText().toString());
            searchFriendAdapter = new SearchFriendAdapter(getSherlockActivity(),searchFriendsCollection, api);
            uiHandler.post(updateSearchFrendList);
        }
    };


    View.OnKeyListener searchFriendOutFocus = new View.OnKeyListener() {
        @Override
        public boolean onKey(View view, int keyCode, KeyEvent event) {

            if (view.getId() == searchEditText.getId())
            {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER))
                    ReactrBase.showLoader(getSherlockActivity());
                    new Thread(searchFriends).start();
            }
            return false;
        }
    };
}
