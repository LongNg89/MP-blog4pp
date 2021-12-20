package com.longng.blog4pp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        synchronized (MainActivity.this) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (FirebaseAuth.getInstance().getCurrentUser() == null)
                intent.setClass(MainActivity.this, LoginActivity.class);
            // Set Navigation to Home
            // Code to else
            startActivity(intent);
        }
    }
}