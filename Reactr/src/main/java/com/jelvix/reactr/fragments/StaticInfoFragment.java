package com.jelvix.reactr.fragments;

/**
 * Created by Kykmyrna on 17.09.13.
 */


import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.jelvix.reactr.MainActivity;
import com.jelvix.reactr.R;
import com.jelvix.reactr.ReactrBase;
import com.jelvix.reactr.reactr.models.FriendEntity;
import com.jelvix.reactr.reactr.models.MessageEntity;
import com.jelvix.reactr.reactr.models.ReactrConstants;

import java.util.ArrayList;
import java.util.HashMap;

import reactr.adaptor.MessageAdapter;
import reactr.network.ReactorApi;

//CHANGES
//мой фрагмент для отображения статической информации
public class StaticInfoFragment extends SherlockFragment {

    private ReactorApi api;
    private ArrayList<MessageEntity> messageArray;
    private ArrayList<FriendEntity> friendEntities;
    private MessageAdapter adapter;
    private ListView messageList;
    private WebView tvPage;
    final String mimeType = "text/html";
    final String encoding = "UTF-8";
    String parameter = " ", toWV = "";
    private MainActivity ma;
    private HashMap<String, String> hm_st_title;
    private View actionBarView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.st_info_fragment, container, false);

        tvPage = (WebView) view.findViewById(R.id.wv_st_info);
        ma = (MainActivity) getSherlockActivity();
        hm_st_title=new HashMap<String, String>();
        hm_st_title.put(ReactrConstants.ABOUT_REACTR,"ABOUT");
        hm_st_title.put(ReactrConstants.TERMS,"TERMS");
        hm_st_title.put(ReactrConstants.PRIVACY,"PRIVACY");
        hm_st_title.put(ReactrConstants.CONTACT_US,"CONTACT US");
        api = ma.getReactorApi();
        if(ma.getSizeStInfo()==0)
        {
            new RequestTask().execute();
        }
        else
        {
            toWV=ma.getStInfoByParameter(parameter);
            tvPage.loadDataWithBaseURL("", toWV, mimeType, encoding, "");
        }
        actionBarView = getSherlockActivity().getSupportActionBar().getCustomView();
        ((TextView) actionBarView.findViewById(R.id.barTitle)).setText(hm_st_title.get(parameter));
        ((ImageButton) actionBarView.findViewById(R.id.barItem)).setVisibility(View.INVISIBLE);
        ((ImageButton) actionBarView.findViewById(R.id.toggleMenu)).setImageResource(R.drawable.to_menu);
        ((ImageButton) actionBarView.findViewById(R.id.toggleMenu)).setOnClickListener(((MainActivity) getSherlockActivity()).toogleMenu);


        return view;
    }

    Runnable updateMessageList = new Runnable() {
        @Override
        public void run() {
            messageList.setAdapter(adapter);
        }
    };

    public StaticInfoFragment(String param){
        super();
        parameter=param;
    }
     //**********************************************************
    class RequestTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            try {

                ma.loadStInfo();
                toWV=ma.getStInfoByParameter(parameter);
                tvPage.loadDataWithBaseURL("", toWV, mimeType, encoding, "");
            } catch (Exception e) {
                System.out.println("Exp=" + e);
            }
            return null;
        }
         @Override
         protected void onPreExecute() {
             ReactrBase.showLoader(getSherlockActivity());
             super.onPreExecute();
         }

        @Override
        protected void onPostExecute(String result) {
            ReactrBase.hideLoader();
            super.onPostExecute(result);
        }


    }

}