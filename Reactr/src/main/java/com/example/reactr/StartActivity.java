package com.example.reactr;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.reactr.reactr.models.LoginActivity;

public class StartActivity extends Activity {

    private Button login;
    private Button signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().hide();

        login = (Button) findViewById(R.id.loginButton);
        signIn = (Button) findViewById(R.id.sigInButton);
        login.setOnClickListener(switchMenu);
        signIn.setOnClickListener(switchMenu);

    }

    View.OnClickListener switchMenu = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.loginButton :
                    startActivity(new Intent(StartActivity.this, LoginActivity.class));
                    break;
                case R.id.sigInButton :
                    startActivity(new Intent(StartActivity.this, SignInActivity.class));
                    break;
            }
        }
    };
}
