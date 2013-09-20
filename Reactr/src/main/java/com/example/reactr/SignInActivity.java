package com.example.reactr;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import reactr.network.ReactorApi;


public class SignInActivity extends Activity {

    private ReactorApi api;
    private Button toStepTwoButton;
    private Button toStepThreeButton;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText usernameEditTexts;
    private EditText phoneEditText;
    private String email = null;
    private String password = null;
    private String username;
    private String phone;
private TextView st_tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up1);

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
            new Thread(validationRequest).start();
            setContentView(R.layout.sign_up2);
            toStepThreeButton = (Button) findViewById(R.id.toStepThreeButton);
            toStepThreeButton.setOnClickListener(toStepThreeClick);
        }
    };

    View.OnClickListener toStepThreeClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            setContentView(R.layout.sign_up3);
        }
    };

    Runnable validationRequest = new Runnable() {
        @Override
        public void run() {
         //   this.toString();
            api.checkUsernameAndEmail(email, password);
        }
    };
}
