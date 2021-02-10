package com.example.chatproject5;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

/******************************************
              아이디 비밀번호 찾기
 ******************************************/
public class Search_userInfoActivity extends AppCompatActivity {


    Handler handler = new Handler();


    EditText userName, userId, userPhone, userPhone2;


    final String urlStr = Constants.SERVER_URL + "findId.do";    //아이디
    final String urlStr2 = Constants.SERVER_URL + "findPw.do";   //비밀번호

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("아이디 비밀번호 찾기");


        userName = findViewById(R.id.userName_search);
        userId = findViewById(R.id.userId_search);
        userPhone = findViewById(R.id.userPhone_search);
        userPhone2 = findViewById(R.id.userPhone2_search);

        Button button = findViewById(R.id.button_search);
        Button button2 = findViewById(R.id.button2_search);


        /*****************아이디 찾기******************/
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = userName.getText().toString().trim();
                String phone = userPhone.getText().toString().trim();


                //공란일 경우
                if(name.equals("") || phone.equals("")) {

                    Toast.makeText(Search_userInfoActivity.this, "정보를 입력해 주세요.", Toast.LENGTH_SHORT).show();


                //정보 입력
                } else {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            findId(urlStr);
                        }
                    }).start();
                }
            }
        }); //end button



        /*****************비밀번호 찾기******************/
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = userId.getText().toString();
                String phone = userPhone2.getText().toString();


                //공란일 경우
                if(id.equals("") || phone.equals("")) {

                    Toast.makeText(Search_userInfoActivity.this, "정보를 입력해 주세요.", Toast.LENGTH_SHORT).show();


                //정보 입력
                } else {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            findPw(urlStr2);
                        }
                    }).start();
                }
            }
        }); //end button2
    }   //end onCreate



    //아이디 찾기
    public void findId(String urlStr) {

        String name = userName.getText().toString();
        String phone = userPhone.getText().toString();

        StringBuilder output = new StringBuilder();

        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if(conn != null) {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);

                OutputStream outputStream = conn.getOutputStream();
                String params = "name=" + name + "&phone=" + phone;

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
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        setFindId(output.toString());

        userName.setText("");
        userPhone.setText("");
    }

    //아이디 찾기
    public void setFindId(String str) {
        Document doc = Jsoup.parse(str);
        Elements result = doc.select("p.result");
        Elements findId = doc.select("ol > li.id");

        for(int i = 0; i < result.size(); i++) {
            if(result.get(0).text().equals("성공")) {

                validId(findId.get(0).text());


            } else if(result.get(0).text().equals("실패")){

                invalid();
            }

        }
    }

    //비밀번호 찾기
    public void findPw(String urlStr2) {

        String id = userId.getText().toString();
        String phone = userPhone2.getText().toString();

        StringBuilder output = new StringBuilder();

        try {
            URL url = new URL(urlStr2);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if(conn != null) {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);

                OutputStream outputStream = conn.getOutputStream();
                String params = "id=" + id + "&phone=" + phone;

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
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        setFindPw(output.toString());

        userId.setText("");
        userPhone2.setText("");
    }

    //비밀번호 찾기
    public void setFindPw(String str) {
        Document doc = Jsoup.parse(str);
        Elements result = doc.select("p.result");
        Elements findPw = doc.select("ol > li.pw");

        for(int i = 0; i < result.size(); i++) {
            if(result.get(0).text().equals("성공")) {

                validPw(findPw.get(0).text());


            } else if(result.get(0).text().equals("실패")){

                invalid();
            }


        }
    }

    //정보가 있을 경우
    public void validId(String data) {
        handler.post(new Runnable() {
            @Override
            public void run() {

                AlertDialog.Builder builder = new AlertDialog.Builder(Search_userInfoActivity.this);

                builder.setTitle("정보확인");
                builder.setMessage("회원님의 아이디는 ' " + data + " ' 입니다.");

                builder.setPositiveButton(R.string.goLogin, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Search_userInfoActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    }
                });

                builder.setNegativeButton(R.string.close, null);
                builder.show();
            }


        });
    }

    public void validPw(String data) {
        handler.post(new Runnable() {
            @Override
            public void run() {

                AlertDialog.Builder builder = new AlertDialog.Builder(Search_userInfoActivity.this);

                builder.setTitle("정보확인");
                builder.setMessage("회원님의 비밀번호는 ' " + data + " ' 입니다.");

                builder.setPositiveButton(R.string.goLogin, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Search_userInfoActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    }
                });

                builder.setNegativeButton(R.string.close, null);
                builder.show();
            }
        });
    }

    //정보가 없을 경우
    public void invalid() {
        handler.post(new Runnable() {
            @Override
            public void run() {

                AlertDialog.Builder builder = new AlertDialog.Builder(Search_userInfoActivity.this);

                builder.setTitle(R.string.info);
                builder.setMessage("가입된 정보가 없습니다.");

                builder.setPositiveButton(R.string.close, null);
                builder.show();

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