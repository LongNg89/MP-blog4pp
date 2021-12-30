package com.longng.blog4pp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.longng.blog4pp.fragments.AccountFragment;
import com.longng.blog4pp.fragments.HomeFragment;
import com.longng.blog4pp.fragments.NotificationFragment;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolBar;
    private String user_id;
    private FloatingActionButton newPostButton;
    private BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment;
    private AccountFragment accountFragment;
    private NotificationFragment notificationFragment;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newPostButton = findViewById(R.id.addPostBtn);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() != null) {
            bottomNavigationView = findViewById(R.id.mainBottomNav);
            // fragments
            homeFragment = new HomeFragment();
            notificationFragment = new NotificationFragment();
            accountFragment = new AccountFragment();

            replaceFragment(homeFragment);

            // Onclick on Bottom navigation bar
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.bottomAccount:
                            replaceFragment(accountFragment);
                            return true;
                        case R.id.bottomHome:
                            replaceFragment(homeFragment);
                            return true;
                        case R.id.bottomNotification:
                            replaceFragment(notificationFragment);
                            return true;
                    }
                    return false;
                }
            });

            newPostButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser != null) {
                        user_id = mAuth.getCurrentUser().getUid();
                        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.getResult().exists()) {
                                    Intent addPost = new Intent(MainActivity.this, NewPostActivity.class);
                                    startActivity(addPost);
                                }
                                else {
                                    // if user hasn't set up name and avatar, go to AccountSetupActivity
                                    Toast.makeText(MainActivity.this, "Please choose profile photo and name", Toast.LENGTH_LONG).show();
                                    Intent accSetup = new Intent(MainActivity.this, AccountSetupActivity.class);
                                    startActivity(accSetup);
                                }
                            }
                        });
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Please choose profile photo and name", Toast.LENGTH_LONG).show();
                        Intent accSetup = new Intent(MainActivity.this, AccountSetupActivity.class);
                        startActivity(accSetup);
                    }
                }
            });

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Check if user has logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){
            sendToLogin();
            finish();
        }
    }

    //add menu drawable resource to action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logOut();
                return true;
            case R.id.action_settings:
                Intent accountSetup = new Intent(MainActivity.this, AccountSetupActivity.class);
                startActivity(accountSetup);
                return true;
            default:
                return false;
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    private void sendToLogin() {
        Intent login = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(login);
    }

    private void logOut() {
        mAuth.signOut();
        sendToLogin();
    }

    //Fragment transition to change fragment when pressed
    private void replaceFragment(androidx.fragment.app.Fragment fragment){
        androidx.fragment.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainContainer,fragment);
        fragmentTransaction.commit();
    }
}
