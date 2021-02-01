package com.example.chatproject5;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import adapter.AddressListAdapter;
import dto.AddressDto;

public class MyInfoActivity extends AppCompatActivity {

    private Intent intent;
    private Handler handler = new Handler();

    private String userId_db, userPw_db;
    private String userId, userPw, userContent, userName, userEmail, userPhone;
    private EditText userContent_edit, userName_edit, userId_edit, userPw_edit, userEmail_edit, userPhone_edit;

    private RecyclerView recyclerView;
    private AddressListAdapter adapter;
    private ArrayList<AddressDto> addressList = new ArrayList<>();

//    AddAddressFragment addressFragment;

//    static final int REQUEST_CODE_UPDATE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_my_info);

        setTitle("내 정보");

        //정보 받기
        intent = getIntent();
        userId_db = intent.getStringExtra("userId_db");
//        String userContent_db = intent.getStringExtra("userContent_db");
//        String userName_db = intent.getStringExtra("userName_db");
        userPw_db = intent.getStringExtra("userPw_db");
//        String userEmail_db = intent.getStringExtra("userEmail_db");
//        String userPhone_db = intent.getStringExtra("userPhone_db");


        userContent_edit = findViewById(R.id.userContent_info);
        userName_edit = findViewById(R.id.userName_info);
        userId_edit = findViewById(R.id.userId_info);
        userPw_edit = findViewById(R.id.userPw_info);
        userEmail_edit = findViewById(R.id.userEmail_info);
        userPhone_edit = findViewById(R.id.userPhone_info);

//        userContent.setText(userContent_db);
//        userName.setText(userName_db);
//        userId.setText(userId_db);
//        userPw.setText(userPw_db);
//        userEmail.setText(userEmail_db);
//        userPhone.setText(userPhone_db);

        //기존 사용자 정보
        new Thread(new Runnable() {
            @Override
            public void run() {

                final String urlStr2 = "http://192.168.0.17:8080/webapp/webServer/login.do";

                myInfo(urlStr2);

            }
        }).start();



        /***************** 정보 수정 버튼 *****************/
        Button edit_button = findViewById(R.id.edit_button);
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String password = userPw_edit.getText().toString().trim();
                String email = userEmail_edit.getText().toString().trim();

