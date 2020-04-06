package com.example.healthcare.Fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.healthcare.Activity.AskQuestion;
import com.example.healthcare.MainActivity;
import com.example.healthcare.Model.UploadPost;
import com.example.healthcare.Model.UserConstantModel;
import com.example.healthcare.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class AskQuestionFragment extends Fragment {


   private View v;
   private Spinner spinner;
   TextInputEditText post_title,post_des;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    public StorageReference storageReference;
    ProgressDialog loadingBar;
    AppCompatImageButton select_post_image;
    Button post_submit;
    ImageView set_post_image;
    Uri imageUri;
    String imageUrl;
    StorageTask uploadTask;
    private String POST="post";
    private String UPLOAD="uploads";

    private static final int IMAGE_REQUEST=1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       v= inflater.inflate(R.layout.fragment_ask_question, container, false);
       spinner=v.findViewById(R.id.categorySpinner);
       /* firebaseUser = FirebaseAuth.getInstance().getCurrentUser();*/
        databaseReference = FirebaseDatabase.getInstance().getReference(POST);

        storageReference= FirebaseStorage.getInstance().getReference(UPLOAD);

        loadingBar=new ProgressDialog(getContext());
        post_title=v.findViewById(R.id.p_title);
        post_des=v.findViewById(R.id.p_des);
        set_post_image=v.findViewById(R.id.select_image_by_user);
        select_post_image=v.findViewById(R.id.camera);
        post_submit=v.findViewById(R.id.submit_post);
        spinnerHelper();
        post_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnUploadPost();
            }
        });
        select_post_image.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               openImage();
           }

       });
       return v;
    }
    public void Uploadimage()
    {
        StorageReference storageReference=FirebaseStorage.getInstance().getReference().child("uploads").child(imageUri.getLastPathSegment());
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri>uriTask=taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlimage=uriTask.getResult();
                imageUrl=urlimage.toString();
                UploadUserPost();
            }

        });
    }

    @Override
    public void onStart() {
        super.onStart();
        post();
    }

    public void post(){
        final DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Post");
        databaseReference.child(MainActivity.CURRENT_CITY).push().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String postname=post_title.getText().toString();
                String postdesc=post_des.getText().toString();
                HashMap<String,Object> map=new HashMap<>();
                map.put("Title","Corona");
                map.put("Category","Dont Know");
                map.put("Description","how to get cure from this");
                map.put("pushKey",dataSnapshot.getRef().getParent().getKey());
                map.put("ImageUri","null");
                map.put("Userid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                map.put("Name", UserConstantModel.Name);
                dataSnapshot.getRef().updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Posted", Toast.LENGTH_SHORT).show();

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void btnUploadPost()
    {
        Uploadimage();
    }
    public void UploadUserPost()
    {

        final DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Post");
        databaseReference.child(MainActivity.CURRENT_CITY).push().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String postname=post_title.getText().toString();
                String postdesc=post_des.getText().toString();
                HashMap<String,Object> map=new HashMap<>();
                map.put("Title",postname);
                map.put("Category","Flu");
                map.put("Description",postdesc);
                map.put("pushKey",dataSnapshot.getRef().getParent().getKey());
                map.put("ImageUri",imageUrl);
                dataSnapshot.getRef().updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Posted", Toast.LENGTH_SHORT).show();

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void openImage()
    {
        Log.i("openImage=","true");
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver =getContext().getContentResolver();
        MimeTypeMap mimeTypeMap =MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data!=null && data.getData() != null){
            imageUri = data.getData();
            set_post_image.setImageURI(imageUri);
            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getContext(), "upload in progress", Toast.LENGTH_SHORT).show();
            }else {
              /*  ChangeProfilePhoto();*/
                Toast.makeText(getContext(),"failed",Toast.LENGTH_LONG).show();
            }
        }
    }



    public void spinnerHelper(){
        List<String> top250 = new ArrayList<String>();
        top250.add("Select Category");
        top250.add("Allergies");
        top250.add("Blood & Immmune System");
        top250.add("Bones,joints");
        top250.add("Cancer");
        top250.add("Chest");
        top250.add("Child health");
        top250.add("Eye");
        top250.add("Allergies");
        top250.add("Blood & Immmune System");
        top250.add("Bones,joints");
        top250.add("Cancer");
        top250.add("Chest");
        top250.add("Child health");
        top250.add("Eye");
        top250.add("Allergies");
        top250.add("Blood & Immmune System");
        top250.add("Bones,joints");
        top250.add("Cancer");
        top250.add("Chest");
        top250.add("Child health");
        top250.add("Eye");
        top250.add("Allergies");
        top250.add("Blood & Immmune System");
        top250.add("Bones,joints");
        top250.add("Cancer");
        top250.add("Chest");
        top250.add("Child health");
        top250.add("Eye");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, top250);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

    }


}
