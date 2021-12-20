package com.longng.blog4pp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toolbar;

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
    protected EditText edPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        toolbar.setNavigationOnClickListener((view) -> finish());


        btGoBack.setOnClickListener((view) -> finish());
    }
}