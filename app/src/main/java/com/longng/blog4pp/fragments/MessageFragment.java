package com.longng.blog4pp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.longng.blog4pp.MessageActivity;
import com.longng.blog4pp.R;
import com.longng.blog4pp.adapters.UserMessageAdapter;
import com.longng.blog4pp.databaseReference.DatabaseManager;
import com.longng.blog4pp.models.UserModel;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment implements UserMessageAdapter.OnItemClickListener {
    private FirebaseAuth mAuth;
    private RecyclerView rcvListFriendInMessage;
    private EditText edtSearchUserMessage;
    private UserMessageAdapter userMessageAdapter;
    private ArrayList<UserModel> listData = new ArrayList<>();
    private String idCurrent;
    private String myUserName;

    public static final String KEY_USERNAME = "KEY_USERNAME" ;
    public static final String KEY_AVARTAR = "KEY_AVARTAR" ;
    public static final String KEY_ID = "KEY_ID" ;


    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        initView(view);
//        loadData();
        showUserConnectedByCurrentUser();

        return view;
    }

    private void loadData() {

//        showUserConnectedByCurrentUser(idCurrent);
//        DatabaseManager
//                .getInstance()
//                .getTableConnectedByID(idCurrent)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        Log.d("DDM","connected by "+idCurrent);
//                        for(DataSnapshot data:snapshot.getChildren()){
//                            listData.clear();
//                            String connectedId = data.getKey();
//                            Log.d("DDM",connectedId);
//                            showUserConnectedByCurrentUser(connectedId);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//        setupActionBar(idCurrent);
    }
    private void showUserConnectedByCurrentUser() {
        Log.d("minhdz", "checked 3");
        DatabaseManager
                .getInstance()
                .getTableUsers()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d("minhdz", "checked 4");
                        listData.clear();
                        for (DataSnapshot temp: snapshot.getChildren()){

                            UserModel user = temp.getValue(UserModel.class);
                            listData.add(user);
                            if (user.getUid().equals(idCurrent)){
                                myUserName = user.getUsername();
                            }

                            Log.d("minhdz", user.getEmail());
                        }
                        userMessageAdapter.notifyDataSetChanged();

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        Log.d("minhdz", "checked 5");
    }

    private void initView(View view) {
        mAuth = FirebaseAuth.getInstance();
        idCurrent = mAuth.getUid();
        rcvListFriendInMessage = view.findViewById(R.id.rcvListFriendInMessage);
        edtSearchUserMessage = view.findViewById(R.id.edtSearchUserMessage);
        edtSearchUserMessage.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    searchUserMessage();
                    return true;
                }
                return false;
            }
        });

        userMessageAdapter = new UserMessageAdapter(listData,R.layout.item_user_chat,this);
        rcvListFriendInMessage.setLayoutManager(new LinearLayoutManager(this.getActivity(),LinearLayoutManager.VERTICAL,false));
        rcvListFriendInMessage.setAdapter(userMessageAdapter);
        Log.d("minhdz", "checked 2");
    }

    private void searchUserMessage() {
        String textSearch = edtSearchUserMessage.getText().toString().trim();
        Log.d("minhdz", textSearch);
        ArrayList<UserModel> filteredList = new ArrayList<>();
        for (UserModel userModel:listData){
            if (userModel.getUsername().contains(textSearch)){
                filteredList.add(userModel);
                Log.d("minhdz", userModel.getUsername());
            }
        }

        userMessageAdapter = new UserMessageAdapter(filteredList,R.layout.item_user_chat,this);
        rcvListFriendInMessage.setLayoutManager(new LinearLayoutManager(this.getActivity(),LinearLayoutManager.VERTICAL,false));
        rcvListFriendInMessage.setAdapter(userMessageAdapter);

    }


    @Override
    public void onItemClick(UserModel user) {
        Intent intent = new Intent(getActivity().getApplication(), MessageActivity.class);
        Log.d("minhdz", user.toString());
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ID,user.getUid());
        bundle.putString(KEY_USERNAME,user.getUsername());
        bundle.putString(KEY_AVARTAR, user.getAvartar());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(UserModel user) {

    }
}