package com.longng.blog4pp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.longng.blog4pp.CommentActivity;
import com.longng.blog4pp.R;
import com.longng.blog4pp.models.BlogPostModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

//Adapter class
public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder>{

    public List<BlogPostModel> blogList;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;
    String blogPostId, currentUser;
    private Context context;
    private String user_id;

    //Constructor which receives list of model class BlogPostModel
    public BlogRecyclerAdapter(List<BlogPostModel> blog_list) {
        this.blogList = blog_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            firebaseFirestore = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser().getUid();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item,parent,false);
        // for Glide
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final String blogPostId = blogList.get(position).BlogPostId;
        holder.setIsRecyclable(false);
        String desc_text = blogList.get(position).getDesc();
        holder.setDescText(desc_text);

        String download_uri = blogList.get(position).getImage_url();
        String thumb_uri = blogList.get(position).getImage_thumb();
        holder.setImage(download_uri,thumb_uri);

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            //get user id and retrieve avatar stored in 'Users' collection
            user_id = blogList.get(position).getUser_id();
            firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            String name = task.getResult().getString("name");
                            String image = task.getResult().getString("image");
                            // populate both image and username to RecyclerView
                            holder.setUserImage(image);
                            holder.setUserName(name);
                        }
                    }
                }
            });
        }

        //Set date
        long milliseconds = blogList.get(position).getTimeStamp().getTime();
        String pattern = "E, dd MM yy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String dateString1 = simpleDateFormat.format(new Date(milliseconds));
        pattern = "HH:mm a";
        simpleDateFormat = new SimpleDateFormat(pattern);
        String dateString2 = simpleDateFormat.format(new Date(milliseconds));
        holder.setDate(dateString1 + " at " + dateString2);

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            //Get likes count
            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    try {
                        if (!documentSnapshots.isEmpty()) {
                            int count = documentSnapshots.size();
                            holder.setLikes(count);
                        }
                        else
                            holder.setLikes(0);
                    }
                    catch (NullPointerException E) {
                        Toast.makeText(context.getApplicationContext(), "Error: " + e, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        //Get comments count
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            firebaseFirestore.collection("Posts/" + blogPostId + "/Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    try {
                        if (!documentSnapshots.isEmpty()) {
                            int countComments = documentSnapshots.size();
                            holder.setComments(countComments);
                        }
                        else
                            holder.setComments(0);
                    }
                    catch (NullPointerException E){
                        Toast.makeText(context.getApplicationContext(), "Error: " + e, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        //Get likes icon
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUser).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                    try {
                        if (documentSnapshot.exists())
                            holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.ic_like_red));
                        else
                            holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.ic_like_gray));
                    }
                    catch (NullPointerException E) {
                        Toast.makeText(context.getApplicationContext(), "Error: " + e, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        //Pressed like button
        holder.blogLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUser).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (!task.getResult().exists()) {
                                Map<String, Object> likeMap = new HashMap<>();
                                likeMap.put("timeStamp", FieldValue.serverTimestamp());
                                firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUser).set(likeMap);
                            }
                            else
                                firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUser).delete();
                        }
                    });
                }
            }
        });

        //Comment
        holder.blogCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent commentIntent = new Intent(context, CommentActivity.class);
                commentIntent.putExtra("BlogPostId", blogPostId);
                context.startActivity(commentIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView descView;
        private TextView userName;
        private TextView postDate;
        private ImageView postImage;
        private CircleImageView userImage;
        private View mView;
        private TextView blogLikeCount;
        private TextView blogCommentCount;
        private ImageView blogLikeBtn;
        private ImageView blogCommentBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            blogLikeBtn = mView.findViewById(R.id.blog_like);
            blogCommentCount = mView.findViewById(R.id.blog_comment_count);
            blogCommentBtn = mView.findViewById(R.id.blog_comment);
            blogLikeCount = mView.findViewById(R.id.blog_like_count);
            userName = mView.findViewById(R.id.commentUsername);
            userImage = mView.findViewById(R.id.commentProfilePic);
            postDate = mView.findViewById(R.id.blog_date);
            postImage = mView.findViewById(R.id.blog_image);
        }

        //Set description text
        public void setDescText(String descText){
            descView = mView.findViewById(R.id.blogDescription);
            descView.setText(descText);

        }

        //set username
        public void setUserName(String userNameText){
            userName.setText(userNameText);
        }

        public void setDate(String dateOfPost){
            postDate.setText(dateOfPost);
        }

        //Set image
        public void setImage(String downloadURL, String thumb_url){
            //use glide to save image into ImageView
            Glide.with(context).load(downloadURL).thumbnail(Glide.with(context).load(thumb_url)).into(postImage);
        }

        //User Image
        public void setUserImage(String userImage_URL){
            Glide.with(context).load(userImage_URL).into(userImage);
        }

        //Set Like Count
        private void setLikes(int count){
            String text = count + " Likes";
            blogLikeCount.setText(text);
        }

        private void setComments(int countC){
            String textC = countC + " Comments";
            blogCommentCount.setText(textC);
        }
    }
}

