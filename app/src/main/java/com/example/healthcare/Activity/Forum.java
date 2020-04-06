package com.example.healthcare.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Point;
import android.media.Image;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.healthcare.Adapter.AnswerAdapter;
import com.example.healthcare.Adapter.CommentAdapter;
import com.example.healthcare.Interface.APIService;
import com.example.healthcare.Model.AnsweredModelClass;
import com.example.healthcare.Model.CommentModelClass;
import com.example.healthcare.Model.Like;
import com.example.healthcare.Model.UserConstantModel;
import com.example.healthcare.Notification.Client;
import com.example.healthcare.Notification.sendNotificationClass;
import com.example.healthcare.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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
import static com.example.healthcare.BastardClass.ANSWERED_POST;

public class Forum extends AppCompatActivity {
    TextView postTitleForum,descriptionForum,dateForum;
    ImageView image;
    private PopupWindow popWindow;
    private Button btnAnswer,btnComment,likeButton;
    RecyclerView AnswerRecyclerView;
    RecyclerView CommentRecyclerView;
    AnswerAdapter answerAdapter;
    CommentAdapter commentAdapter;
    List<AnsweredModelClass> answeredModelClasses = new ArrayList<>();
    List<CommentModelClass> commentModelClasses = new ArrayList<>();
    Bundle bundle;
    String LIKES = "Likes";
    final String PATIENT= "patient";
    public EditText Answer,Comment;
    public ImageButton btnSend,commentButtonSend;
    public  final String ANSWERED_POST="AnsweredPost";
    public  final String COMMENTED_POST="CommentedPost";
    public TextView alertComment;
    public TextView alertAnswer;
    public String UserId;
    CircleImageView senderPic;
    public static APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        postTitleForum=findViewById(R.id.postTitleForum);
        descriptionForum=findViewById(R.id.descriptionForum);
        dateForum=findViewById(R.id.postDateForum);
        image=findViewById(R.id.imagePostForum);
        btnAnswer=findViewById(R.id.btnAnswerForum);
        btnComment=findViewById(R.id.btnCommentForum);
        likeButton=findViewById(R.id.likeButton);
        senderPic = findViewById(R.id.profileImageForum);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);


        bundle =getIntent().getExtras();
        postTitleForum.setText(bundle.getString("postTitle"));
        descriptionForum.setText(bundle.getString("description"));
        dateForum.setText(bundle.getString("date"));
        Glide.with(this).load( bundle.getString("imageUrl")).placeholder(R.mipmap.ic_launcher).into(image);
        Glide.with(this).load( "null").placeholder(R.mipmap.ic_launcher).into(senderPic);
        UserId = bundle.getString("UserId");
        Log.i("bundle = ",bundle.getString("pushKey"));

        btnAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUpAnswer(v);
            }
        });
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUpComments(v);
            }
        });

        CheckIfUserAlreadyLikedAnswer(bundle.getString("pushKey"));
        //GetLikesOfCurrentPost(bundle.getString("pushKey"));

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPostLikes(bundle.getString("pushKey"));
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                if (getParentActivityIntent() == null) {
                    onBackPressed();
                } else {
                    NavUtils.navigateUpFromSameTask(this);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void showPopUpAnswer(View v) {

        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // inflate the custom popup layout
        View inflatedView = layoutInflater.inflate(R.layout.answer_layout, null,false);
        // find the ListView in the popup layout

        alertAnswer = inflatedView.findViewById(R.id.alertMessageAnswer);
        Answer = inflatedView.findViewById(R.id.comment2);
        btnSend = inflatedView.findViewById(R.id.btnSend);


        if (UserConstantModel.UserType.equals(PATIENT)){
            Answer.setVisibility(View.GONE);
            btnSend.setVisibility(View.GONE);
        }else {
            Answer.setVisibility(View.VISIBLE);
            btnSend.setVisibility(View.VISIBLE);
        }


        AnswerRecyclerView = inflatedView.findViewById(R.id.answerRecyclerView);
        AnswerRecyclerView.hasFixedSize();
        AnswerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Answer.getText().equals("")){
                    setAnsweredPost(bundle.getString("pushKey"),Forum.this,Answer.getText().toString());
                    Answer.setText("");
                }else {
                    Toast.makeText(Forum.this, "empty Answer", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // get device size
        Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
//        mDeviceHeight = size.y;
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;


        // fill the data to the list items


        // set height depends on the device size
        popWindow = new PopupWindow(inflatedView, width,height-50, true );
        // set a background drawable with rounders corners
        popWindow.setBackgroundDrawable(getResources().getDrawable(R.color.white));

        popWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        popWindow.setAnimationStyle(R.style.PopupAnimation);

        // show the popup at bottom of the screen and set some margin at bottom ie,
        getAnsweredPost(bundle.getString("pushKey"),v);
    }

    private void showPopUpComments(View v) {

        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // inflate the custom popup layout
        View inflatedView = layoutInflater.inflate(R.layout.comment_layout, null,false);
        // find the ListView in the popup layout

        alertComment = inflatedView.findViewById(R.id.AlertMessageComment);
        Comment = inflatedView.findViewById(R.id.comment2);
        commentButtonSend = inflatedView.findViewById(R.id.btnSend);
        CommentRecyclerView = inflatedView.findViewById(R.id.commentRecyclerView);
        CommentRecyclerView.hasFixedSize();
        CommentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Comment.getText().equals("")){
                    setCommentPost(bundle.getString("pushKey"),Forum.this,Comment.getText().toString());
                    Comment.setText("");
                }else {
                    Toast.makeText(Forum.this, "empty comment", Toast.LENGTH_SHORT).show();
                }


            }
        });

        // get device size
        Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
