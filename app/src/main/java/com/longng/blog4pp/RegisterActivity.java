package com.longng.blog4pp;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.longng.blog4pp.databaseReference.DatabaseManager;
import com.longng.blog4pp.models.UserModel;


public class RegisterActivity extends AppCompatActivity {
    private LinearLayout registerProgress;
    private EditText registerEmail, registerPassword, registerConfirmPassword;
    private Button registerButton;
    private TextView alreadyHaveAccount;
    boolean isEnable;
    private ImageView registerShowPassButton, registerShowConfirmPassButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setTitle("Register");
        // add up button to return to parent activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        registerEmail = findViewById(R.id.emailRegisterTxt);
        registerPassword = findViewById(R.id.passRegisterTxt);
        registerConfirmPassword = findViewById(R.id.passConfirmRegisterTxt);
        registerButton = findViewById(R.id.registerBtn);
        registerShowPassButton = findViewById(R.id.show_password_icon1);
        registerShowConfirmPassButton = findViewById(R.id.show_password_icon2);
        alreadyHaveAccount = findViewById(R.id.alreadyHaveAccount);
        registerProgress = findViewById(R.id.progressBarRegister);
        mAuth = FirebaseAuth.getInstance();

        registerShowPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPass(registerPassword);
            }
        });

        registerShowConfirmPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPass(registerConfirmPassword);
            }
        });

        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToLogin();
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, password, passConfirm;
                email = registerEmail.getText().toString().trim();
                password = registerPassword.getText().toString().trim();
                passConfirm = registerConfirmPassword.getText().toString().trim();
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    registerEmail.setError("Please enter valid email!");
                    registerEmail.requestFocus();
                }
                else if (TextUtils.isEmpty(password)) {
                    registerPassword.setError("Please enter password!");
                    registerPassword.requestFocus();
                }
                else if (TextUtils.isEmpty(passConfirm)) {
                    registerConfirmPassword.setError("Please enter confirm password!");
                    registerConfirmPassword.requestFocus();
                }
                else if (password.length() < 6) {
                    registerPassword.setError("Password must be 6 characters or longer!");
                    registerPassword.requestFocus();
                }
                else if (!password.equals(passConfirm)) {
                    registerConfirmPassword.setError("Password not match!");
                    registerConfirmPassword.requestFocus();
                }
                else {
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    registerProgress.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d("mpProject","signInWithEmail:success");
                                    UserModel userModel = new UserModel(mAuth.getUid(),email,password,"","");
                                    saveUser(userModel);
                                    Toast.makeText(RegisterActivity.this,"Register successful!", Toast.LENGTH_LONG).show();
                                    sendToSetting();
                                }
                                else {
                                    Log.w("mpProject", "signUpWithEmail:failure", task.getException());
                                    String error = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                registerProgress.setVisibility(View.INVISIBLE);
                            }
                        });
                }
            }
        });

    }

    private void showPass(EditText password) {
        if(!isEnable) {
            isEnable = true;
            password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
        else {
            isEnable = false;
            password.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }

    private void sendToSetting() {
        Intent accountSetup = new Intent(RegisterActivity.this, AccountSetupActivity.class);
        startActivity(accountSetup);
        finish();
    }

    //Checks if the user have already logged in
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //If user have logged in send back to MainActivity
        if(currentUser != null){
            sendToMain();
        }
    }


    // when click on up button, return to parent activity
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        sendToLogin();
        return super.onOptionsItemSelected(item);
    }

    private void sendToMain() {
        Intent main = new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(main);
    }

    private void sendToLogin() {
        Intent login = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(login);
    }

    private void saveUser(UserModel userModel){
        DatabaseManager
                .getInstance()
                .getTableUsersByID(userModel.getUid())
                .setValue(userModel);
    }
}