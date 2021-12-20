package com.longng.blog4pp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @BindView(
            R.id.toolbar
    )
    protected Toolbar toolbar;

    @SuppressLint("NonConstantResourceId")
    @BindView(
            R.id.bt_back_to_login
    )
    protected Button btGoBack;

    @SuppressLint("NonConstantResourceId")
    @BindView(
            R.id.bt_register
    )
    protected Button btRegister;

    @SuppressLint("NonConstantResourceId")
    @BindView(
            R.id.et_email
    )

    protected EditText etEmail;
    @SuppressLint("NonConstantResourceId")
    @BindView(
            R.id.et_password
    )
    protected EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        toolbar.setNavigationOnClickListener((view) -> finish());

        btRegister.setOnClickListener((view) -> {
            final String email = etEmail.getText().toString();
            final String password = etPassword.getText().toString();
            final AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            builder.setPositiveButton(R.string.ok, (dialog, id) -> dialog.cancel());
            builder.setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());
            try {
                FirebaseAuth
                        .getInstance()
                        .createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener((authResult) -> {
                            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        })
                        .addOnFailureListener(command -> {
                            builder.setMessage(command.getMessage());
                            builder.setTitle(R.string.error);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        });
            } catch (Exception e) {
                builder.setMessage(e.getMessage());
                builder.setTitle(R.string.error);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        btGoBack.setOnClickListener((view) -> finish());
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}