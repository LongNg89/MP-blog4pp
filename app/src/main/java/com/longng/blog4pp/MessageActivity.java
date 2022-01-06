package com.longng.blog4pp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.longng.blog4pp.adapters.UserMessAdapter;
import com.longng.blog4pp.models.UserModel;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {
//    private FirebaseAuth mAuth;
//    private RecyclerView rcvConnectedList;
//    private UserMessAdapter userMessAdapter;
//    private ArrayList<UserModel> connectedList = new ArrayList<>();
//    private String idCurrent ;
//    public static final String KEY_ID = "KEY_ID" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
    }

    public void SendMessageHandler(View view) {
    }
}