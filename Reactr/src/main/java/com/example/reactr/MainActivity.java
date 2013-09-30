package com.example.reactr;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.example.reactr.fragments.MailBoxFragment;
import com.example.reactr.fragments.MenuFragment;
import com.google.android.c2dm.C2DMessaging;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.testflightapp.lib.TestFlight;

import java.util.HashMap;

import reactr.network.ReactorApi;

public class MainActivity extends SlidingFragmentActivity  {

    private Fragment mContent;
    private ReactorApi reactorApi;
    private String sessionHash;
    private int userId;
    private static String username;
    private SharedPreferences preferences;
    private MenuFragment menuFragment;
    private HashMap<String, String> st_info_hm;
    private ImageButton toggleMenuButton;
    private SharedPreferences.Editor editorSettings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        TestFlight.takeOff(getApplication(), "3f105bbc-e217-4c64-b4cd-2d43e1c22971");
        st_info_hm = new HashMap<String, String>();
        menuFragment = new MenuFragment();

        sessionHash = getSharedPreferences("reactrPrefer", MODE_PRIVATE).getString("session_hash", null);
        userId = getSharedPreferences("reactrPrefer", MODE_PRIVATE).getInt("user_id", 0);
        username = getSharedPreferences("reactrPrefer", MODE_PRIVATE).getString("username", null);

        reactorApi = ReactorApi.init(userId, sessionHash);

        if(savedInstanceState != null)
            mContent  = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
        if(mContent == null)
            mContent = new MailBoxFragment();

        setBehindContentView(R.layout.menu_frame);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.menu_frame, menuFragment)
                .commit();

        setContentView(R.layout.content_fragment);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, mContent)
                .commit();
        getSlidingMenu().setBehindOffset(200);
        getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        getSlidingMenu().setShadowDrawable(R.drawable.shadow);
        getSlidingMenu().setShadowWidth(15);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        getSlidingMenu().setOnOpenListener(new SlidingMenu.OnOpenListener() {
            public void onOpen() {
                    InputMethodManager inputManager = (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputManager.isAcceptingText()) {
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                }
          //     updateMenu();
            }
        });

        toggleMenuButton = (ImageButton) getSupportActionBar().getCustomView().findViewById(R.id.toggleMenu);
        toggleMenuButton.setOnClickListener(toogleMenu);

        String message = getIntent().getStringExtra("message");
    //    if(message == null)
   //         Toast.makeText(getBaseContext(), "NULL", Toast.LENGTH_SHORT).show();
        if(message != null)
            Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();

        String id = C2DMessaging.getRegistrationId(this);
        if(id == "")
        {
            C2DMessaging.register(this, "ash@eyepinch.com");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "mContent", mContent);
    }

    public void switchContent(Fragment fragment) {
        mContent = fragment;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    View.OnClickListener toogleMenu = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
       //  updateMenu();
            toggle();
        }
    };

    public ReactorApi getReactorApi()
    {
        return reactorApi;
    }

    public static String getUsername()
    {
        return username;
    }

    public boolean isPrivacyMessage()
    {
        return new Boolean(getSharedPreferences("reactrPrefer", MODE_PRIVATE).getString("privacy_message", null));
    }

    public void setAppSettings(String field, String value)
    {
        editorSettings = preferences.edit();
        editorSettings.putString(field, value);
    }

    public String getEmail()
    {
        return getSharedPreferences("reactrPrefer", MODE_PRIVATE).getString("email", null);
    }

    public String getPhone() {
        String phone = getSharedPreferences("reactrPrefer", MODE_PRIVATE).getString("phone", null);
        return (phone.length() < 10)? "0" + phone : phone;
    }

    public void removeSessionHash()
    {
        preferences = getSharedPreferences("reactrPrefer", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putString("session_hash", "");
        prefEditor.commit();
    }
    //*************************
    public String getStInfoByParameter(String p)
    {
        return st_info_hm.get(p);
    }
    public int getSizeStInfo()
    {
        return st_info_hm.size();
    }
    public void loadStInfo()
    {
        st_info_hm = reactorApi.loadStInfo();
    }
    public void updateMenu()
    {
        menuFragment.updateMenu();
    }
}
