package com.example.healthcare.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.healthcare.Activity.Forum;
import com.example.healthcare.Activity.Signup_Patient;
import com.example.healthcare.Model.ImageDp;
import com.example.healthcare.Model.Like;
import com.example.healthcare.Model.NewsFeedModel;
import com.example.healthcare.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.NewsFeedViewHolder> {
    Context mContext;
    List<NewsFeedModel> newsFeedsList;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;

    public NewsFeedAdapter(Context mcontext,List<NewsFeedModel> newsFeedsList){
        this.mContext=mcontext;
        this.newsFeedsList=newsFeedsList;
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public NewsFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.news_layout,parent,false);
        return new NewsFeedViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final NewsFeedViewHolder holder, final int position) {
        final String title=newsFeedsList.get(position).getTitle();
        final String description=newsFeedsList.get(position).getDescription();


        Log.i("value == ",newsFeedsList.get(position).getTitle());

        holder.name.setText(newsFeedsList.get(position).getName());
        holder.tvpostTitle.setText(title);
        holder.tvpostDate.setText("21-10-1997");
        holder.tvPostDescription.setText(description);
        if (newsFeedsList.get(position).getImageUri().equals("null")){
            holder.image.setVisibility(View.GONE);
        }else{
            Glide.with(mContext).load(newsFeedsList.get(position).getImageUri()).placeholder(R.mipmap.ic_launcher).into(holder.image);
        }

        GetDpOfUser(newsFeedsList.get(position).getUserid(),holder.senderPic);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, Forum.class);
                intent.putExtra("postTitle",title);
                intent.putExtra("imageUrl",newsFeedsList.get(position).getImageUri());
                intent.putExtra("date","21-10-1997");
                intent.putExtra("description",description);
                intent.putExtra("pushKey",newsFeedsList.get(position).getPushKey());
                intent.putExtra("UserId",newsFeedsList.get(position).getUserid());
                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return newsFeedsList.size();
    }

    public class NewsFeedViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvpostTitle;
        TextView tvpostDate;
        TextView tvPostDescription,name;
        ImageView image;
        CircleImageView senderPic;

        public NewsFeedViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView=itemView.findViewById(R.id.postCard);
            tvpostTitle=itemView.findViewById(R.id.postTitle);
            tvpostDate=itemView.findViewById(R.id.postDate);
            tvPostDescription=itemView.findViewById(R.id.postDiscription);
            image=itemView.findViewById(R.id.postimage);
            name=itemView.findViewById(R.id.name);
            senderPic = itemView.findViewById(R.id.senderPic);
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
                    Glide.with(mContext).load(dpUri.getDp()).placeholder(R.mipmap.ic_launcher).into(dp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //////////// This function will set the Dp to specific User post ////////////////////



    public void AddLikeToCurrentPost(String pushKey){
        databaseReference= FirebaseDatabase.getInstance().getReference("Like");
        databaseReference.child(pushKey).child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null){
                    Like liked =dataSnapshot.getValue(Like.class);
                    if (liked.isLiked()){
                        dataSnapshot.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(mContext, "Disliked", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }else {
                    HashMap<String,Object> map=new HashMap<>();
                    map.put("Liked",true);
                    databaseReference.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(mContext, "Liked", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(mContext, "something went wrong", Toast.LENGTH_SHORT).show();
            }
        });



    }

    public void GetLikesOfCurrentPostFromDatabase(String pushKey){
        databaseReference=FirebaseDatabase.getInstance().getReference("Like");
        databaseReference.child(pushKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(mContext, "Likes "+dataSnapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(mContext, "something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
