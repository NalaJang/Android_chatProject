package com.example.chatproject5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import database.BodyHelper;
import dto.BodyDto;

public class MyBodyActivity extends AppCompatActivity {

    final String urlStr = "http://192.168.0.17:8080/webapp/webServer/mybodyInsert.do";

    private BodyHelper db;

    private TextView height_edit, weight_edit, foot_edit;
    private TextView shoulder_edit, arm_edit, bust_edit, waist_edit, totalUpperBody_edit,
            hip_edit, thigh_edit, calf_edit, totalLowerBody_edit;

    private String userId_db;
    private String shoulder_db, arm_db, bust_db, waist_db, totalUpperBody_db,
            hip_db, thigh_db, calf_db, totalLowerBody_db, height_db, weight_db, foot_db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_my_body);

        setTitle("내 체형");

        //정보 받기
        Intent intent = getIntent();
        userId_db = intent.getStringExtra("userId_db");
        shoulder_db = intent.getStringExtra("shoulder_db");
        arm_db = intent.getStringExtra("arm_db");
        bust_db = intent.getStringExtra("bust_db");
        waist_db = intent.getStringExtra("waist_db");
        totalUpperBody_db = intent.getStringExtra("totalUpperBody_db");
        hip_db = intent.getStringExtra("hip_db");
        thigh_db = intent.getStringExtra("thigh_db");
        calf_db = intent.getStringExtra("calf_db");
        totalLowerBody_db = intent.getStringExtra("totalLowerBody_db");
        height_db = intent.getStringExtra("height_db");
        weight_db = intent.getStringExtra("weight_db");
        foot_db = intent.getStringExtra("foot_db");

        System.out.println("MYBODY, SHOULDER => " + shoulder_db);

        shoulder_edit = findViewById(R.id.shoulder_edit);
        arm_edit = findViewById(R.id.arm_edit);
        bust_edit = findViewById(R.id.bust_edit);
        waist_edit = findViewById(R.id.waist_edit);
        totalUpperBody_edit = findViewById(R.id.totalUpperBody_edit);
        hip_edit = findViewById(R.id.hip_edit);
        thigh_edit = findViewById(R.id.thigh_edit);
        calf_edit = findViewById(R.id.calf_edit);
        totalLowerBody_edit = findViewById(R.id.totalLowerBody_edit);
        height_edit = findViewById(R.id.height_body);
        weight_edit = findViewById(R.id.weight_body);
        foot_edit = findViewById(R.id.foot_body);


        shoulder_edit.setText(shoulder_db);
        arm_edit.setText(arm_db);
        bust_edit.setText(bust_db);
        waist_edit.setText(waist_db);
        totalUpperBody_edit.setText(totalUpperBody_db);
        hip_edit.setText(hip_db);
        thigh_edit.setText(thigh_db);
        calf_edit.setText(calf_db);
        totalLowerBody_edit.setText(totalLowerBody_db);
        height_edit.setText(height_db);
        weight_edit.setText(weight_db);
        foot_edit.setText(foot_db);


        //등록 버튼
        Button editButton = findViewById(R.id.edit_button_body);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        editProfile(urlStr);

                    }
                }).start();


            }
        });
    }   //end onCreate

    public void editProfile(String urlStr) {
        StringBuilder output = new StringBuilder();

        shoulder_db = shoulder_edit.getText().toString();
        String weight = weight_edit.getText().toString();


        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if(conn != null) {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);

                OutputStream outputStream = conn.getOutputStream();

                //값 넣어주기
                String params = "id=" + userId_db + "&shoulder=" + shoulder_db + "&weight=" + weight;

                outputStream.write(params.getBytes());


                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = null;

                while(true) {
                    line = reader.readLine();

                    if(line == null) {
                        break;
                    }

                    output.append(line + "\n");

                }
                reader.close();
                conn.disconnect();
            } else {

                Log.i("통신 결과", conn.getResponseCode()+"에러");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("====", output.toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar 의 back 키 눌렀을 때 동작

                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}