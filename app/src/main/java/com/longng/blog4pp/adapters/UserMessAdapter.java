package com.longng.blog4pp.adapters;

import android.content.Context;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.longng.blog4pp.R;
import com.longng.blog4pp.models.BlogPostModel;
import com.longng.blog4pp.models.UserModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserMessAdapter extends RecyclerView.Adapter<UserMessAdapter.ViewHolder> {
    private Context context;
    private List<UserModel> userList;
    private int layoutRes;
    private OnItemClickListener onItemClickListener;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;
    private String currentId;

    public UserMessAdapter( List<UserModel> userList, int layoutRes, OnItemClickListener onItemClickListener) {
        this.userList = userList;
        this.layoutRes = layoutRes;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        firebaseFirestore =FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentId = mAuth.getCurrentUser().getUid();


        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(layoutRes,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);

        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserModel user = userList.get(position);

        Log.d("minhdz", "checked");

        holder.txtNameFriends.setText(user.getEmail());
        holder.txtLastMessages.setText("this is last message...");
        firebaseFirestore.collection("Users").document(user.getAvartar()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        String userAvartar = task.getResult().getString("image");
                        holder.imvAvartars(userAvartar);
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

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView imvAvartars;
        private TextView txtNameFriends;
        private  TextView txtLastMessages;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imvAvartars = itemView.findViewById(R.id.imvAvartars);
            txtNameFriends = itemView.findViewById(R.id.txtNameFriends);
            txtLastMessages = itemView.findViewById(R.id.txtLastMessages);
        }

        private void imvAvartars(String userAvartar) {
            Glide.with(context).load(userAvartar).into(imvAvartars);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(UserModel user);
        void onItemLongClick(UserModel user);
    }
}
