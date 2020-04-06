package com.example.healthcare.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.healthcare.R;

public class signup_options extends AppCompatActivity {
    Button doctor,patient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_options);

        doctor=findViewById(R.id.doctor);
        patient=findViewById(R.id.patiant);

        doctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(signup_options.this,Signup_Doctor.class));
            }
        });

        patient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(signup_options.this,Signup_Patient.class));
            }
        });
    }
}
