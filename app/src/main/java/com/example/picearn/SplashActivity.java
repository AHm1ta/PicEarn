package com.example.picearn;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    Handler handler;

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(R.color.appbar);
        setContentView(R.layout.activity_splash);
        FirebaseAuth auth= FirebaseAuth.getInstance();
        final FirebaseUser user= auth.getCurrentUser();

        handler=new Handler();
        // Using handler with postDelayed called runnable run method
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (user!=null){
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    finish();
                }
                else {
                    Intent intent=new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },2000);

    }

}