package com.example.chatproject5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import chat.Constants;

/******************************************
                   회원가입
 ******************************************/
public class Sign_upActivity extends AppCompatActivity {


    Intent intent;
    Handler handler = new Handler();

    EditText userName, userId, userPw, userEmail, userPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_sign_up);

        setTitle("회원가입");


        CheckBox check1, check2;
        Button button;

        userName = findViewById(R.id.userName_sign_up);
        userId = findViewById(R.id.userId_sign_up);
        userPw = findViewById(R.id.userPw_sign_up);
        userEmail = findViewById(R.id.userEmail_sign_up);
        userPhone = findViewById(R.id.userPhone_sign_up);

        check1 = findViewById(R.id.check_sign_up);
        check2 = findViewById(R.id.check2_sign_up);
        button = findViewById(R.id.button_sign_up);


//        여기에 선언하니까 버튼 클릭 시 문자들을 읽지 못했다.
//        String name = userName.getText().toString().trim();
//        String id = userId.getText().toString().trim();
//        String pw = userPw.getText().toString().trim();
//        String email = userEmail.getText().toString().trim();
//        String phone = userPhone.getText().toString().trim();



        //가입하기 버튼 클릭
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = userName.getText().toString().trim();
                String id = userId.getText().toString().trim();
                String pw = userPw.getText().toString().trim();
                String email = userEmail.getText().toString().trim();
                String phone = userPhone.getText().toString().trim();

                if(!check1.isChecked() || !check2.isChecked()) {

                    Toast.makeText(Sign_upActivity.this, "약관을 읽어보시고 동의해 주세요.", Toast.LENGTH_SHORT).show();

                } else if(name.equals("")) {

                    Toast.makeText(Sign_upActivity.this, "이름을 입력해 주세요.", Toast.LENGTH_SHORT).show();

                } else if(id.equals("")) {

                    Toast.makeText(Sign_upActivity.this, "아이디를 입력해 주세요.", Toast.LENGTH_SHORT).show();

                } else if(pw.equals("")) {

                    Toast.makeText(Sign_upActivity.this, "비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();

                } else if(email.equals("")) {

                    Toast.makeText(Sign_upActivity.this, "이메일을 입력해 주세요.", Toast.LENGTH_SHORT).show();

                } else if(phone.equals("")) {

                    Toast.makeText(Sign_upActivity.this, "휴대폰번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();

                } else {
                    final String urlStr = Constants.SERVER_URL + "signUp.do";

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            signUp(urlStr);
                        }
                    }).start();
                }
            }
        }); //end button

    }   //end on Create



    public void signUp(String urlStr) {
        StringBuilder output = new StringBuilder();

        String name = userName.getText().toString().trim();
        String id = userId.getText().toString().trim();
        String pw = userPw.getText().toString().trim();
        String email = userEmail.getText().toString().trim();
        String phone = userPhone.getText().toString().trim();

        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection)url .openConnection();

            if(conn != null) {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);

                OutputStream outputStream = conn.getOutputStream();
                String params = "id=" + id
                            + "&name=" + name
                            + "&pw=" + pw
                            + "&email=" + email
                            + "&phone=" + phone;
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

        setDialog();

        intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }


    public void setDialog() {
        handler.post(new Runnable() {
            @Override
            public void run() {

                Toast.makeText(getApplicationContext(), "가입되었습니다.", Toast.LENGTH_SHORT).show();

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