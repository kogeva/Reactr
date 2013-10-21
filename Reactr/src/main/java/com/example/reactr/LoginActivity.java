package com.example.reactr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.c2dm.C2DMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import reactr.network.ReactorApi;

public class LoginActivity extends Activity {

    private ReactorApi reactorApi;
    private Handler handler;
    private EditText email;
    private EditText password;
    private Button loginButton;
    private JSONObject responseJson;
    private SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    private Context context;
    private String pushNotificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getActionBar().hide();
        handler = new Handler();
        context = this;

        preferences = getSharedPreferences("reactrPrefer", MODE_PRIVATE);
        prefEditor = preferences.edit();

        email = (EditText) findViewById(R.id.emailInput);
        password = (EditText) findViewById(R.id.passwordInput);
        loginButton = (Button) findViewById(R.id.loginButton);

        C2DMessaging.register(this, "856805386889");

//        if (C2DMessaging.getRegistrationId(this).length() == 0) {
//            pushNotificationId = C2DMessaging.getRegistrationId(this);
//        } else
//            pushNotificationId = C2DMessaging.getRegistrationId(this);

        loginButton.setOnClickListener(loginClick);
    }

    private View.OnClickListener loginClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            ReactrBase.showLoader(context);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    reactorApi = ReactorApi.init(0, "");
                    responseJson = reactorApi.login(email.getText().toString(), password.getText().toString(), C2DMessaging.getRegistrationId(getApplicationContext()));
                    handler.post(checkUserDone);

                }
            }).start();
            }
    };

    Runnable checkUserDone = new Runnable() {
        @Override
        public void run() {

            ReactrBase.hideLoader();
            try {
                if (responseJson != null)
                {
                    if( responseJson.get("status").equals("success"))
                    {
                        prefEditor.putInt("user_id", responseJson.getInt("user_id"));
                        prefEditor.putString("session_hash", responseJson.getString("session_hash"));
                        prefEditor.putString("username", responseJson.getString("username"));
                        prefEditor.putString("phone", responseJson.getString("phone"));
                        prefEditor.putString("email", email.getText().toString());
                        prefEditor.putString("privacy_message", responseJson.getString("privacy_message"));
                        prefEditor.commit();

                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "Incorrect password or email", Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(getApplicationContext(), "Connection to the server failed", Toast.LENGTH_SHORT).show();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
