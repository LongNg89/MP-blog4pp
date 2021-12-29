package com.longng.blog4pp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private ProgressBar loginProgress;
    private EditText loginEmail, loginPassword;
    private Button loginButton;
    private TextView dontHaveAccount;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.emailLoginTxt);
        loginPassword = findViewById(R.id.passLoginTxt);
        loginButton = findViewById(R.id.loginBtn);
        dontHaveAccount = findViewById(R.id.createNewAccount);
        loginProgress = findViewById(R.id.loginProgressBar);
        mAuth = FirebaseAuth.getInstance();

        dontHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToRegister();
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //On clicking Login button log in the user
                String email = loginEmail.getText().toString().trim();
                String password= loginPassword.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    loginEmail.setError("Please enter email address!");
                }
                if (TextUtils.isEmpty(password)) {
                    loginPassword.setError("Please enter password!");
                }
                if (password.length() < 6) {
                    loginPassword.setError("Password must be 6 characters or longer");
                }
                else {
                    loginProgress.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                Log.d("mpProject","signInWithEmail:success");
                                Toast.makeText(LoginActivity.this,"Welcome",Toast.LENGTH_SHORT).show();
                                sendToMain();
                            }
                            else {
                                Log.w("mpProject", "signInWithEmail:failure", task.getException());
                                String error = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                            loginProgress.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });
    }

    //Checks if the user have already logged in
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //If user have logged in send back to MainActivity
        if(currentUser != null)
            sendToMain();
    }

    private void sendToMain() {
        Intent main = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(main);
    }

    private void sendToRegister() {
        Intent register = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(register);
    }
}