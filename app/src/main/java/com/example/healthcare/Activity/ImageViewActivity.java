package com.example.healthcare.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.healthcare.R;

public class ImageViewActivity extends AppCompatActivity {

    ImageView openImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Image");
        Window window = getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(ContextCompat.getColor(this,R.color.ThemeColor));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
        openImage = findViewById(R.id.OpenImageView);
        Bundle bundle=getIntent().getExtras();
        assert bundle != null;
        Glide.with(this).load(bundle.getString("imagekey")).placeholder(R.mipmap.ic_launcher).into(openImage);
    }
}
