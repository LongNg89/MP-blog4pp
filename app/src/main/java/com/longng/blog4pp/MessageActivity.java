package com.longng.blog4pp;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.longng.blog4pp.adapters.MessageAdapter;
import com.longng.blog4pp.databaseReference.DatabaseManager;
import com.longng.blog4pp.fragments.MessageFragment;
import com.longng.blog4pp.models.MessageModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageActivity extends AppCompatActivity implements MessageAdapter.OnItemClickListener {
    private ImageView btnSend;
    private EditText edtChat;
    private String myFriendID;
    private String myID;
    private RecyclerView rcvMessage;

    private String usernameOfYourFriend;
    private String avatarOfYourFriend;

    private DatabaseReference messageMeRef;
    private DatabaseReference messageMyFriendRef;

    private List<MessageModel> listMessage = new ArrayList<>();
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        initView();
        setupSupportActionBar();
        setupDataBaseReferent();
        getDataMessage();
    }

    private void initView() {
        Bundle bundle = getIntent().getExtras();
        usernameOfYourFriend = bundle.getString(MessageFragment.KEY_USERNAME);
        avatarOfYourFriend = bundle.getString(MessageFragment.KEY_AVARTAR);
        myFriendID = bundle.getString(MessageFragment.KEY_ID);
        myID = FirebaseAuth.getInstance().getUid();

        btnSend = findViewById(R.id.btnSend);
        edtChat = findViewById(R.id.edtChat);
        rcvMessage = findViewById(R.id.rcvMessage);

        messageAdapter = new MessageAdapter(this,listMessage,R.layout.item_message, myID,this);
        rcvMessage.setLayoutManager(new LinearLayoutManager(this));
        rcvMessage.setAdapter(messageAdapter);


    }

    private void getDataMessage() {
        com.longng.blog4pp.databaseReference.DatabaseManager.getInstance()
                .getTableMessagesByID(myID)
                .child(myFriendID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        listMessage.clear();
                        for (DataSnapshot temp: snapshot.getChildren()){
                            MessageModel message = temp.getValue(MessageModel.class);
                            listMessage.add(message);

                        }
                        messageAdapter.notifyDataSetChanged();;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void setupDataBaseReferent() {
        messageMeRef = com.longng.blog4pp.databaseReference.DatabaseManager
                .getInstance()
                .getTableMessagesByID(myID)
                .child(myFriendID);

        messageMyFriendRef = DatabaseManager
                .getInstance()
                .getTableMessagesByID(myFriendID)
                .child(myID);
    }

    private void setupSupportActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(usernameOfYourFriend);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setDisplayUseLogoEnabled(true);
    }

    public void SendMessageHandler(View view) {
        String messageContent = edtChat.getText().toString().trim();
        if (messageContent.isEmpty()){
            return;
        }
        DatabaseReference messageKeyRef = messageMeRef.push();

        String messageID = messageKeyRef.getKey();

        MessageModel message = new MessageModel(messageID,messageContent,new Date(), myID, myFriendID);

        messageMeRef.child(messageID).setValue(message);
        messageMyFriendRef.child(messageID).setValue(message);


        edtChat.setText("");
    }

    // when click on up button, return to parent activity
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(MessageModel message) {

    }

    @Override
    public void onItemLongClick(MessageModel message) {

    }
}