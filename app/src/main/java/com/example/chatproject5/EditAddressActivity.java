package com.example.chatproject5;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class EditAddressActivity extends AppCompatActivity {

    private String userId_db, nickName, userName, phone, zip_num, address1, address2, result;
    private TextView nickName_text, userName_text, phone_text, zip_num_text, address1_text, address2_text;
    private CheckBox checkBox;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_edit_address);

        nickName_text = findViewById(R.id.nickName_editAddr);
        userName_text = findViewById(R.id.name_editAddr);
        phone_text = findViewById(R.id.phone_editAddr);
        zip_num_text = findViewById(R.id.zip_num_editAddr);
        address1_text = findViewById(R.id.address1_editAddr);
        address2_text = findViewById(R.id.address2_editAddr);
        checkBox = findViewById(R.id.check_editAddr);
        Button editButton = findViewById(R.id.button_editAddr);


        Intent intent = getIntent();
        userId_db = intent.getStringExtra("userId_db");
//        nickName_db = intent.getStringExtra("nickName_db");
//        userName_db = intent.getStringExtra("userName_db");
//        phone_db = intent.getStringExtra("userPhone_db");
//        address1_db = intent.getStringExtra("userAddress1_db");
//        address2_db = intent.getStringExtra("userAddress2e_db");

//        nickName_text.setText(nickName_db);
//        userName_text.setText(userName_db);
//        phone_text.setText(phone_db);
//        address1_text.setText(address1_db);
//        address2_text.setText(address2_db);

        //선택한 배송지
        final String urlStr = "http://192.168.0.17:8080/webapp/webServer/addressList.do";

        new Thread(new Runnable() {
            @Override
            public void run() {

                myAddress(urlStr);

            }
        }).start();


        //수정
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //기본 배송지 설정 유무
                if(checkBox.isChecked()) {

                    result = "1";
                } else {

                    result = "0";
                }

                final String urlStr2 = "http://192.168.0.17:8080/webapp/webServer/addressUpdate.do";

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        myAddressEdit(urlStr2);

                    }
                }).start();
            }
        });
    }   //end onCreate

    public void myAddress(String urlStr) {
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

        setMyAddress(output.toString());
    }

    public void setMyAddress(String str) {
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
            //userId_db, nickName, userName, phone, zip_num, address1, address2_db, result;
            nickName = nickName_db.text();
            userName = userName_db.text();
            phone = phone_db.text();
            zip_num = zip_num_db.text();
            address1 = address1_db.text();
            address2 = address2_db.text();
            result = result_db.text();
        }

        println();
    }

    public void println() {
        handler.post(new Runnable() {
            @Override
            public void run() {

                if(result.equals("1")) {
                    checkBox.setChecked(true);
                }

                nickName_text.setText(nickName);
                userName_text.setText(userName);
                phone_text.setText(phone);
                zip_num_text.setText(zip_num);
                address1_text.setText(address1);
                address2_text.setText(address2);
            }
        });
    }

    public void myAddressEdit(String urlStr) {

        nickName = nickName_text.getText().toString().trim();
        userName = userName_text.getText().toString().trim();
        phone = phone_text.getText().toString().trim();
        zip_num = zip_num_text.getText().toString().trim();
        address1 = address1_text.getText().toString().trim();
        address2 = address2_text.getText().toString().trim();


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
                String params = "id=" + userId_db
                                + "&name=" + nickName
                                + "&userName=" + userName
                                + "&phone=" + phone
                                + "&zip_num=" + zip_num
                                + "&address1=" + address1
                                + "&address2=" + address2
                                + "&result=" + result;


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
        setMyAddressEdit(output.toString());

    }

    public void setMyAddressEdit(String str) {
        Document doc = Jsoup.parse(str);
        Elements nickName_db = doc.select("ol > li.name");
        Elements userName_db = doc.select("ol > li.userName");
        Elements phone_db = doc.select("ol > li.phone");
        Elements zip_num_db = doc.select("ol > li.zip_num");
        Elements address1_db = doc.select("ol > li.address1");
        Elements address2_db = doc.select("ol > li.address2");
        Elements result_db = doc.select("ol > li.result");

        for(int i = 0, size = userName_db.size(); i < size; i++) {

            nickName = nickName_db.text();
            userName = userName_db.text();
            phone = phone_db.text();
            zip_num = zip_num_db.text();
            address1 = address1_db.text();
            address2 = address2_db.text();
            result = result_db.text();

        }

        println2();

    }

    public void println2() {
        handler.post(new Runnable() {
            @Override
            public void run() {

                nickName_text.setText(nickName);
                userName_text.setText(userName);
                phone_text.setText(phone);
                zip_num_text.setText(zip_num);
                address1_text.setText(address1);
                address2_text.setText(address2);

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