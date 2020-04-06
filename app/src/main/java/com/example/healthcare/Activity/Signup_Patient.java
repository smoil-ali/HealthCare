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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.healthcare.Model.PatientInfo;
import com.example.healthcare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class Signup_Patient extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private FirebaseDatabase mfirebaseDatabase;
    private DatabaseReference mdatabaseReference;
    private Button patientSignup;
    private EditText etName,etEmail,etPassword;
    private RadioButton rbGender;
    private String user_type="patient";
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        hideStatusbar();

        progressDialog=new ProgressDialog(this);
        etName=findViewById(R.id.p_name);
        etEmail=findViewById(R.id.p_email);
        etPassword=findViewById(R.id.p_pass);
        patientSignup=findViewById(R.id.p_register);
        radioGroup=findViewById(R.id.patient_radiogroup);



        mdatabaseReference=FirebaseDatabase.getInstance().getReference();

        patientSignup.setOnClickListener(this);
    }


    public void hideStatusbar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.p_register:
                signup();
                break;
        }
    }


    public void signup(){

        progressDialog.setTitle("Creating...");
        progressDialog.setMessage("Please wait, while you are sign up...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(true);
        mAuth=FirebaseAuth.getInstance();
        final String email=etEmail.getText().toString();
        final String pass=etPassword.getText().toString();
        final String user_name=etName.getText().toString();

        //check gender
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
        final String gender=radioButton.getText().toString();

        if (email.isEmpty()){
            etEmail.setError("Please Fill Email");
            etEmail.setFocusable(true);
            progressDialog.dismiss();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError("Please Correct Email");
            etEmail.setFocusable(true);
            progressDialog.dismiss();
        }
        else if(pass.isEmpty()){
            etPassword.setError("Please Fill Password");
            etPassword.setFocusable(true);
            progressDialog.dismiss();
        }
        else if(!email.isEmpty() && !pass.isEmpty()){
            mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        progressDialog.dismiss();
                        final String patientUid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                        PatientInfo patientInfo=new PatientInfo(user_name,email,gender,user_type,patientUid,"null");
                        mdatabaseReference.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(patientInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    mdatabaseReference.child("userType").child(patientUid).child("type").setValue(user_type);
                                    Toast.makeText(Signup_Patient.this,"Successful",Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(Signup_Patient.this,"Error",Toast.LENGTH_SHORT);
                                }
                            }
                        });
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(Signup_Patient.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else {
            progressDialog.dismiss();
            Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show();
        }
    }
}