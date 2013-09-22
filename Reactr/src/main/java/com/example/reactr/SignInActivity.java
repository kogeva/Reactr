package com.example.reactr;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.testflightapp.lib.TestFlight;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import reactr.network.ReactorApi;


public class SignInActivity extends Activity {

    private ReactorApi api;
    private ArrayList<String> errors;
    private Handler handler;
    private Button toStepTwoButton;
    private Button toStepThreeButton;
    private Button registrationComplete;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText usernameEditTexts;
    private EditText phoneEditText;
    private String email = null;
    private String password = null;
    private String username;
    private String phone;
private TextView st_tv;
    private Context context;
    private JSONObject result;
    private SharedPreferences preferences;
    private SharedPreferences.Editor prefEditor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TestFlight.takeOff(getApplication(), "3f105bbc-e217-4c64-b4cd-2d43e1c22971");
        setContentView(R.layout.sign_up1);
        handler = new Handler();
        context = this;

        preferences = getSharedPreferences("reactrPrefer", MODE_PRIVATE);
        prefEditor = preferences.edit();

        api = ReactorApi.init(0,"");

        toStepTwoButton = (Button) findViewById(R.id.toStepTwoButton);
        emailEditText = (EditText) findViewById(R.id.emailText);
        passwordEditText = (EditText) findViewById(R.id.epasswordText);
        toStepTwoButton.setOnClickListener(toStepTwoClick);

        //настройки для перехождения по ссылке
        st_tv = (TextView)findViewById(R.id.textView);
        st_tv.setLinksClickable(true);
        st_tv.setMovementMethod(new LinkMovementMethod());

    }

    View.OnClickListener toStepTwoClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            email = emailEditText.getText().toString();
            password = passwordEditText.getText().toString();
            if(email.isEmpty() || password.isEmpty())
                Toast.makeText(context, "Email and password a required", Toast.LENGTH_LONG).show();
            else {
                ReactrBase.showLoader(context);
                new Thread(validationRequest).start();
            }

        }
    };

    View.OnClickListener toStepThreeClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            username = usernameEditTexts.getText().toString();
            if(username.isEmpty())
                Toast.makeText(context, "Username a required", Toast.LENGTH_LONG).show();
            else {
                ReactrBase.showLoader(context);
                new Thread(validationRequestTwo).start();
            }
        }
    };

    Runnable validationRequest = new Runnable() {
        @Override
        public void run() {
            this.toString();
            api.checkUsernameAndEmail(email, password);
        }
    };
}
