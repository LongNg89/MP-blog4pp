package com.longng.blog4pp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @BindView(
            R.id.et_email
    )
    EditText etEmail;
    @SuppressLint("NonConstantResourceId")
    @BindView(
            R.id.et_password
    )
    EditText etPassword;

    @SuppressLint("NonConstantResourceId")
    @BindView(
            R.id.bt_login
    )
    Button btLogin;

    @SuppressLint("NonConstantResourceId")
    @BindView(
            R.id.bt_create_new_account
    )
    Button btCreateNewAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        btLogin.setOnClickListener((view) -> {
            final String email = etEmail.getText().toString();
            final String password = etPassword.getText().toString();
            try {
                FirebaseAuth
                        .getInstance()
                        .signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener((authResult) -> {
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        })
                        .addOnFailureListener(command -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setMessage(command.getMessage());
                            builder.setTitle(R.string.error);
                            builder.setPositiveButton(R.string.ok, (dialog, id) -> dialog.cancel());
                            builder.setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        });
            } catch (Exception e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage(e.getMessage());
                builder.setTitle(R.string.error);
                builder.setPositiveButton(R.string.ok, (dialog, id) -> dialog.cancel());
                builder.setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        btCreateNewAccount.setOnClickListener((view) ->

        {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}