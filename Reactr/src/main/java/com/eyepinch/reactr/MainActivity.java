package com.eyepinch.reactr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.eyepinch.reactr.fragments.AddMessageFragment;
import com.eyepinch.reactr.fragments.CreatePhotoFragment;
import com.eyepinch.reactr.fragments.MailBoxFragment;
import com.eyepinch.reactr.fragments.MenuFragment;
import com.eyepinch.reactr.fragments.ShowMessageFragment;
import com.eyepinch.reactr.reactr.models.MessageEntity;
import com.google.android.c2dm.C2DMessaging;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
//import com.testflightapp.lib.TestFlight;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.util.HashMap;

import reactr.network.ReactorApi;

public class MainActivity extends SlidingFragmentActivity  {

    private Fragment mContent;
    public static ReactorApi reactorApi;
    private String sessionHash;
    private int userId;
    private static String username;
    private SharedPreferences preferences;
    private MenuFragment menuFragment;
    private HashMap<String, String> st_info_hm;
    private ImageButton toggleMenuButton;
    private SharedPreferences.Editor editorSettings;
    private static int RESULT_LOAD_IMAGE = 1;
    private MessageEntity messageEntity=null;
    private String pushNotificationId;
    public static Boolean isRunningApplication;
    public static boolean IsInGallery;
    private static long back_pressed;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        try
        {
        super.onCreate(savedInstanceState);
        }
        catch (Exception e) {finish();}
        setBehindContentView(R.layout.load_layout);
        isRunningApplication = true;


        C2DMessaging.register(this, "856805386889");
        pushNotificationId = C2DMessaging.getRegistrationId(this);

        st_info_hm = new HashMap<String, String>();
        menuFragment = new MenuFragment();

        sessionHash = getSharedPreferences("reactrPrefer", MODE_PRIVATE).getString("session_hash", null);
        userId = getSharedPreferences("reactrPrefer", MODE_PRIVATE).getInt("user_id", 0);
        username = getSharedPreferences("reactrPrefer", MODE_PRIVATE).getString("username", null);

        reactorApi = ReactorApi.init(userId, sessionHash);

        if(savedInstanceState != null)
            mContent  = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
        if(mContent == null)
        {
            if (fromNotificationMessage(getIntent()) != null)
                mContent = new ShowMessageFragment(fromNotificationMessage(getIntent()));
            else
                mContent = new MailBoxFragment();
        }

        setBehindContentView(R.layout.menu_frame);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.menu_frame, menuFragment)
                .commitAllowingStateLoss();

        setContentView(R.layout.content_fragment);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, mContent)
                .commitAllowingStateLoss();
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
            }
        });

        toggleMenuButton = (ImageButton) getSupportActionBar().getCustomView().findViewById(R.id.toggleMenu);
        toggleMenuButton.setOnClickListener(toogleMenu);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mContent instanceof  CreatePhotoFragment){
            Log.d("ErrorR", "instanceof");
            switchContent(mContent);
        }
          else{
            Log.d("ErrorR", "else instance");
                        try{

                        getSupportFragmentManager().putFragment(outState, "mContent", mContent);
                        }
                        catch (Exception e)
                        { }
                    }
                    }


    public void switchContent(Fragment fragment) {
        mContent = fragment;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment).addToBackStack(String.valueOf(fragment.getId()))
             //   .commit();
        .commitAllowingStateLoss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public View.OnClickListener toogleMenu = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
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
        if(preferences == null)
            preferences = getSharedPreferences("reactrPrefer", MODE_PRIVATE);

        editorSettings = preferences.edit();
        editorSettings.putString(field, value);
        editorSettings.commit();
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

    public int getResultLoadImage(){
        return RESULT_LOAD_IMAGE;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private MessageEntity fromNotificationMessage(Intent intent)
    {
        String message = intent.getStringExtra("message");

        if(message != null && !message.isEmpty()){
            MessageEntity mesage = new MessageEntity();
            mesage.setPhoto(intent.getStringExtra("photo"));
            if(!intent.getStringExtra("reactionPhoto").equals("false"))
                mesage.setReactionPhoto(intent.getStringExtra("reactionPhoto"));
            else
                mesage.setReactionPhoto("null");
            mesage.setText(intent.getStringExtra("text"));
            mesage.setFrom_user(new Integer(intent.getStringExtra("from_user")));
            mesage.setId(new Integer(intent.getStringExtra("messageId")));
            mesage.setRead(false);
            mesage.setFromMe(false);
            return mesage;
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IsInGallery = false;
        if (null != data) {
            Uri selectedImage = data.getData();

            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            if(picturePath == null)
            {
                Toast.makeText(getBaseContext(), "Can't upload image, try another folder", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, this.getResultLoadImage());
            }
            else
            {
            File imgFile = new File(picturePath);

                try {
                    Bitmap bmp=null;
                    if(picturePath.toLowerCase().contains("http://")||picturePath.toLowerCase().contains("https://"))
                    {
                        URL url = new URL(picturePath);
                        bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    }
                    else
                    {
                        try
                        {
                        bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        }
                        catch (Exception e){}
                    }

            Bitmap toImageBitmap = bmp;
            Bitmap.createScaledBitmap(toImageBitmap, toImageBitmap.getWidth()/2, toImageBitmap.getHeight()/2, false);
            Bitmap bitmap = toImageBitmap;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bitmapdata = stream.toByteArray();

            if (messageEntity == null)
                ReactrBase.switchFraagment(this, new AddMessageFragment(bitmapdata, -1));
            else
                ReactrBase.switchFraagment(this, new AddMessageFragment(bitmapdata, messageEntity, -1));

                }
                catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
            }

        }
    }

    public void setMessageEntity(MessageEntity me){
         messageEntity = me;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunningApplication = false;
    }
    @Override
    protected void onResume() {
        super.onResume();
        isRunningApplication = true;
    }

    public static Boolean isRunning()
    {
        return isRunningApplication;
    }
    @Override
    public void onBackPressed()
    {
        if (back_pressed + 2000 > System.currentTimeMillis())
        {
            finish();
        }
        else Toast.makeText(getBaseContext(), "Press again to exit!", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }
}