                if(!password.isEmpty() || !email.isEmpty()) {


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String urlStr = "http://192.168.0.17:8080/webapp/webServer/userInfoUpdate.do";

                            update(urlStr);


                        }
                    }).start();

                } else {
                    Toast.makeText(getApplicationContext(), "모두 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }

//                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);


            }
        }); //end editButton onClick


        /***************** 배송지 목록 *****************/
        recyclerView = findViewById(R.id.recyclerView_address);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        adapter = new AddressListAdapter(addressList, getApplicationContext(), userId_db);
        recyclerView.setAdapter(adapter);


        //DB 에서 배송지 목록가져오기
        final String urlStr = "http://192.168.0.17:8080/webapp/webServer/addressList.do";

        new Thread(new Runnable() {
            @Override
            public void run() {

                myAddressList(urlStr);

            }
        }).start();



        /***************** 배송지 추가 버튼 *****************/
        Button addAddress_button = findViewById(R.id.addAddress_button);
        addAddress_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //getSupportFragmentManager().beginTransaction().replace(R.id.container, addressFragment).commit();
                intent = new Intent(getApplicationContext(), AddressActivity.class);
                startActivity(intent);
            }
        });
    }   //end onCreate



    //기존 정보
    public void myInfo(String urlStr) {
        StringBuilder output = new StringBuilder();

//        String id = userId.getText().toString();
//        String pw = userPw.getText().toString();


        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if(conn != null) {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);

                OutputStream outputStream = conn.getOutputStream();

                //값 넣어주기
                String params = "id=" + userId_db + "&pw=" + userPw_db;

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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        setUpdatedMyInfo(output.toString());    //잘라줄 값
        System.out.println("기존 정보 : " + output.toString());

    }

    public void setUpdatedMyInfo(String str) {

        Document doc = Jsoup.parse(str);
        Elements result = doc.select("p.result");
        Elements id_db = doc.select("ol > li.id");
        Elements userName_db = doc.select("ol > li.name");
        Elements userPw_db = doc.select("ol > li.pw");
        Elements userEmail_db = doc.select("ol > li.email");
        Elements userPhone_db = doc.select("ol > li.phone");
        Elements userContent_db = doc.select("ol > li.content");
        Elements userProfilePhoto_db = doc.select("ol > li.profilePhoto");


        for(int i = 0; i < result.size(); i++) {

            if(result.get(0).text().equals("로그인 성공")) {

//                userId = id_db.text();
                userContent = userContent_db.text();
                userName = userName_db.text();
//                userPw = userPw_db.text();
                userEmail = userEmail_db.text();
                userPhone = userPhone_db.text();
            }
        }
        println3();
    }
    public void println3() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                //userContent_edit, userName_edit, userId_edit, userPw_edit, userEmail_edit, userPhone_edit
                userId_edit.setText(userId_db);
                userContent_edit.setText(userContent);
                userName_edit.setText(userName);
                userPw_edit.setText(userPw_db);
                userEmail_edit.setText(userEmail);
                userPhone_edit.setText(userPhone);
            }
        });
    }


    //DB 회원 정보 업데이트
    public void update(String urlStr) {
        StringBuilder output = new StringBuilder();

        userPw = userPw_edit.getText().toString();
        userContent = userContent_edit.getText().toString();
        userEmail = userEmail_edit.getText().toString();
        userPhone = userPhone_edit.getText().toString();


        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if(conn != null) {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);

                OutputStream outputStream = conn.getOutputStream();

                //값 넣어주기
                String params = "id=" + userId_db + "&content=" + userContent + "&pw=" + userPw + "&email=" + userEmail + "&phone=" + userPhone;
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
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "다시 시도해주세요", Toast.LENGTH_SHORT).show();

        }

        setUpdate(output.toString());
        System.out.println(output.toString());

    }

    public void setUpdate(String outputStr) {
        Document doc = Jsoup.parse(outputStr);
        Elements result = doc.select("p.result");
        Elements userContent_db = doc.select("ol > li.content");
        Elements userPw_db = doc.select("ol > li.pw");
        Elements userEmail_db = doc.select("ol > li.email");
        Elements userPhone_db = doc.select("ol > li.phone");

        for(int i = 0; i < result.size(); i++) {
            if(result.get(0).text().equals("수정된정보")) {

                userContent = userContent_db.text();
                userPw = userPw_db.text();
                userEmail = userEmail_db.text();
                userPhone = userPhone_db.text();

            }
        }

        println();

    }   //end setUpdate


    //수정 성공시 실행 메소드
    public  void println() {
        handler.post(new Runnable() {
            @Override
            public void run() {

                AlertDialog.Builder builder = new AlertDialog.Builder(MyInfoActivity.this);

                builder.setTitle(R.string.info);
                builder.setMessage("수정이 완료되었습니다.");
                builder.setPositiveButton(R.string.close, null);
                builder.show();

                userContent_edit.setText(userContent);
                userPw_edit.setText(userPw);
                userEmail_edit.setText(userEmail);
                userPhone_edit.setText(userPhone);
            }
        });
    }


    //DB 배송지 목록
    public void myAddressList(String urlStr) {
        StringBuilder output = new StringBuilder();

        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if(conn != null) {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);

                OutputStream outputStream = conn.getOutputStream();

                //값 넣어주기
                String params = "id=" + userId_db;

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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        setAddressList(output.toString());
    }

    //DB 에서 가져온 배송지 목록
    public void setAddressList(String str) {
        Document doc = Jsoup.parse(str);
        Elements nickName_db = doc.select("ol > li.nickName");
        Elements userName_db = doc.select("ol > li.userName");
        Elements id_db = doc.select("ol > li.id");
        Elements phone_db = doc.select("ol > li.phone");
        Elements zip_num_db = doc.select("ol > li.zip_num");
        Elements address1_db = doc.select("ol > li.address1");
        Elements address2_db = doc.select("ol > li.address2");
        Elements result_db = doc.select("ol > li.result");

        for(int i = 0, size = id_db.size(); i < size; i++) {

            AddressDto addressDto = new AddressDto().setNickName(nickName_db.get(i).text())
                                                    .setUserName(userName_db.get(i).text())
                                                    .setId(id_db.get(i).text())
                                                    .setPhone(phone_db.get(i).text())
                                                    .setZip_num(zip_num_db.get(i).text())
                                                    .setAddress1(address1_db.get(i).text())
                                                    .setAddress2(address2_db.get(i).text())
                                                    .setResult(result_db.get(i).text());

            adapter.addItem(addressDto);
        }

        println2();
    }

    public void println2() {
        handler.post(new Runnable() {
            @Override
            public void run() {

                recyclerView.setAdapter(adapter);

            }
        });
    }


    //뒤로 가기
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
