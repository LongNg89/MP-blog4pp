package com.longng.blog4pp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.longng.blog4pp.R;
import com.longng.blog4pp.models.MessageModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private Context context;
    private List<MessageModel> messageList;
    private int layoutRes;
    private String id;
    private MessageAdapter.OnItemClickListener onItemClickListener;

    private DateFormat dateFormat = new SimpleDateFormat("hh:mm");

    public MessageAdapter(Context context, List<MessageModel> messageList, int layoutRes, String id, MessageAdapter.OnItemClickListener onItemClickListener) {
        this.context = context;
        this.messageList = messageList;
        this.layoutRes = layoutRes;
        this.id = id;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(layoutRes,parent,false);
        MessageAdapter.ViewHolder viewHolder = new MessageAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        MessageModel message = messageList.get(position);
        if (message.getSenderID().equals(id)){
            holder.llYourFriendMessage.setVisibility(View.GONE);
            holder.txtYourMessage.setText(message.getMessage());
            holder.txtTimeYourMessage.setText(convertTime(message.getTimeForMessage()));
        }else{
            holder.llYourMessage.setVisibility(View.GONE);
            holder.txtYourFriendMessage.setText(message.getMessage());
            holder.txtTimeYourFriendMessage.setText(message.getTimeForMessage().toString());
        }

//        Log.d("minhdz", "time: "+message.getMessage());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null){
                    onItemClickListener.onItemClick(message);
                }
            }
        });
    }

    private String convertTime(Date date) {
        return dateFormat.format(date);
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
