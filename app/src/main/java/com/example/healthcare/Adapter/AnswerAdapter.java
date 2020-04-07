package com.example.healthcare.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.healthcare.Activity.ChatActivity;
import com.example.healthcare.Activity.Forum;
import com.example.healthcare.Model.AnsweredModelClass;
import com.example.healthcare.Model.ImageDp;
import com.example.healthcare.Model.Like;
import com.example.healthcare.Model.UserConstantModel;
import com.example.healthcare.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.healthcare.BastardClass.ANSWERED_LIKES;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.AnswerViewHolder> {

    Context context;
    List<AnsweredModelClass> answeredModelClasses=new ArrayList<>();

    public AnswerAdapter(Context context, List<AnsweredModelClass> answeredModelClasses) {
        this.context = context;
        this.answeredModelClasses = answeredModelClasses;
    }

    @NonNull
    @Override
    public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_recyclerview_layout,parent,false);
        return new AnswerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AnswerViewHolder holder, final int position) {
        Glide.with(context).load("null").placeholder(R.mipmap.ic_launcher).into(holder.answerDp);
        holder.answerDoctorName.setText(answeredModelClasses.get(position).getNameOfDoctor());

        Log.i("answered value == ",answeredModelClasses.get(position).getPushKey()+" "+answeredModelClasses.get(position).getNameOfDoctor());

        GetLikesOfCurrentPostOfCurrentAnswer(holder.noOfLikes,answeredModelClasses.get(position).getPushKey());
        GetDpOfUser(answeredModelClasses.get(position).getDoctorUid(),holder.answerDp);
        holder.answer.setText(answeredModelClasses.get(position).getAnswer());
        CheckIfUserAlreadyLikedAnswer(holder.answeredLiked,answeredModelClasses.get(position).getPushKey());
        holder.answeredLiked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAnsweredLikes(answeredModelClasses.get(position).getPushKey());
            }
        });



        holder.answerChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(context, ChatActivity.class);
                intent.putExtra("key",answeredModelClasses.get(position).getDoctorUid());
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return answeredModelClasses.size();
    }

    public class AnswerViewHolder extends RecyclerView.ViewHolder {
        CircleImageView answerDp;
        TextView answerDoctorName,noOfLikes,answer,answeredLiked,answerChat;
        public AnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            answerDp=itemView.findViewById(R.id.answerDp);
            answerDoctorName=itemView.findViewById(R.id.answerName);
            noOfLikes=itemView.findViewById(R.id.noOfLikes);
            answer=itemView.findViewById(R.id.answerDescription);
            answeredLiked=itemView.findViewById(R.id.answerLiked);
            answerChat=itemView.findViewById(R.id.answerChat);
        }
    }

    //////////// This function will set the Dp to specific User post ////////////////////

    public void GetDpOfUser(String UserId, final CircleImageView dp){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("ImageDps");
        databaseReference.child(UserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    ImageDp dpUri=dataSnapshot.getValue(ImageDp.class);
                    Glide.with(context).load(dpUri.getDp()).placeholder(R.mipmap.ic_launcher).into(dp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //////////// This function will set the Dp to specific User post ////////////////////

    //////////// This function will set the Like to specific Answer ////////////////////

    public  void setAnsweredLikes(String pushKeyOfAnswer){
        final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(ANSWERED_LIKES);
        databaseReference.child(pushKeyOfAnswer).child(UserConstantModel.Uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null){
                    Like liked =dataSnapshot.getValue(Like.class);
                    if(liked != null){
                        if (liked.isLiked()){
                            dataSnapshot.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(context, "Disliked", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }else {
                        HashMap<String,Object> map=new HashMap<>();
                        map.put("Liked",true);
                        dataSnapshot.getRef().updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Liked", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show();
            }
        });



    }

    //////////// This function will set the Like to specific Answer ////////////////////



    //////////// This function will get the number of Likes of specific answer of specific Post ////////////////

    public  void GetLikesOfCurrentPostOfCurrentAnswer(final TextView noOfLikes, String pushKeyOfAnswer){
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(ANSWERED_LIKES);
        databaseReference.child(pushKeyOfAnswer).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    noOfLikes.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //////////// This function will get the Likes of specific answer of specific Post ////////////////





    /////// This function will set if user liked then text will set to liked and color will be changed/////////////////



    public void CheckIfUserAlreadyLikedAnswer(final TextView Liked, String pushKey){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(ANSWERED_LIKES);
        databaseReference.child(pushKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild(UserConstantModel.Uid)) {
                        Liked.setText("Liked");
                        Liked.setTextColor(context.getResources().getColor(R.color.LikedColor));
                    }
                } else {
                    Liked.setText("Like");
                    Liked.setTextColor(context.getResources().getColor(R.color.LikeColor));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /////// This function will set if user liked then text will set to liked and color will be changed/////////////////
}
