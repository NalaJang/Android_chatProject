package com.example.chatproject5;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.ArrayList;

import adapter.AddressListAdapter;
import chat.Constants;
import dto.AddressDto;

public class MyInfoActivity extends AppCompatActivity {

    public static final int EDIT_REQUEST_CODE = 1;

    private Intent intent;
    private final Handler handler = new Handler();

    private String userId_db, userPw_db;
    private String userPw, userContent, userName, userEmail, userPhone;
    private EditText userContent_edit, userName_edit, userId_edit, userPw_edit, userEmail_edit, userPhone_edit;

    private RecyclerView recyclerView;
    private AddressListAdapter adapter;
    private final ArrayList<AddressDto> addressList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_my_info);

        setTitle("내 정보");

        //정보 받기
        intent = getIntent();
        userId_db = intent.getStringExtra("userId_db");
        userPw_db = intent.getStringExtra("userPw_db");


        userContent_edit = findViewById(R.id.userContent_info);
        userName_edit = findViewById(R.id.userName_info);
        userId_edit = findViewById(R.id.userId_info);
        userPw_edit = findViewById(R.id.userPw_info);
        userEmail_edit = findViewById(R.id.userEmail_info);
        userPhone_edit = findViewById(R.id.userPhone_info);


        //기존 사용자 정보
        new Thread(new Runnable() {
            @Override
            public void run() {

                final String urlStr2 = Constants.SERVER_URL + "login.do";

                myInfo(urlStr2);

            }
        }).start();


        /* **************** 정보 수정 버튼 *****************/
        Button edit_button = findViewById(R.id.edit_button);
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String password = userPw_edit.getText().toString().trim();
                String email = userEmail_edit.getText().toString().trim();

                if(password.equals("") || email.equals("")) {

                    Toast.makeText(getApplicationContext(), "모두 입력해 주세요.", Toast.LENGTH_SHORT).show();

                } else {

                    new Thread(() -> {
                        final String updateUrlStr = Constants.SERVER_URL + "userInfoUpdate.do";

                        update(updateUrlStr);

                    }).start();

                }
            }
        }); //end editButton onClick


        /* **************** 배송지 목록 *****************/
        recyclerView = findViewById(R.id.recyclerView_address);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        adapter = new AddressListAdapter(addressList, MyInfoActivity.this, userId_db);
        recyclerView.setAdapter(adapter);


        //DB 에서 배송지 목록가져오기
        final String urlStr = Constants.SERVER_URL + "addressList.do";

        new Thread(() -> myAddressList(urlStr)).start();



        /* **************** 배송지 추가 버튼 *****************/
        Button addAddress_button = findViewById(R.id.addAddress_button);
        addAddress_button.setOnClickListener(v -> {

            intent = new Intent(getApplicationContext(), AddressActivity.class);
            intent.putExtra("userId_db", userId_db);
//                startActivity(intent);
            startActivityForResult(intent, EDIT_REQUEST_CODE);
        });
    }   //end onCreate



    //기존 정보
    public void myInfo(String urlStr) {
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
                String params = "id=" + userId_db + "&pw=" + userPw_db;

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

        setUpdatedMyInfo(output.toString());    //잘라줄 값

    }

    public void setUpdatedMyInfo(String str) {

        Document doc = Jsoup.parse(str);
        Elements result = doc.select("p.result");
        Elements userName_db = doc.select("ol > li.name");
        Elements userEmail_db = doc.select("ol > li.email");
        Elements userPhone_db = doc.select("ol > li.phone");
        Elements userContent_db = doc.select("ol > li.content");
        Elements userProfilePhoto_db = doc.select("ol > li.profilePhoto");


        for(int i = 0; i < result.size(); i++) {

            if(result.get(0).text().equals("로그인 성공")) {

                userContent = userContent_db.text();
                userName = userName_db.text();
                userEmail = userEmail_db.text();
                userPhone = userPhone_db.text();
            }
        }
        println3();
    }
    public void println3() {
        handler.post(() -> {

            userId_edit.setText(userId_db);
            userContent_edit.setText(userContent);
            userName_edit.setText(userName);
            userPw_edit.setText(userPw_db);
            userEmail_edit.setText(userEmail);
            userPhone_edit.setText(userPhone);
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
            //추가
//            FileInputStream fileInputStream = new FileInputStream(img_path);


            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if(conn != null) {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);

                OutputStream outputStream = conn.getOutputStream();

                //값 넣어주기
                String params = "id=" + userId_db
                            + "&content=" + userContent
                            + "&pw=" + userPw
                            + "&email=" + userEmail
                            + "&phone=" + userPhone;
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

        setUpdate(output.toString());

    }

    //update ->
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
        handler.post(() -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(MyInfoActivity.this);

            builder.setTitle(R.string.info);
            builder.setMessage("수정이 완료되었습니다.");
            builder.setPositiveButton(R.string.close, null);
            builder.show();

            userContent_edit.setText(userContent);
            userPw_edit.setText(userPw);
            userEmail_edit.setText(userEmail);
            userPhone_edit.setText(userPhone);
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

        setAddressList(output.toString());
    }

    //DB 에서 가져온 배송지 목록(myAddressList)
    public void setAddressList(String str) {
        Document doc = Jsoup.parse(str);
        Elements no_db = doc.select("ol > li.no");
        Elements nickName_db = doc.select("ol > li.nickName");
        Elements userName_db = doc.select("ol > li.userName");
        Elements id_db = doc.select("ol > li.id");
        Elements phone_db = doc.select("ol > li.phone");
        Elements zip_num_db = doc.select("ol > li.zip_num");
        Elements address1_db = doc.select("ol > li.address1");
        Elements address2_db = doc.select("ol > li.address2");
        Elements result_db = doc.select("ol > li.result");

        for(int i = 0, size = id_db.size(); i < size; i++) {

            AddressDto addressDto = new AddressDto().setNo(no_db.get(i).text())
                                                    .setNickName(nickName_db.get(i).text())
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
        handler.post(() -> recyclerView.setAdapter(adapter));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        AddressListAdapter.onActivityResult(requestCode, resultCode, data);

        //정보 수정
        if(requestCode == EDIT_REQUEST_CODE) {  //요청 판단
            if(resultCode == RESULT_OK) {       //성공 시

                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        }

    }


    //뒤로 가기
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {//toolbar 의 back 키 눌렀을 때 동작

            setResult(RESULT_OK, intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
