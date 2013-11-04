package com.eyepinch.reactr;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;

import reactr.network.ReactorApi;

public class ResetPasswordActivity extends Activity {
    private ReactorApi reactorApi;
    private Handler handler;
    private boolean resetPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);
        reactorApi = ReactorApi.init(0,"");
        handler=new Handler();
        final EditText email=(EditText)findViewById(R.id.resetEmail);
        final EditText phone=(EditText)findViewById(R.id.resetPhone);
        ImageButton btnClose=(ImageButton)findViewById(R.id.closeBtn);
        Button resetPassButton= (Button) findViewById(R.id.resetPasswordButton);

        getActionBar().hide();
        btnClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                finish();
            }
        });

        resetPassButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                ReactrBase.showLoader(ResetPasswordActivity.this);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        reactorApi = ReactorApi.init(0, "");
                        resetPass = reactorApi.remindPassword(email.getText().toString(), phone.getText().toString());
                        handler.post(resPass);

                    }
                }).start();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.reset_password, menu);
        return true;
    }
    Runnable resPass = new Runnable() {
        @Override
        public void run() {

            ReactrBase.hideLoader();
            if(resetPass)
                Toast.makeText(ResetPasswordActivity.this, "Password sent to your email!", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(ResetPasswordActivity.this, "Error! Check your email or phone number!", Toast.LENGTH_SHORT).show();
        }
    };
}
