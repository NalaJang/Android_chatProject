package com.example.chatproject5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import chat.Constants;

public class MyBodyActivity extends AppCompatActivity {


    private final Handler handler = new Handler();

    private TextView height_edit, weight_edit, foot_edit;
    private TextView shoulder_edit, arm_edit, bust_edit, waist_edit, totalUpperBody_edit,
            hip_edit, thigh_edit, calf_edit, totalLowerBody_edit;

    private String userId_db;
    private String shoulder, arm, bust, waist, totalUpperBody,
            hip, thigh, calf, totalLowerBody, height, weight, foot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_my_body);

        setTitle("내 체형");


        //정보 받기
        Intent intent = getIntent();
        userId_db = intent.getStringExtra("userId_db");


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


        //기존 바디 프로필 내역
        new Thread(new Runnable() {
            @Override
            public void run() {

                final String urlStr = Constants.SERVER_URL + "mybodyList.do";

                myBodyProfile(urlStr);

            }
        }).start();



        //등록 버튼
        Button editButton = findViewById(R.id.edit_button_body);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String urlStr2 = Constants.SERVER_URL + "myBodyUpdate.do";

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        editProfile(urlStr2);

                    }
                }).start();
            }
        });
    }   //end onCreate



    //기존 바디 프로필
    public void myBodyProfile(String urlStr) {

        StringBuilder output = new StringBuilder();

        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if(conn != null) {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);

                OutputStream outputStream = conn.getOutputStream();

                String params = "id=" + userId_db;

                outputStream.write(params.getBytes());


                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;

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
        setMyBodyProfile(output.toString());
    }

    public void setMyBodyProfile(String str) {
        Document doc = Jsoup.parse(str);
        Elements result = doc.select("p.result");
        Elements shoulder_db = doc.select("ol > li.shoulder");
        Elements arm_db = doc.select("ol > li.arm");
        Elements bust_db = doc.select("ol > li.bust");
        Elements waist_db = doc.select("ol > li.waist");
        Elements totalUpperBody_db = doc.select("ol > li.totalUpperBody");
        Elements hip_db = doc.select("ol > li.hip");
        Elements thigh_db = doc.select("ol > li.thigh");
        Elements calf_db = doc.select("ol > li.calf");
        Elements totalLowerBody_db = doc.select("ol > li.totalLowerBody");
        Elements foot_db = doc.select("ol > li.foot");
        Elements height_db = doc.select("ol > li.height");
        Elements weight_db = doc.select("ol > li.weight");
        Elements indate_db = doc.select("ol > li.indate");

        for(int i = 0; i < result.size(); i++) {
            if (result.get(0).text().equals("성공")) {

                shoulder = shoulder_db.text();
                arm = arm_db.text();
                bust = bust_db.text();
                waist = waist_db.text();
                totalUpperBody = totalUpperBody_db.text();
                hip = hip_db.text();
                thigh = thigh_db.text();
                calf = calf_db.text();
                totalLowerBody = totalLowerBody_db.text();
                foot = foot_db.text();
                height = height_db.text();
                weight = weight_db.text();

            } else if (result.get(0).text().equals("실패")) {

            }
            println();
        }
    }


    public void println() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                shoulder_edit.setText(shoulder);
                arm_edit.setText(arm);
                bust_edit.setText(bust);
                waist_edit.setText(waist);
                totalUpperBody_edit.setText(totalUpperBody);
                hip_edit.setText(hip);
                thigh_edit.setText(thigh);
                calf_edit.setText(calf);
                totalLowerBody_edit.setText(totalLowerBody);
                height_edit.setText(height);
                weight_edit.setText(weight);
                foot_edit.setText(foot);
            }
        });
    }


    //수정된 바디 프로필
    public void editProfile(String urlStr) {
        StringBuilder output = new StringBuilder();

        shoulder = shoulder_edit.getText().toString();
        arm = arm_edit.getText().toString();
        bust = bust_edit.getText().toString();
        waist = waist_edit.getText().toString();
        totalUpperBody = totalUpperBody_edit.getText().toString();
        hip = hip_edit.getText().toString();
        thigh = thigh_edit.getText().toString();
        calf = calf_edit.getText().toString();
        totalLowerBody =  totalLowerBody_edit.getText().toString();
        height = height_edit.getText().toString();
        weight = weight_edit.getText().toString();
        foot = foot_edit.getText().toString();


        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if(conn != null) {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);

                OutputStream outputStream = conn.getOutputStream();

                String params = "id=" + userId_db
                                + "&shoulder=" + shoulder
                                + "&arm=" + arm
                                + "&bust=" + bust
                                + "&waist=" + waist
                                + "&totalUpperBody=" + totalUpperBody
                                + "&hip=" + hip
                                + "&thigh=" + thigh
                                + "&calf=" + calf
                                + "&totalLowerBody=" + totalLowerBody
                                + "&height=" + height
                                + "&weight=" + weight
                                + "&foot=" + foot;

                outputStream.write(params.getBytes());


                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;

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
        setEditProfile(output.toString());
    }

    public void setEditProfile(String str) {
        Document doc = Jsoup.parse(str);
//        Elements result = doc.select("p.result");
        Elements shoulder_db = doc.select("p.shoulder");
        Elements arm_db = doc.select("p.arm");
        Elements bust_db = doc.select("p.bust");
        Elements waist_db = doc.select("p.waist");
        Elements totalUpperBody_db = doc.select("p.totalUpperBody");
        Elements hip_db = doc.select("p.hip");
        Elements thigh_db = doc.select("p.thigh");
        Elements calf_db = doc.select("p.calf");
        Elements totalLowerBody_db = doc.select("p.totalLowerBody");
        Elements foot_db = doc.select("p.foot");
        Elements height_db = doc.select("p.height");
        Elements weight_db = doc.select("p.weight");
        Elements indate_db = doc.select("p.indate");

        for(int i = 0, size = shoulder_db.size(); i < size; i++) {

            shoulder = shoulder_db.text();
            arm = arm_db.text();
            bust = bust_db.text();
            waist = waist_db.text();
            totalUpperBody = totalUpperBody_db.text();
            hip = hip_db.text();
            thigh = thigh_db.text();
            calf = calf_db.text();
            totalLowerBody = totalLowerBody_db.text();
            height = height_db.text();
            foot = foot_db.text();
            weight = weight_db.text();

        }
        println2();
    }

    public void println2() {
        handler.post(new Runnable() {
            @Override
            public void run() {

                shoulder_edit.setText(shoulder);
                arm_edit.setText(arm);
                bust_edit.setText(bust);
                waist_edit.setText(waist);
                totalUpperBody_edit.setText(totalUpperBody);
                hip_edit.setText(hip);
                thigh_edit.setText(thigh);
                calf_edit.setText(calf);
                totalLowerBody_edit.setText(totalLowerBody);
                height_edit.setText(height);
                weight_edit.setText(weight);
                foot_edit.setText(foot);

                Toast.makeText(MyBodyActivity.this, "정보가 수정되었습니다.", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {//toolbar 의 back 키 눌렀을 때 동작

            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}