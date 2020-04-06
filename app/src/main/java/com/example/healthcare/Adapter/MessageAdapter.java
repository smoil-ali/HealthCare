package com.example.healthcare.Adapter;

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
/*import com.example.healthcare.Activities.ImageViewActivity;*/
import com.example.healthcare.Model.ChatModel;
import com.example.healthcare.R;
/*import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;*/

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    public static final int MSG_TYPE_RIGHT_IMAGE = 2;
    public static final int MSG_TYPE_RIGHT_LEFT = 3;
    private Context context;
    private List<ChatModel> list;
   /* private FirebaseUser firebaseUser;*/
    private String imageurl;

    public MessageAdapter(Context context, List<ChatModel> list,String imageurl) {
        this.context = context;
        this.list = list;
        this.imageurl= imageurl;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT){
            View view= LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent,false);
            return new MessageViewHolder(view);
        }if (viewType == MSG_TYPE_RIGHT_IMAGE){
            View view= LayoutInflater.from(context).inflate(R.layout.chat_item_right_img,parent,false);
            return new MessageViewHolder(view);
        }if (viewType == MSG_TYPE_RIGHT_LEFT) {
            View view= LayoutInflater.from(context).inflate(R.layout.chat_item_left_img,parent,false);
            return new MessageViewHolder(view);
        }else {
            View view= LayoutInflater.from(context).inflate(R.layout.chat_item_left,parent,false);
            return new MessageViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        final ChatModel chatModel=list.get(position);


        if (imageurl.equals("default")){
            holder.imageView.setImageResource(R.mipmap.ic_launcher);
        }else {
            Glide.with(context).load(imageurl).into(holder.imageView);
        }


        if ( !list.get(position).getSendedImageUri().equals("null") && list.get(position).getMessage().equals("") ){
            Glide.with(context).load(chatModel.getSendedImageUri()).placeholder(R.mipmap.ic_launcher).into(holder.chatImage);
            holder.chatImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   /* Intent intent=new Intent(context, ImageViewActivity.class);
                    intent.putExtra("imagekey",chatModel.getSendedImageUri());
                    context.startActivity(intent);*/
                }
            });
        }else {
            holder.show_message.setText(chatModel.getMessage());
        }


        if (position == list.size()-1){
            if (chatModel.isIsseen()){
                holder.textseen.setText("Seen");
            }else {
                holder.textseen.setText("Delivered");
            }
        }else {
            holder.textseen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView show_message,textseen;
        ImageView imageView,chatImage;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            show_message=itemView.findViewById(R.id.show_message);
            imageView=itemView.findViewById(R.id.left_profile_image);
            textseen=itemView.findViewById(R.id.txt_seen);
            chatImage=itemView.findViewById(R.id.show_sended_image);
        }
    }

    @Override
    public int getItemViewType(int position) {
        /*firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if (list.get(position).getSender().equals(firebaseUser.getUid())){
            if ( !list.get(position).getSendedImageUri().equals("null") && list.get(position).getMessage().equals("") &&
                            list.get(position).getSender().equals(firebaseUser.getUid())){
                return MSG_TYPE_RIGHT_IMAGE;
            }
            return MSG_TYPE_RIGHT;
        }else {
            if ( !list.get(position).getSendedImageUri().equals("null") && list.get(position).getMessage().equals("") ){
                return MSG_TYPE_RIGHT_LEFT;
            }
            return MSG_TYPE_LEFT;
        }*/
        return 0;
    }
}
