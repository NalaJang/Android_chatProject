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

    BodyHelper db;
    String userId_db;
    TextView shoulder_edit, weight_edit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_my_body);

        setTitle("내 체형");

        //정보 받기
        Intent intent = getIntent();
        userId_db = intent.getStringExtra("userId_db");
//        String shoulder_db = intent.getStringExtra("shoulder_db");

        shoulder_edit = findViewById(R.id.shoulder_edit);
        weight_edit = findViewById(R.id.weight_body);

//        shoulder_edit.setText(shoulder_db);


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

        String shoulder = shoulder_edit.getText().toString();
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
                String params = "id=" + userId_db + "&shoulder=" + shoulder + "&weight=" + weight;

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