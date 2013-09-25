package com.example.reactr.reactr.models;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.reactr.MainActivity;
import com.example.reactr.R;
import com.example.reactr.ReactrBase;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.interfaces.DSAPrivateKey;

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
                    responseJson = reactorApi.login(email.getText().toString(), password.getText().toString());
                    try {
                        if(responseJson.get("status").equals("success"))
                        {
                            prefEditor.putInt("user_id", responseJson.getInt("user_id"));
                            prefEditor.putString("session_hash", responseJson.getString("session_hash"));
                            prefEditor.putString("username", responseJson.getString("username"));
                            prefEditor.commit();
                        }
                            handler.post(checkUserDone);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    };

    Runnable checkUserDone = new Runnable() {
        @Override
        public void run() {
            ReactrBase.hideLoader();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    };
}