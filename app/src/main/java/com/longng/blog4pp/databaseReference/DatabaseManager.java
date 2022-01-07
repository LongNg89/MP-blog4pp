package com.longng.blog4pp.databaseReference;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseManager {
    private DatabaseReference databaseReference;
    private static final String TABLE_USER = "USERS";
    private static final String TABLE_CONNECTED = "CONNECTED";
    private static final String TABLE_MESSAGES = "MESSAGES";

    public static DatabaseManager getInstance(){
        return new DatabaseManager();
    }

    public DatabaseManager() {
        databaseReference = FirebaseDatabase.getInstance("https://blog-e61dc-default-rtdb.firebaseio.com/").getReference();
    }

    public DatabaseReference getTableUsers(){
        return databaseReference
                .child(TABLE_USER);
    }


    public DatabaseReference getTableConnected(){
        return databaseReference
                .child(TABLE_CONNECTED);
    }

    public DatabaseReference getTableMessages(){
        return databaseReference
                .child(TABLE_MESSAGES);
    }

    public DatabaseReference getTableUsersByID(String id){
        return getTableUsers()
                .child(id);
    }

    public DatabaseReference getTableConnectedByID(String id){
        return getTableConnected()
                .child(id);
    }

    public DatabaseReference getTableMessagesByID(String id){
        return  getTableMessages()
                .child(id);
    }
}
