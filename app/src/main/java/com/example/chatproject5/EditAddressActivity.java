package com.example.chatproject5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class EditAddressActivity extends AppCompatActivity {

    private String userId_db, nickName_db, userName_db, phone_db, zip_num_db, address1_db, address2_db, result;
    private TextView nickName, userName, phone, zip_num, address1, address2;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);

        nickName = findViewById(R.id.nickName_editAddr);
        userName = findViewById(R.id.name_editAddr);
        phone = findViewById(R.id.phone_editAddr);
        zip_num = findViewById(R.id.zip_num_editAddr);
        address1 = findViewById(R.id.address1_editAddr);
        address2 = findViewById(R.id.address2_editAddr);
        checkBox = findViewById(R.id.check_editAddr);
        Button editButton = findViewById(R.id.button_editAddr);


        Intent intent = getIntent();
        userId_db = intent.getStringExtra("userId_db");
        nickName_db = intent.getStringExtra("nickName_db");
        userName_db = intent.getStringExtra("userName_db");
        phone_db = intent.getStringExtra("userPhone_db");
        address1_db = intent.getStringExtra("userAddress1_db");
        address2_db = intent.getStringExtra("userAddress2e_db");

        nickName.setText(nickName_db);
        userName.setText(userName_db);
        phone.setText(phone_db);
        address1.setText(address1_db);
        address2.setText(address2_db);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkBox.isChecked()) {

                    result = "1";
                } else {

                    result = "0";
                }

                final String urlStr = "http://192.168.0.17:8080/webapp/webServer/addressUpdate.do";

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        myAddressEdit(urlStr);

                    }
                }).start();
            }
        });
    }

    public void myAddressEdit(String urlStr) {

        nickName_db = nickName.getText().toString().trim();
        userName_db = userName.getText().toString().trim();
        phone_db = phone.getText().toString().trim();
        zip_num_db = zip_num.getText().toString().trim();
        address1_db = address1.getText().toString().trim();
        address2_db = address2.getText().toString().trim();


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
                        + "&name=" + nickName_db
                        + "&userName=" + userName_db
                        + "&phone=" + phone_db
                        + "&zip_num=" + zip_num_db
                        + "&address1=" + address1_db
                        + "&address2=" + address2_db
                        + "&result=" + result;

                System.out.println("update Address result => " + result);

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
    }
}