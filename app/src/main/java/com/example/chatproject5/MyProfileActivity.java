package com.example.chatproject5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class MyProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_profile);

        ImageView profilePhoto = findViewById(R.id.image_profile);
        TextView userId = findViewById(R.id.userId_profile);
        TextView content = findViewById(R.id.content_profile);


        Intent intent = getIntent();
        String userId_db = intent.getStringExtra("userId_db");
        String userContent_db = intent.getStringExtra("userContent_db");

        userId.setText(userId_db);
        content.setText(userContent_db);


    }

    public void close_profile(View v) {
        finish();
    }

}