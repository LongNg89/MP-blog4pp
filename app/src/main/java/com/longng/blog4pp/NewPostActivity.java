package com.longng.blog4pp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class NewPostActivity extends AppCompatActivity {
    private EditText newPostText;
    private String user_id;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private ImageView newPostImg;
    private Uri main_uri = null;
    private ProgressBar progressBar;
    private String postText;
    private Bitmap compressedImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        //Toolbar
        Toolbar toolbarNewPost = findViewById(R.id.toolbarNewPost);
        getSupportActionBar().setTitle("Add New Post");

        //add back button to parent activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        newPostImg = findViewById(R.id.newPostImage);
        newPostText = findViewById(R.id.newPostDescription);
        Button submitBtn = findViewById(R.id.newPostBtn);
        progressBar = findViewById(R.id.newPostProgress);

        newPostImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512,512)
                        .setAspectRatio(1,1)
                        .start(NewPostActivity.this);
            }
        });

        //clicked on submit
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postText = newPostText.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                if (main_uri != null && !TextUtils.isEmpty(postText)) {
                    progressBar.setVisibility(View.VISIBLE);
                    final String randomName = UUID.randomUUID().toString();
                    //Create storage path reference
                    StorageReference filePath = storageReference.child("post_images").child(randomName + ".jpg");
                    //url of image to be put in the above path
                    //This uploads image to Post_images folder in FB Storage. To determine hierarchy need to use map to put in collections
                    filePath.putFile(main_uri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot task) {
                                    Task<Uri> uriTask = task.getStorage().getDownloadUrl();
                                    while (!uriTask.isSuccessful());
                                    String downloadUrl = uriTask.getResult().toString();
                                    if(uriTask.isSuccessful()) {
                                        //upload thumbnail compressed
                                        File imageFile = new File(main_uri.getPath());
                                        try {
                                            compressedImageBitmap = new Compressor(NewPostActivity.this)
                                                    .setMaxHeight(100)
                                                    .setMaxWidth(100)
                                                    .setQuality(1)
                                                    .compressToBitmap(imageFile);
                                        }
                                        catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        //Upload bitmap to firebase
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        compressedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                        byte[] thumbBitmap = baos.toByteArray();

                                        UploadTask thumbImage = storageReference.child("/post_images/thumbs").child(randomName+".jpg").putBytes(thumbBitmap);
                                        thumbImage.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                if (uriTask.isSuccessful()) {
                                                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                                    while (!uriTask.isSuccessful());
                                                    String thumbUri = uriTask.getResult().toString();

                                                    Map<String,Object> postMap = new HashMap<>();
                                                    postMap.put("image_url",downloadUrl);
                                                    postMap.put("desc",postText);
                                                    postMap.put("user_id",user_id);
                                                    postMap.put("image_thumb",thumbUri);
                                                    postMap.put("timeStamp", FieldValue.serverTimestamp());

                                                    //Finally add everything to collection
                                                    //don't add .document as it must be created and named randomly by firebase automatically
                                                    firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                                            if (task.isSuccessful()) {
                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                Toast.makeText(NewPostActivity.this, "Post Successful!", Toast.LENGTH_LONG).show();
                                                                Intent main = new Intent(NewPostActivity.this,MainActivity.class);
                                                                startActivity(main);
                                                                finish();
                                                            }
                                                            else {
                                                                Toast.makeText(NewPostActivity.this, "Error" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                                progressBar.setVisibility(View.INVISIBLE);
                                                            }
                                                        }
                                                    });
                                                }
                                                else {
                                                    Toast.makeText(NewPostActivity.this, "Error" + uriTask.getException().toString(), Toast.LENGTH_SHORT).show();
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                }
                                            }
                                        });

                                    }
                                }
                            });
                }
                else {
                    Toast.makeText(NewPostActivity.this, "No Image or Description", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                main_uri = result.getUri();
                newPostImg.setImageURI(main_uri);
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(NewPostActivity.this,"Error" + error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}