package com.example.reactr.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
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

public class MyFriendsFragment extends SherlockFragment {

    private ListView myFriendList;
    private ArrayList<FriendEntity> friends;
    private MyFrendsAdapter myFrendsAdapter;
    private ReactorApi api;
    private Handler mainHandler;
    private HashMap<Long, String> contacts;
    private EditText searchText;
    private View actionBarView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.my_friends_layout, container, false);
        myFriendList = (ListView) view.findViewById(R.id.my_friends_list);
        searchText = (EditText) view.findViewById(R.id.editText);

        searchText.setOnFocusChangeListener( new MyFocusChangeListener());
        searchText.addTextChangedListener(new MyTextWatcher());

        actionBarView = getSherlockActivity().getSupportActionBar().getCustomView();
        ((TextView) actionBarView.findViewById(R.id.barTitle)).setText("FRIENDS");
        ((ImageButton) actionBarView.findViewById(R.id.barItem)).setVisibility(View.VISIBLE);
        ((ImageButton) actionBarView.findViewById(R.id.barItem)).setImageResource(R.drawable.add_friend_btn);
        ((ImageButton) actionBarView.findViewById(R.id.barItem)).setOnClickListener(goToAddFriendClick);

        ((ImageButton) actionBarView.findViewById(R.id.toggleMenu)).setImageResource(R.drawable.to_menu);
        ((ImageButton) actionBarView.findViewById(R.id.toggleMenu)).setPadding(10, 14, 43, 14);

        setHasOptionsMenu(true);
        mainHandler = new Handler();
        contacts = ReactrBase.getContacts(getActivity());
        api = ((MainActivity) getSherlockActivity()).getReactorApi();
        ReactrBase.showLoader(getSherlockActivity());
        new Thread(new Runnable() {
            @Override
            public void run() {
                friends = ReactrBase.addInFriendContactName(api.getFriends(), contacts);
                myFrendsAdapter = new MyFrendsAdapter(getActivity(), friends);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {
            ReactrBase.switchFraagment(getSherlockActivity(), new FriendsFragment());
        }
        return true;
    }

    View.OnClickListener goToAddFriendClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ReactrBase.switchFraagment(getSherlockActivity(), new FriendsFragment());
        }
    };

    private class MyFocusChangeListener implements View.OnFocusChangeListener {

        public void onFocusChange(View v, boolean hasFocus){
            if(v.getId() == R.id.editText && !hasFocus) {
                InputMethodManager imm =  (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }
    private class MyTextWatcher implements TextWatcher {
        public void afterTextChanged(Editable s) {
        }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String str = String.valueOf(searchText.getText()).toLowerCase();
        ArrayList<FriendEntity> filterList = new ArrayList<FriendEntity>();
        for (int i = 0; i < friends.size(); i++) {
            if (friends.get(i).getUsername().toLowerCase().indexOf(str) != -1) {
                filterList.add(friends.get(i));
            }
        }
        myFrendsAdapter = new MyFrendsAdapter(getActivity(), filterList);
        mainHandler.post(updateFrendlist);
    }
}
}
