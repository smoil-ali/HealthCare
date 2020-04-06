package com.example.healthcare.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthcare.MainActivity;
import com.example.healthcare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private TextView user_signup,etEmail,etPass;
    private Button user_login;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser!=null){
            startActivity(new Intent(Login.this,MainActivity.class));
            finish();
        }
        hideStatusBar();
        progressDialog=new ProgressDialog(this);

        user_signup=findViewById(R.id.u_sign_up_txt);
        user_login=findViewById(R.id.u_login);
        etEmail=findViewById(R.id.u_email);
        etPass=findViewById(R.id.u_password);


        user_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,signup_options.class));
            }
        });

        user_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               signIn();
            }
        });

    }

    public void hideStatusBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }


    public void signIn(){

        progressDialog.setTitle("Logging...");
        progressDialog.setMessage("Please wait, while you are logging...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(true);

        mAuth= FirebaseAuth.getInstance();
        String email=etEmail.getText().toString();
        String pass=etPass.getText().toString();


        if(email.isEmpty()){
            etEmail.setError("Please Fill Email");
            etEmail.setFocusable(true);
            progressDialog.dismiss();
        }
        else if (pass.isEmpty()){
            etPass.setError("Please Fill Password");
            etPass.setFocusable(true);
            progressDialog.dismiss();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError("Please Fill Email");
            etEmail.setFocusable(true);
            progressDialog.dismiss();
        }
        else if(!email.isEmpty() && !pass.isEmpty()){
            mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        progressDialog.dismiss();
                        Intent intent =new Intent(Login.this,MainActivity.class);
                        startActivity(intent);
                        finish();

                    }
                    else{
                        Toast.makeText(Login.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }else {
            Toast.makeText(Login.this,"Error",Toast.LENGTH_SHORT).show();
        }
    }

}
