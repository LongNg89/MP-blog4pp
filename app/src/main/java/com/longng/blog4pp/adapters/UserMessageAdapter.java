package com.longng.blog4pp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.longng.blog4pp.R;
import com.longng.blog4pp.databaseReference.DatabaseManager;
import com.longng.blog4pp.models.UserModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserMessageAdapter extends RecyclerView.Adapter<UserMessageAdapter.ViewHolder> {
    private Context context;
    private List<UserModel> userList;
    private int layoutRes;
    private OnItemClickListener onItemClickListener;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;
    private String currentId;

    private DateFormat dateFormat = new SimpleDateFormat("hh:mm");

    public UserMessageAdapter(List<UserModel> userList, int layoutRes, OnItemClickListener onItemClickListener) {
        this.userList = userList;
        this.layoutRes = layoutRes;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentId = mAuth.getCurrentUser().getUid();

        context = parent.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(layoutRes,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserModel user = userList.get(position);

        holder.txtNameFriends.setText(user.getUsername());
        setLastMessageAnTimeByUID(user.getUid()+"",holder);
        firebaseFirestore.collection("Users").document(user.getUid()+"").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        String userAvatar = task.getResult().getString("image");
                        holder.imvAvatars(userAvatar);
                    }
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null){
                    onItemClickListener.onItemClick(user);
                }
            }
        });
    }

    private String convertTime(Date date) {
        return dateFormat.format(date);
    }

    private void setLastMessageAnTimeByUID(String myFriendID,ViewHolder holder) {
        DatabaseReference dbRef = DatabaseManager.getInstance().getTableMessagesByID(currentId);
        Query query = dbRef.child(myFriendID + "").orderByKey().limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot ds : snapshot.getChildren()){
                        String message = ds.child("message").getValue(String.class);
                        Date timeForMessage = ds.child("timeForMessage").getValue(Date.class);
                        if (message != null&&timeForMessage!=null){
                            holder.txtLastMessages.setText(message);
                            holder.txtTimeOfLastMessage.setText(convertTime(timeForMessage));
                        }else{
                            holder.txtLastMessages.setText("you haven't texted this person yet");
                            holder.txtTimeOfLastMessage.setText("");
                        }
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView imvAvatars;
        private TextView txtNameFriends;
        private TextView txtLastMessages;
        private TextView txtTimeOfLastMessage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imvAvatars = itemView.findViewById(R.id.imvAvatars);
            txtNameFriends = itemView.findViewById(R.id.txtNameFriends);
            txtLastMessages = itemView.findViewById(R.id.txtLastMessages);
            txtTimeOfLastMessage = itemView.findViewById(R.id.txtTimeOfLastMessage);
        }

        private void imvAvatars(String userAvatar) {
            Glide.with(context).load(userAvatar).into(imvAvatars);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(UserModel user);
        void onItemLongClick(UserModel user);
    }
}