//        mDeviceHeight = size.y;
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;


        // fill the data to the list items


        // set height depends on the device size
        popWindow = new PopupWindow(inflatedView, width,height-50, true );
        // set a background drawable with rounders corners
        popWindow.setBackgroundDrawable(getResources().getDrawable(R.color.white));

        popWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        popWindow.setAnimationStyle(R.style.PopupAnimation);

        // show the popup at bottom of the screen and set some margin at bottom ie,
        getCommentedPost(bundle.getString("pushKey"),v);
    }



    /////////// This function Will get Answers related to specific Post //////////////////

    public  void getAnsweredPost(String pushKeyOfPost, final View v){
        final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(ANSWERED_POST);
        databaseReference.child(pushKeyOfPost).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                answeredModelClasses.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    for (DataSnapshot snapshot1:snapshot.getChildren()){
                        AnsweredModelClass answeredModelClass=snapshot1.getValue(AnsweredModelClass.class);
                        answeredModelClasses.add(answeredModelClass);
                    }
                }
                if (answeredModelClasses.size() > 0){
                    alertAnswer.setVisibility(View.GONE);
                    answerAdapter = new AnswerAdapter(Forum.this,answeredModelClasses);
                    AnswerRecyclerView.setAdapter(answerAdapter);
                    popWindow.showAtLocation(v, Gravity.BOTTOM, 0,100);
                }else {
                    alertAnswer.setVisibility(View.VISIBLE);
                    popWindow.showAtLocation(v, Gravity.BOTTOM, 0,100);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Forum.this, "something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /////////// This function Will get Answers related to specific Post //////////////////


    /////////// This function Will set Answers related to specific Post //////////////////

    public  void  setAnsweredPost(String pushKeyOfPost, final Context context, final String answer){
        final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(ANSWERED_POST);
        databaseReference.child(pushKeyOfPost).push().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String,Object> map=new HashMap<>();
                map.put("doctorUid",UserConstantModel.Uid);
                map.put("nameOfDoctor",UserConstantModel.Name);
                map.put("Answer",answer);
                map.put("pushKey",dataSnapshot.getRef().getParent().getKey());
                dataSnapshot.getRef().updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        sendNotificationClass.sendNotification(UserId,"you have new answer by "+UserConstantModel.Name,bundle.getString("postTitle"),Forum.this);
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /////////// This function Will set Answers related to specific Post //////////////////


    /////// This function will set if user liked then text will set to liked and color will be changed/////////////////

    public void CheckIfUserAlreadyLikedAnswer(String pushKey){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(LIKES);
        Log.i("database ref = ",databaseReference.toString());
        databaseReference.child(pushKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot != null) {
                        if (dataSnapshot.hasChild(UserConstantModel.Uid)) {
                            likeButton.setText("Liked");
                            likeButton.setTextColor(Forum.this.getResources().getColor(R.color.LikedColor));
                        }
                    }
                } else {
                    likeButton.setText("Like");
                    likeButton.setTextColor(getResources().getColor(R.color.LikeColor));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Forum.this, "something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /////// This function will set if user liked then text will set to liked and color will be changed/////////////////


    //////////// This function will get the number of Likes of specific Post ////////////////

    public  void GetLikesOfCurrentPost(String pushKey){
        final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(LIKES);
        databaseReference.child(pushKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(Forum.this, "Likes " + dataSnapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Forum.this, "something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //////////// This function will get the Likes of specific Post ////////////////



    //////////// This function will set the Like to specific Post ////////////////////

    public  void setPostLikes(String pushKey){
        final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(LIKES);
        databaseReference.child(pushKey).child(UserConstantModel.Uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null){
                    Like liked =dataSnapshot.getValue(Like.class);
                    if (liked != null) {
                        if (liked.isLiked()) {
                            dataSnapshot.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Forum.this, "Disliked", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }else {
                        HashMap<String,Object> map=new HashMap<>();
                        map.put("Liked",true);
                        dataSnapshot.getRef().updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Forum.this, "Liked", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    //////////// This function will set the Like to specific Post ////////////////////


    /////////// This function Will set Comment related to specific Post //////////////////

    public  void  setCommentPost(String pushKeyOfPost, final Context context, final String comment){
        final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(COMMENTED_POST);
        databaseReference.child(pushKeyOfPost).push().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String,Object> map=new HashMap<>();
                map.put("UserUid",UserConstantModel.Uid);
                map.put("nameOfUser",UserConstantModel.Name);
                map.put("Comment",comment);
                map.put("pushKey",dataSnapshot.getRef().getParent().getKey());

                dataSnapshot.getRef().updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        sendNotificationClass.sendNotification(UserId,UserConstantModel.Name+" commented on your post",bundle.getString("postTitle"),Forum.this);
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /////////// This function Will set Comment related to specific Post //////////////////



    /////////// This function Will get Comments related to specific Post //////////////////

    public  void getCommentedPost(String pushKeyOfPost, final View v){
        final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(COMMENTED_POST);
        databaseReference.child(pushKeyOfPost).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentModelClasses.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    for (DataSnapshot snapshot1:snapshot.getChildren()){
                        CommentModelClass commentModelClass=snapshot1.getValue(CommentModelClass.class);
                        commentModelClasses.add(commentModelClass);
                    }
                }

                if (commentModelClasses.size() > 0){
                    alertComment.setVisibility(View.GONE);
                    commentAdapter = new CommentAdapter(Forum.this,commentModelClasses);
                    CommentRecyclerView.setAdapter(commentAdapter);
                    popWindow.showAtLocation(v, Gravity.BOTTOM, 0,100);
                }else {
                    alertComment.setVisibility(View.VISIBLE);
                    popWindow.showAtLocation(v, Gravity.BOTTOM, 0,100);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Forum.this, "something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /////////// This function Will get Comments related to specific Post //////////////////


}
