package com.longng.blog4pp.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.longng.blog4pp.R;
import com.longng.blog4pp.models.MessageModel;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
    private Context context;
    private List<MessageModel> messageList;
    private int layoutRes;
    private String id;
    private MessageAdapter.OnItemClickListener onItemClickListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtYourMessage;
        private TextView txtTimeYourMessage;
        private LinearLayout llYourMessage;
        private TextView txtYourFriendMessage;
        private TextView txtTimeYourFriendMessage;
        private LinearLayout llYourFriendMessage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtYourMessage = itemView.findViewById(R.id.txtYourMessage);
            txtTimeYourMessage = itemView.findViewById(R.id.txtTimeYourMessage);
            llYourMessage = itemView.findViewById(R.id.llYourdMessage);

            txtYourFriendMessage = itemView.findViewById(R.id.txtYourFriendMessage);
            txtTimeYourFriendMessage = itemView.findViewById(R.id.txtTimeYourFriendMessage);
            llYourFriendMessage = itemView.findViewById(R.id.llYourFriendMessage);

        }
    }

    public interface OnItemClickListener{
        void onItemClick(MessageModel message);
        void onItemLongClick(MessageModel message);
    }
}
