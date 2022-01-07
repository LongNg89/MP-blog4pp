package com.longng.blog4pp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.longng.blog4pp.adapters.UserMessAdapter;
import com.longng.blog4pp.databaseReference.DataBaseManager;
import com.longng.blog4pp.models.BlogPostModel;
import com.longng.blog4pp.models.UserModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment implements UserMessAdapter.OnItemClickListener{
    private FirebaseAuth mAuth;
    private RecyclerView rcvListFriendInMessage;
    private UserMessAdapter userMessAdapter;
    private ArrayList<UserModel> listData = new ArrayList<>();
    private String idCurrent ;

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
//        DataBaseManager
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
        DataBaseManager
                .getInstance()
                .getTableUsers()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d("minhdz", "checked 4");
//
//                        UserModel userModel = snapshot.getValue(UserModel.class);
//                        listData.add(userModel);
//                        userMessAdapter.notifyDataSetChanged();

                        listData.clear();
                        for (DataSnapshot temp: snapshot.getChildren()){

                            UserModel user = temp.getValue(UserModel.class);
                            listData.add(user);
                            Log.d("minhdz", user.getEmail());
                        }
                        userMessAdapter.notifyDataSetChanged();

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
        userMessAdapter = new UserMessAdapter(listData,R.layout.item_user_chat,this);
        rcvListFriendInMessage.setLayoutManager(new LinearLayoutManager(this.getActivity(),LinearLayoutManager.VERTICAL,false));
        rcvListFriendInMessage.setAdapter(userMessAdapter);
        Log.d("minhdz", "checked 2");
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