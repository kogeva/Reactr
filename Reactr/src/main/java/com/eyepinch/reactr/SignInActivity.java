package com.eyepinch.reactr;

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
//import com.testflightapp.lib.TestFlight;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
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
    private Context context;
    private JSONObject result;
    private SharedPreferences preferences;
    private SharedPreferences.Editor prefEditor;
    private TextView st_tv;
    private String pushNotificationId;
    private int screenNumber=1;

    @Override
    public void onBackPressed() {
        switch (screenNumber)
        {
            case 1:
                finish();
                break;
            case 2:
                screenNumber=1;
                setContentView(R.layout.sign_up1);
                break;
            case 3:
                screenNumber=2;
                setContentView(R.layout.sign_up2);
                break;
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TestFlight.takeOff(getApplication(), "3f105bbc-e217-4c64-b4cd-2d43e1c22971");
        setContentView(R.layout.sign_up1);
        handler = new Handler();
        context = this;
        getActionBar().hide();

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

        C2DMessaging.register(this, "856805386889");
        pushNotificationId = C2DMessaging.getRegistrationId(this);

    }
    View.OnClickListener toStepTwoClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

         //   handler.post(switchToSlideActivity);
        //    startActivity(new Intent(SignInActivity.this, PhotoViewActivity.class));
            email = emailEditText.getText().toString();
            password = passwordEditText.getText().toString();
           
            if(email.isEmpty() || password.isEmpty())
                Toast.makeText(context, "Email and password are required", Toast.LENGTH_LONG).show();
            else {
                if(password.length() < 6)
                    Toast.makeText(context, "Password less then 6 characters", Toast.LENGTH_LONG).show();
                else
                {
                    if (isValidEmail(email)) {
                        ReactrBase.showLoader(context);
                        new Thread(validationRequest).start();
                    } else
                        Toast.makeText(getApplicationContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
                }
            }

         //   startActivity(new Intent(SignInActivity.this, PhotoViewActivity.class));
        }
    };

    View.OnClickListener toStepThreeClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            username = usernameEditTexts.getText().toString();
            if(username.isEmpty())
                Toast.makeText(context, "Username is required", Toast.LENGTH_LONG).show();
            else {
                ReactrBase.showLoader(context);
                new Thread(validationRequestTwo).start();
            }
        }
    };

    Runnable validationRequest = new Runnable() {
        @Override
        public void run() {
            errors = api.checkUsernameAndEmail(null, email);
            handler.post(updateView);
        }
    };

    Runnable updateView = new Runnable() {
        @Override
        public void run() {
            ReactrBase.hideLoader();
            if(errors.size() < 1) {
                screenNumber=2;
                ((SignInActivity) context).setContentView(R.layout.sign_up2);
                usernameEditTexts = (EditText) findViewById(R.id.usernameEditText);
                toStepThreeButton = (Button) findViewById(R.id.toStepThreeButton);
                toStepThreeButton.setOnClickListener(toStepThreeClick);
            } else {
                Toast.makeText(context, "Email exist in the system", Toast.LENGTH_LONG).show();
            }
        }
    };

    Runnable validationRequestTwo = new Runnable() {
        @Override
        public void run() {
            errors = api.checkUsernameAndEmail(username, null);
            handler.post(vupdateViewTwo);
        }
    };

    Runnable vupdateViewTwo = new Runnable() {
        @Override
        public void run() {
            ReactrBase.hideLoader();
            if(errors.size() < 1) {
                screenNumber=3;
                ((SignInActivity) context).setContentView(R.layout.sign_up3);
                phoneEditText = (EditText) findViewById(R.id.phoneEdiText);
                registrationComplete = (Button) findViewById(R.id.completeButton);
                registrationComplete.setOnClickListener(registerClick);
            } else {
                Toast.makeText(context, "Username exist in the system", Toast.LENGTH_LONG).show();
            }
        }
    };

    View.OnClickListener registerClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            phone = phoneEditText.getText().toString();
            if (booleanIsValidPhone(phone))
                new Thread(registration).start();
            else
                Toast.makeText(getApplicationContext(), "Invalid  phone number", Toast.LENGTH_SHORT).show();
        }
    };

    Runnable registration = new Runnable() {
        @Override
        public void run() {
           result = api.registration(email, password, username, phone, C2DMessaging.getRegistrationId(getApplicationContext()));
            try {
                if (result.get("status").equals("success")) {
                    prefEditor.putInt("user_id", result.getInt("user_id"));
                    prefEditor.putString("session_hash", result.getString("session_hash"));
                    prefEditor.putString("username", username);
                    prefEditor.putString("phone", phone);
                    prefEditor.putString("privacy_message", "true");
                    prefEditor.putString("email", email);
                    prefEditor.commit();
                    handler.post(switchToSlideActivity);
                  //  handler.post(switchToMainActivity);
                }
                if (result.get("status").equals("failed"))
                {
                    if(((JSONObject)result.get("errors")).getString("phone").length() > 0)
                    {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "This phone exist in the system", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    Runnable switchToMainActivity = new Runnable() {
        @Override
        public void run() {
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
            //api.checkUsernameAndEmail(email, password);
        }
    };


    Runnable switchToSlideActivity = new Runnable() {
        @Override
        public void run() {
            startActivity(new Intent(SignInActivity.this, PhotoViewActivity.class));
            //api.checkUsernameAndEmail(email, password);
        }
    };

    private  boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private Boolean booleanIsValidPhone (String phone) {
        return  (phone.length() > 9 && phone.length() <= 10) ? true : false;
    }
}
