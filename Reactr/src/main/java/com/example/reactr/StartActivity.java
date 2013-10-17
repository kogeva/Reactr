package com.example.reactr;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
                    if(!ReactrBase.isOnline(StartActivity.this))
                        Toast.makeText(getBaseContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
                    else
                        startActivity(new Intent(StartActivity.this, LoginActivity.class));
                    break;
                case R.id.sigInButton :
                    if(!ReactrBase.isOnline(StartActivity.this))
                        Toast.makeText(getBaseContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
                    else
                        startActivity(new Intent(StartActivity.this, SignInActivity.class));
                    break;
            }
        }
    };
}
