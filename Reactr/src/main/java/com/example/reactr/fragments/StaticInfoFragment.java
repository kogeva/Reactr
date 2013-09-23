package com.example.reactr.fragments;

/**
 * Created by Kykmyrna on 17.09.13.
 */


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.reactr.MainActivity;
import com.example.reactr.R;
import com.example.reactr.reactr.models.FriendEntity;
import com.example.reactr.reactr.models.MessageEntity;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;

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
    private ProgressDialog dialog;
    MainActivity ma;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.st_info_fragment, container, false);

        tvPage = (WebView) view.findViewById(R.id.wv_st_info);
        ma = (MainActivity) getSherlockActivity();
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
        protected void onPostExecute(String result) {
            dialog.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(getSherlockActivity());
            dialog.setMessage("Loading...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            dialog.show();
            super.onPreExecute();
        }
    }

}