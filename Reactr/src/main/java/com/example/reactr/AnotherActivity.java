package com.example.reactr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.example.reactr.fragments.MyFriendsFragment;
import com.example.reactr.reactr.models.ReactrConstants;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.util.HashMap;


public class AnotherActivity extends Activity {
//    private String apiUrl = "http://api.reactrapp.com";
    private String apiUrl = "http://54.200.74.218";
    private final String ST_INFO = apiUrl + "/getStaticInfo/";
    private ProgressDialog dialog;
    final String mimeType = "text/html";
    final String encoding = "UTF-8";
    String text;
    private WebView tvPage;
    private View actionBarView;
    private HashMap<String, String> hm_st_title;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hm_st_title=new HashMap<String, String>();
        hm_st_title.put(ReactrConstants.TERMS,"TERMS");
        hm_st_title.put(ReactrConstants.PRIVACY,"PRIVACY");

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.custom_action_bar);
        actionBarView = getActionBar().getCustomView();

        Uri uri = getIntent().getData();
        tvPage = new WebView(this);
        String caption = uri.getQueryParameter("caption");
        text = uri.getQueryParameter("text");

        ((TextView) actionBarView.findViewById(R.id.barTitle)).setText(hm_st_title.get(text));
        ((ImageButton) actionBarView.findViewById(R.id.barItem)).setVisibility(View.INVISIBLE);
        ((ImageButton) actionBarView.findViewById(R.id.toggleMenu)).setImageResource(R.drawable.back_btn);
        ((ImageButton) actionBarView.findViewById(R.id.toggleMenu)).setOnClickListener(goBackClick);

        String param="";
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        new RequestTask().execute();
        setContentView(tvPage);

    }
    public String loadStInfo(String tag)
    {
        String toRet = "";
        HashMap<String, String> st_info_hm = new HashMap<String, String>();
        try {
            DefaultHttpClient hc = new DefaultHttpClient();
            ResponseHandler<String> res = new BasicResponseHandler();
            HttpPost postMethod = new HttpPost(ST_INFO);
            String response = hc.execute(postMethod, res);

            JSONObject json = new JSONObject(response);
            JSONObject urls = json.getJSONObject("static_info");
            toRet=urls.getString(tag);
            } catch (Exception e) {
            System.out.println("Exp=" + e);
        }
        return toRet;
    }
    //**********************************************************
    class RequestTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            try {

                String toWeb=loadStInfo(text);
                tvPage.loadDataWithBaseURL("", toWeb, mimeType, encoding, "");
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
            dialog = new ProgressDialog(AnotherActivity.this);
            dialog.setMessage("Loading...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            dialog.show();
            super.onPreExecute();
        }
    }

    View.OnClickListener goBackClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };
}
