package com.longng.blog4pp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ModuleInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.longng.blog4pp.databaseReference.DataBaseManager;
import com.longng.blog4pp.models.UserModel;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountSetupActivity extends AppCompatActivity {
    //imports
    private ProgressBar progressBar;
    private CircleImageView userImg;
    private Uri main_uri = null;
    private Uri default_uri = null;

    private FirebaseAuth mAuth;
    private FirebaseFirestore fireStore;
    private boolean isChanged = true;

    private StorageReference mStorageRef;
    private Button submit;
    private String user_id;
    private EditText userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setup);

        submit = findViewById(R.id.saveBtn);
        progressBar = findViewById(R.id.accountProgressBar);
        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        fireStore = FirebaseFirestore.getInstance();
        userName = findViewById(R.id.username);
        userImg = findViewById(R.id.profile);

        default_uri = Uri.parse("R.mipmap.user");

        // Each time go to this activity, check if username and avatar have already present in FireStore, if yes retrieve and set username & avatar using Glide
        fireStore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                progressBar.setVisibility(View.VISIBLE);
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("image");
                        Toast.makeText(AccountSetupActivity.this, "DATA EXISTS", Toast.LENGTH_SHORT).show();
                        userName.setText(name);

                        // Glide set avatar placeholder
                        RequestOptions placeHolder = new RequestOptions();
                        placeHolder.placeholder(R.mipmap.user);

                        //Convert image string to URI and store it in mainImageUri
                        main_uri = Uri.parse(image);
                        Glide.with(AccountSetupActivity.this).setDefaultRequestOptions(placeHolder.placeholder(R.mipmap.user)).load(image).into(userImg);
                    }
                    else {
                        main_uri = default_uri;
                        Toast.makeText(AccountSetupActivity.this, "NO DATA EXISTS", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else {
                    Toast.makeText(AccountSetupActivity.this, "Firestore Retrieve Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // upload username and icon to FireStore
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String uName = userName.getText().toString();
                if (isChanged) {
                    if (TextUtils.isEmpty(uName))
                        userName.setError("Please enter user name!");
                    else
                        uploadProfile(uName);
                }
            }
        });

        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //read permission available in Android, ask for permissions
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //Toast.makeText(AccountSetupActivity.this, "ReadPermission", Toast.LENGTH_SHORT).show();
                    if (ContextCompat.checkSelfPermission(AccountSetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AccountSetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                        ActivityCompat.requestPermissions(AccountSetupActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                    //Toast.makeText(AccountSetupActivity.this, "WritePermission", Toast.LENGTH_SHORT).show();
                    if (ContextCompat.checkSelfPermission(AccountSetupActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AccountSetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                        ActivityCompat.requestPermissions(AccountSetupActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                    }
                }
                getPicture();
            }

            private void getPicture() {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(AccountSetupActivity.this);
                Log.d("mpProject","getAvatar:success");
            }
        });
    }

    private void uploadProfile(String uName) {
        if (userImg.getDrawable() != null) {
            userImg.setDrawingCacheEnabled(true);
            userImg.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) userImg.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask avatar = mStorageRef.child("/Profile_photos/thumbnails").child(user_id + ".jpg").putBytes(data);
            avatar.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;
                    String downloadUri = uriTask.getResult().toString();

                    if (uriTask.isSuccessful()) {
                        //Create HashMap with keys and values
                        Map<String, String> userMap = new HashMap<>();
                        userMap.put("name", uName);
                        userMap.put("image", downloadUri);
                        updateUser(user_id,uName,downloadUri);
                        fireStore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("mpProject", "uploadToFireStore:success");
                                    Toast.makeText(AccountSetupActivity.this, "Settings Saved Successfully", Toast.LENGTH_LONG).show();
                                    Intent main = new Intent(AccountSetupActivity.this, MainActivity.class);
                                    startActivity(main);
                                } else {
                                    Log.w("mpProject", "uploadToFireStore:failure", task.getException());
                                    String error = task.getException().getMessage();
                                    Toast.makeText(AccountSetupActivity.this, " FireStore Error" + error, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AccountSetupActivity.this, "Image Error" + e, Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }
        else {
            Toast.makeText(AccountSetupActivity.this, "No image", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                userImg.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(AccountSetupActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateUser(String uid, String uname, String downloadUri){
        Map<String,Object> update = new HashMap();
        update.put("/username/",uname);
        update.put("/avartar/",downloadUri);
        DataBaseManager
                .getInstance()
                .getTableUsersByID(uid) // reference to object id in realtime db
                .updateChildren(update);
    }

}
