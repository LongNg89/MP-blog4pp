package com.longng.blog4pp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText resetEmail;
    private Button resetButton;
    private LinearLayout resetProgress;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        getSupportActionBar().setTitle("Reset Password");
        // add up button to return to parent activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        resetEmail = findViewById(R.id.emailResetTxt);
        resetButton = findViewById(R.id.resetBtn);
        resetProgress = findViewById(R.id.progressBarReset);
        mAuth = FirebaseAuth.getInstance();

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void resetPassword(){
        String email = resetEmail.getText().toString().trim();
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            resetEmail.setError("Please enter valid email!");
            resetEmail.requestFocus();
        }
        else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            resetProgress.setVisibility(View.VISIBLE);
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ResetPasswordActivity.this, "Check your email to reset password, please!", Toast.LENGTH_SHORT).show();
                        sendToLogin();
                    }
                    else {
                        Toast.makeText(ResetPasswordActivity.this, "Something wrong, try again!", Toast.LENGTH_SHORT).show();
                    }
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    resetProgress.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    // when click on up button, return to parent activity
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        sendToLogin();
        return super.onOptionsItemSelected(item);
    }

    private void sendToLogin() {
        Intent login = new Intent(ResetPasswordActivity.this, LoginActivity.class);
        startActivity(login);
    }
}