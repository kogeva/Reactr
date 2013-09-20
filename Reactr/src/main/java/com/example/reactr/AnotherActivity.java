package com.example.reactr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.webkit.WebView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.example.reactr.fragments.MailBoxFragment;
import com.example.reactr.fragments.MenuFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.util.HashMap;

import reactr.network.ReactorApi;
import reactr.utils.ReactrConstants;
//Активити для отображения статич. инфы (пока не работает, не грузит)
public class AnotherActivity extends Activity {
    private String apiUrl = "http://api.reactrapp.com";
    private final String ST_INFO           = apiUrl + "/getStaticInfo/";

    final String mimeType = "text/html";
    final String encoding = "UTF-8";
    private WebView tvPage;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri uri = getIntent().getData();

        String caption = uri.getQueryParameter("caption");
        //String text = "text";
        String text = uri.getQueryParameter("text");
        String param="";

        String toWeb=loadStInfo(text);
        tvPage.loadDataWithBaseURL("", toWeb, mimeType, encoding, "");


    }


    public String loadStInfo(String tag)
    {

        String toRet="";
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

}
