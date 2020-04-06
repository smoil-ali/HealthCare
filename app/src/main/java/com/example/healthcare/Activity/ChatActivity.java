package com.example.healthcare.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import com.bumptech.glide.Glide;
/*import com.example.healthcare.APIService;*/
import com.example.healthcare.Adapter.MessageAdapter;
import com.example.healthcare.Interface.APIService;
import com.example.healthcare.Model.ChatModel;
/*import com.example.healthcare.Model.UserModel;
import com.example.healthcare.Notification.Client;
import com.example.healthcare.Notification.Data;
import com.example.healthcare.Notification.MyResponse;
import com.example.healthcare.Notification.Sender;
import com.example.healthcare.Notification.Token;
import com.example.healthcare.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;*/
import com.example.healthcare.Model.UserModel;
import com.example.healthcare.Notification.Client;
import com.example.healthcare.Notification.Data;
import com.example.healthcare.Notification.MyResponse;
import com.example.healthcare.Notification.Sender;
import com.example.healthcare.Notification.Token;
import com.example.healthcare.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
/*import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatActivity extends AppCompatActivity {

    EditText sendingMessage;
    ImageButton sent;
    String receiver;
    Bundle bundle;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    TextView username;
    MessageAdapter messageAdapter;
    List<ChatModel> chatlist;
    RecyclerView messageRecyclerview;
    CircleImageView profile_image;
    private static final int IMAGE_REQUEST = 99;
    public StorageReference storageReference;
    ProgressDialog loadingBar;
    Uri imageUri;
    StorageTask uploadTask;

    ImageView sendImage;
    ValueEventListener seenListener;
    APIService apiService;
    boolean notify = false;
    private Toolbar toolbar;
    AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        bundle=getIntent().getExtras();
        assert bundle != null;




        toolbar=findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.chatmenu);



       toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
           @Override
           public boolean onMenuItemClick(MenuItem item) {
               int id=item.getItemId();
               switch (id){
                   case R.id.clear_chat:
                       ClearChat();
                       break;
                   case R.id.viewprofile:
                       Toast.makeText(ChatActivity.this, "profile", Toast.LENGTH_SHORT).show();
                       break;
               }
               return false;
           }
       });

        receiver=bundle.getString("key");
        username=findViewById(R.id.user_name);
        sent=findViewById(R.id.btnSend);
        sendingMessage=findViewById(R.id.textSend);
        messageRecyclerview=findViewById(R.id.chat_recyclerview);
        sendImage=findViewById(R.id.btnImg);
        messageRecyclerview.hasFixedSize();
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageRecyclerview.setLayoutManager(linearLayoutManager);

        ///For making url for Firebase Messaging
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);



        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ChatActivity.this, "profile of chatter", Toast.LENGTH_SHORT).show();
            }
        });


        storageReference= FirebaseStorage.getInstance().getReference("uploads");
        loadingBar=new ProgressDialog(this);

        profile_image=findViewById(R.id.profile_image);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        ///////// Sending Image to Receiver/////////////

        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });


        ////// Sending Message To Specific User by calling SendMessage() function

        sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify=true;
                String msg=sendingMessage.getText().toString();
                if (!msg.equals("")){
                    SendMessage(receiver,firebaseUser.getUid(),msg,"null");  ///// variable receiver has unique id of receiver
                }else {
                    Toast.makeText(ChatActivity.this, "Cannot send empty message", Toast.LENGTH_SHORT).show();
                }
                sendingMessage.setText("");
            }
        });

        ///// Here we are getting the receiver profile for showing the name and profile photo in Toolbar
        ///// And also calling ReadMessages() function to read all messages between sender and receiver

        databaseReference= FirebaseDatabase.getInstance().getReference("doctor").child(receiver);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                UserModel userModel=dataSnapshot.getValue(UserModel.class);
                assert userModel != null;
                username.setText(userModel.getName());
                if (userModel.getImageUri().equals("default")){
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                }else {
                    Glide.with(getApplicationContext()).load(userModel.getImageUri()).placeholder(R.mipmap.ic_launcher).into(profile_image);
                }
                ReadMessages(receiver,firebaseUser.getUid(),userModel.getImageUri());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //// Here we are calling the seenMessage() function to check if message is seen by receiver

         //// receiver==receiver unique id
    }


    private void ClearChat() {
        final DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Chat");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    ChatModel chatModel=snapshot.getValue(ChatModel.class);
                    if (chatModel.getSender().equals(firebaseUser.getUid()) && chatModel.getReceiver().equals(receiver) ||
                                chatModel.getSender().equals(receiver) && chatModel.getReceiver().equals(firebaseUser.getUid())){
                        snapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(ChatActivity.this, "removed", Toast.LENGTH_SHORT).show();
                                }
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



    private void openImage() {

        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data!=null && data.getData() != null){
            imageUri = data.getData();
            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(this, "upload in progress", Toast.LENGTH_SHORT).show();
            }else {
                uploadImage();
            }
        }
    }

    ///////////////////////////Logic Explain of seenMessage() here/////////////////
    //// if sender id == current user id && receiver id == receiver unique id then message has been seen!!!!
    //// By updating isseen == true
//    private void seenMessage(final String userId){
//
//        databaseReference=FirebaseDatabase.getInstance().getReference("Chat");
//        seenListener=databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
//                    ChatModel chatModel=snapshot.getValue(ChatModel.class);
//                    assert chatModel != null;
//                    if (chatModel.getSender().equals(firebaseUser.getUid()) && chatModel.getReceiver().equals(userId)){
//                        HashMap<String,Object> hashMap = new HashMap<>();
//                        hashMap.put("isseen",true);
//                        snapshot.getRef().updateChildren(hashMap);
//                    }
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }


    ////Here we are sending message to receiver
    ////And also we are sending the notification to message receiver
    private void SendMessage(final String receiver, String sender, String message, String ImageUrl){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
        ChatModel chatModel=new ChatModel(receiver,sender,message,false,ImageUrl);
        databaseReference.child("Chat").push().setValue(chatModel);

        final DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference("ChatList")
                .child(sender)
                .child(receiver);

        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    databaseReference1.child("id").setValue(receiver);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        final String msg=message;
        databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel=dataSnapshot.getValue(UserModel.class);
                if (notify) {
                    assert userModel != null;
                    sendNotification(receiver, userModel.getName(), msg);
                }
                notify=false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //////This is the code for uploading and storing image in database////////////////

        private String getFileExtension(Uri uri){
        ContentResolver contentResolver =getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap =MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){

        loadingBar.setMessage("uploading....");
        loadingBar.show();

        if (imageUri != null){
            final StorageReference storageReference1=storageReference.child(System.currentTimeMillis()
                   +"."+getFileExtension(imageUri));
            uploadTask = storageReference1.putFile(imageUri);




            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) throw task.getException();
                    return storageReference1.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {

                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();
                        databaseReference=FirebaseDatabase.getInstance().getReference();
                        loadingBar.dismiss();
                        SendMessage(receiver,firebaseUser.getUid(),"",mUri);
                    }else {
                        Toast.makeText(ChatActivity.this, "failed", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ChatActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            });
        }
    }


    /////////////This is the code that sending Notification to receiver
    private void sendNotification(final String receiver1, final String name, final String msg) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
            Query query = tokens.orderByKey().equalTo(receiver1);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                        Token token = snapshot.getValue(Token.class);
                        Data data = null;
                        if (!msg.equals("")) {
                            data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, name + ": " + msg, "New Message", receiver);
                        } else {
                            data = new Data(firebaseUser.getUid(),R.mipmap.ic_launcher,name+": "+"You received new image","Image",receiver);
                        }


                        assert token != null;
                        Sender sender = new Sender(data, token.getToken());
                        apiService.sendNotification(sender)
                                .enqueue(new Callback<MyResponse>() {
                                    @Override
                                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                        if (response.code() == 200) {
                                            if (response.body().success != 1)
                                                Toast.makeText(ChatActivity.this, "failed", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ChatActivity.this, "response is not workinig...", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<MyResponse> call, Throwable t) {
                                        Toast.makeText(ChatActivity.this, "failure", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ChatActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
    }

    ///////This is the code that read message of receiver and sender
    private void ReadMessages(final String receiver, final String sender, final String Receiver_image_url){


        chatlist=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Chat");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatlist.clear();
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    ChatModel chatModel=dataSnapshot1.getValue(ChatModel.class);
                    assert chatModel != null;
                    if ((chatModel.getReceiver().equals(receiver) && chatModel.getSender().equals(sender)) ||
                            (chatModel.getReceiver().equals(sender) && chatModel.getSender().equals(receiver))){
                        chatlist.add(chatModel);
                    }
                }
                messageAdapter=new MessageAdapter(ChatActivity.this,chatlist,Receiver_image_url);
                messageRecyclerview.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //////// This is the code that is updating user status online or offline
    public void status(String status){

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        HashMap<String, Object> map= new HashMap<>();
        map.put("status",status);
        databaseReference.updateChildren(map);
    }


    //////// when activity resume or start status is online
    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        currentUser(receiver);

    }

    /////// status is offline when activity close or change
    ////// seenlistener is remove that detects whether message is seen by receiver or not
    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
        currentUser("none");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //////// Here we are making logic that disable notification while we are chatting

    private void currentUser(String userId){
        SharedPreferences.Editor editor=getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentUser",userId);
        editor.apply();
    }

}
