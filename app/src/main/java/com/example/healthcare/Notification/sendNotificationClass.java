package com.example.healthcare.Notification;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;



import com.example.healthcare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.healthcare.Activity.Forum.apiService;

public class sendNotificationClass {



    public static FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

    public static void sendNotification(final String receiver1, final String msg, final String title , final Context context){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver1);
        query.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                 Token token = snapshot.getValue(Token.class);

                 Data data = null;
                 if (!msg.equals("")) {
                     data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, msg, title, receiver1);
                 }


                 assert token != null;
                 Sender sender = new Sender(data, token.getToken());
                 apiService.sendNotification(sender)
                         .enqueue(new Callback<MyResponse>() {
                             @Override
                             public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                 if (response.code() == 200) {
                                     if (response.body().success != 1)
                                         Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show();
                                 } else {
                                     Toast.makeText(context, "response is not workinig...", Toast.LENGTH_SHORT).show();
                                 }
                             }

                             @Override
                             public void onFailure(Call<MyResponse> call, Throwable t) {
                                 Toast.makeText(context, "failure", Toast.LENGTH_SHORT).show();
                             }
                         });
             }
         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {
             Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show();
         }
     });
    }

}
