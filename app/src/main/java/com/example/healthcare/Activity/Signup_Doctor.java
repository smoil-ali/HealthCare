package com.example.healthcare.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.healthcare.Model.DoctorInfo;
import com.example.healthcare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup_Doctor extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private Button doctorSignUp;
    private EditText etName,etEmail,etPassword,etReg,etSpecification;
    private String user_type="doctor";
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup__doctor);
        hideStatusBar();

        etName=findViewById(R.id.doctorName);
        etEmail=findViewById(R.id.doctorEmail);
        etPassword=findViewById(R.id.doctorPassword);
        etReg=findViewById(R.id.reg_id);
        etSpecification=findViewById(R.id.specification);
        doctorSignUp=findViewById(R.id.doctorSignup);

        progressDialog=new ProgressDialog(this);
        databaseReference=FirebaseDatabase.getInstance().getReference();

        doctorSignUp.setOnClickListener(this);
    }


    public void hideStatusBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.doctorSignup:
                signUp();
                break;
        }
    }


    public void signUp(){

        progressDialog.setTitle("Creating...");
        progressDialog.setMessage("Please wait, while you are sign up...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(true);
        mAuth=FirebaseAuth.getInstance();

        final String name=etName.getText().toString();
        final String email=etEmail.getText().toString();
        final String password=etPassword.getText().toString();
        final String regId=etReg.getText().toString();
        final String spcification=etSpecification.getText().toString();


        if(email.isEmpty()){
            etEmail.setError("Please Fill Email");
            etEmail.setFocusable(true);
            progressDialog.dismiss();
        }
        else if(password.isEmpty()){
            etPassword.setError("Please Fill Password");
            etPassword.setFocusable(true);
            progressDialog.dismiss();
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError("Please Correct Email");
            etEmail.setFocusable(true);
            progressDialog.dismiss();
        }
        else if(regId.isEmpty()){
            etReg.setError("Please Fill Regestration number");
            etReg.setFocusable(true);
            progressDialog.dismiss();
        }
        else if (name.isEmpty()){
            etName.setError("Please Fill Name");
        }
        else if(!email.isEmpty() && !password.isEmpty()){
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        progressDialog.dismiss();
                        final String doctorUid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DoctorInfo doctorInfo=new DoctorInfo(name,email,spcification,user_type,regId,doctorUid,"null");
                        databaseReference.child("doctor").child(doctorUid)
                                .setValue(doctorInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    databaseReference.child("userType").child(doctorUid).child("type").setValue(user_type);
                                    Toast.makeText(Signup_Doctor.this,"Successful",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(Signup_Doctor.this,"Error",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(Signup_Doctor.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            progressDialog.dismiss();
            Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show();
        }
    }
}