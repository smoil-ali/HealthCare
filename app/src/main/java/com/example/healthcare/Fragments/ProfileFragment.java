package com.example.healthcare.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.FileUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.healthcare.MainActivity;
import com.example.healthcare.Model.DoctorInfo;
import com.example.healthcare.Model.PatientInfo;
import com.example.healthcare.Model.UserConstantModel;
import com.example.healthcare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {


    private EditText name,email,gender,specification;
    private LinearLayout specificationContainer,genderContainer;
    private String child="";
    private PatientInfo  patientInfo;
    private DoctorInfo doctorInfo;
    private DatabaseReference databaseReference;
    private static final int IMAGE_REQUEST=1;
    private StorageReference storageReference;
    private String Upload="uploads";
    private CircleImageView selectImage;
    private String imageUrl="";
    private StorageTask uploadTask;
    private  Uri uriPath;
    private String UPLOAD="uploads";
    private TextView save;
    private ProgressDialog progressDialog;
    private String uid;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);
        specificationContainer=view.findViewById(R.id.specificationContainer);
        genderContainer=view.findViewById(R.id.gendercontainer);
        acordingUserType();
        name=view.findViewById(R.id.name);
        email=view.findViewById(R.id.email);
        specification=view.findViewById(R.id.specification);
        gender=view.findViewById(R.id.gender);
        uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        progressDialog=new ProgressDialog(getContext());
        databaseReference=FirebaseDatabase.getInstance().getReference();
        storageReference=FirebaseStorage.getInstance().getReference();
        selectImage=view.findViewById(R.id.imageProfile);
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        profileInfo();

        return view;
    }

    public void selectImage(){
        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_REQUEST);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==IMAGE_REQUEST && resultCode==RESULT_OK && data!=null) {

            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(getContext(),ProfileFragment.this);
        }
        Log.i("crop : :","crop wali if");

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            Log.i("crop : :","crop k bd ");
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK) {
                Toast.makeText(getContext(),"resutl ok",Toast.LENGTH_LONG).show();
                progressDialog.setTitle("Updating...");
                progressDialog.setMessage("Please wait, while updating image...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                uriPath = result.getUri();




                File actualImage=new File(uriPath.getPath());
                try {
                    Bitmap compressedImage = new Compressor(getContext())
                            .setMaxWidth(250)
                            .setMaxHeight(250)
                            .setQuality(75)
                            .setCompressFormat(Bitmap.CompressFormat.WEBP)
                            .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES).getAbsolutePath())
                            .compressToBitmap(actualImage);


                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    compressedImage.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    byte[] final_image = baos.toByteArray();
                    Log.i("final image = ",final_image.toString());
                    uploadImage(final_image);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }else if(resultCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error=result.getError();
                Toast.makeText(getContext(),"Error",Toast.LENGTH_LONG).show();
            }

        }else{
            Log.i("crop","else part chal raha hai");
        }
    }

    public void uploadImage(byte[] finalImage)
    {
        Log.i("in fucntion","fucntion me tpak pra ahi ");

        StorageReference imagePath=storageReference.child("profileImage").child(uid).child(uriPath.getLastPathSegment());

        uploadTask=imagePath.putBytes(finalImage);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri>uriTask=taskSnapshot.getStorage().getDownloadUrl();

                while (!uriTask.isComplete());
                Uri urlimage=uriTask.getResult();
                imageUrl=urlimage.toString();

                if ((MainActivity.userType).equals("doctor")) {
                    databaseReference.child("doctor").child(uid).child("imageUrl").setValue(imageUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                databaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String value=dataSnapshot.child("doctor").child(uid).child("imageUrl").getValue(String.class);
                                        UserConstantModel.ImageUri = value;
                                        Glide.with(getContext()).load(value).into(selectImage);
                                        progressDialog.dismiss();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                            else{
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Failuire", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }else if ((MainActivity.userType).equals("patient")){
                    databaseReference.child("user").child(uid).child("imageUrl").setValue(imageUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                databaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String value=dataSnapshot.child("user").child(uid).child("imageUrl").getValue(String.class);
                                        UserConstantModel.ImageUri = value;
                                        Glide.with(getContext()).load(value).into(selectImage);
                                        progressDialog.dismiss();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getContext(), "Failuire", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "hogya...", Toast.LENGTH_SHORT).show();
                            }else{
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Failuire", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }

        });
    }


    public void profileInfo(){
        databaseReference= FirebaseDatabase.getInstance().getReference();
        final String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (MainActivity.userType.equals("patient")){
                    patientInfo=dataSnapshot.child("user").child(uid).getValue(PatientInfo.class);
                    name.setText(patientInfo.getPatientName());
                    email.setText(patientInfo.getPatientEmail());
                    gender.setText(patientInfo.getPatientGender());
                }
                else if (MainActivity.userType.equals("doctor")){
                    doctorInfo=dataSnapshot.child("doctor").child(uid).getValue(DoctorInfo.class);
                    name.setText(doctorInfo.getDoctorName());
                    email.setText(doctorInfo.getDoctorEmail());
                    specification.setText(doctorInfo.getDoctorSpecification());
                    if (!doctorInfo.getImageUrl().isEmpty()){
                        Glide.with(getContext()).load(doctorInfo.getImageUrl()).into(selectImage);
                    }

                }
                acordingUserType();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(),"Error",Toast.LENGTH_LONG).show();
            }
        });


    }

    public void acordingUserType(){
        if ((MainActivity.userType).equals("doctor")) {
            specificationContainer.setVisibility(View.VISIBLE);


        }else if ((MainActivity.userType).equals("patient")){
            genderContainer.setVisibility(View.VISIBLE);
        }
    }

}