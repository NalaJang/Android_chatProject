package com.example.chatproject5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

public class EditAddressActivity extends AppCompatActivity {
    private static final int SEARCH_ADDRESS_ACTIVITY = 10000;

    private String num_db, userId_db, nickName, userName, phone, address1, address2, result;
    private TextView nickName_text, userName_text, phone_text, address1_text, address2_text;
    private CheckBox checkBox;

    private final Handler handler = new Handler();
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_edit_address);

        setTitle("배송지 수정");

        intent = new Intent();

        nickName_text = findViewById(R.id.nickName_editAddr);
        userName_text = findViewById(R.id.name_editAddr);
        phone_text = findViewById(R.id.phone_editAddr);
        address1_text = findViewById(R.id.address1_editAddr);
        address2_text = findViewById(R.id.address2_editAddr);
        checkBox = findViewById(R.id.check_editAddr);
        Button searchButton = findViewById(R.id.search_eidtAddr);
        Button editButton = findViewById(R.id.button_editAddr);


        intent = getIntent();
        num_db = intent.getStringExtra(("num_db"));
        userId_db = intent.getStringExtra("userId_db");



        //선택한 배송지
        final String urlStr = Constants.SERVER_URL + "myAddress.do";

        new Thread(new Runnable() {
            @Override
            public void run() {

                myAddress(urlStr);

            }
        }).start();


        //주소 검색 버튼
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (searchButton != null) {
                    searchButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            intent = new Intent(getApplicationContext(), WebViewActivity.class);
                            startActivityForResult(intent, SEARCH_ADDRESS_ACTIVITY);
                        }
                    });

                }
            }
        });


        //수정 버튼
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //기본 배송지 설정 유무
                if(checkBox.isChecked()) {

                    result = "1";
                } else {

                    result = "0";
                }

                final String urlStr2 = Constants.SERVER_URL + "addressUpdate.do";

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        myAddressEdit(urlStr2);

                    }
                }).start();
            }
        });
    }   //end onCreate


    //선택한 주소 값
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case SEARCH_ADDRESS_ACTIVITY:
                if (resultCode == RESULT_OK) {

                    //webViewActivity 에서 선택한 주소 값 가져오기
                    String data = intent.getExtras().getString("data");

                    if (data != null) {
                        address1_text.setText(data);
                    }
                }
                break;
        }
    }

    //기존 정보
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
                String params = "no=" + num_db;

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

        setMyAddress(output.toString());
    }

    //myAddress ->
    public void setMyAddress(String str) {
        Document doc = Jsoup.parse(str);
        Elements nickName_db = doc.select("ol > li.nickName");
        Elements userName_db = doc.select("ol > li.userName");
        Elements id_db = doc.select("ol > li.id");
        Elements phone_db = doc.select("ol > li.phone");
        Elements address1_db = doc.select("ol > li.address1");
        Elements address2_db = doc.select("ol > li.address2");
        Elements result_db = doc.select("ol > li.result");

        for(int i = 0, size = id_db.size(); i < size; i++) {

            nickName = nickName_db.text();
            userName = userName_db.text();
            phone = phone_db.text();
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
                address1_text.setText(address1);
                address2_text.setText(address2);
            }
        });
    }

    //주소 수정
    public void myAddressEdit(String urlStr) {

        nickName = nickName_text.getText().toString().trim();
        userName = userName_text.getText().toString().trim();
        phone = phone_text.getText().toString().trim();
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
                String params = "no=" + num_db
                                + "&id=" + userId_db
                                + "&name=" + nickName
                                + "&userName=" + userName
                                + "&phone=" + phone
                                + "&address1=" + address1
                                + "&address2=" + address2
                                + "&result=" + result;

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
        setMyAddressEdit(output.toString());
    }

    //myAddressEdit ->
    public void setMyAddressEdit(String str) {
        Document doc = Jsoup.parse(str);
        Elements nickName_db = doc.select("ol > li.name");
        Elements userName_db = doc.select("ol > li.userName");
        Elements phone_db = doc.select("ol > li.phone");
        Elements address1_db = doc.select("ol > li.address1");
        Elements address2_db = doc.select("ol > li.address2");
        Elements result_db = doc.select("ol > li.result");

        for(int i = 0, size = userName_db.size(); i < size; i++) {

            nickName = nickName_db.text();
            userName = userName_db.text();
            phone = phone_db.text();
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
                address1_text.setText(address1);
                address2_text.setText(address2);


                Toast.makeText(getApplicationContext(), "수정되었습니다.", Toast.LENGTH_SHORT).show();

            }
        });
    }


    //뒤로 가기
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar 의 back 키 눌렀을 때 동작

                setResult(RESULT_OK, intent);
                finish();

                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}