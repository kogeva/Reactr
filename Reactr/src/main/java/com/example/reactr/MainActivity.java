package com.example.reactr;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.example.reactr.fragments.FriendsFragment;
import com.example.reactr.fragments.MailBoxFragment;
import com.example.reactr.fragments.MenuFragment;
import com.example.reactr.fragments.MessageFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import reactr.network.ReactorApi;

public class MainActivity extends SlidingFragmentActivity  {

    private Fragment mContent;
    private ReactorApi reactorApi;
    private String sessionHash;
    private int userId;
    private static String username;
    private SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                .replace(R.id.menu_frame, new MenuFragment())
                .commit();

        setContentView(R.layout.content_fragment);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, mContent)
                .commit();
        getSlidingMenu().setBehindOffset(200);
        getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        getSlidingMenu().setShadowDrawable(R.drawable.shadow);
        getSlidingMenu().setShadowWidth(20);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        toggle();
        System.out.println(item.getItemId());
        System.out.println(R.id.slidingmenumain);
        return super.onOptionsItemSelected(item);
    }

    public ReactorApi getReactorApi()
    {
        return reactorApi;
    }

    public static String getUsername()
    {
        return username;
    }

    public void removeSessionHash()
    {
        preferences = getSharedPreferences("reactrPrefer", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putString("session_hash", "");
        prefEditor.commit();
    }
}
