package com.example.chatproject5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import java.util.ArrayList;

public class AddressActivity extends AppCompatActivity {

    private static final int SEARCH_ADDRESS_ACTIVITY = 10000;//기존

    private Intent intent = new Intent();
    private Handler handler = new Handler();
    private String userId_db, nickName, name, phone, address1, address2, result;

    private EditText et_address;//기존
    private EditText nickName_edit, name_edit, phone_edit, address1_edit, address2_edit;
    private CheckBox checkBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_address);

        setTitle("배송지 추가하기");

        Intent intent = getIntent();
        userId_db = intent.getStringExtra("userId_db");

        nickName_edit = findViewById(R.id.nickName_add);
        name_edit = findViewById(R.id.name_add);
        phone_edit = findViewById(R.id.phone_add);
        address1_edit = findViewById(R.id.address1_add);
        address2_edit = findViewById(R.id.address2_add);
        checkBox = findViewById(R.id.check_add);
        Button search_button = findViewById(R.id.search_add);
        Button addAddress_button = findViewById(R.id.addAddress_add);

        //주소 검색
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (search_button != null) {
                    search_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(getApplicationContext(), WebViewActivity.class);
                            startActivityForResult(i, SEARCH_ADDRESS_ACTIVITY);
                        }
                    });

                }
            }
        });

        //완료 버튼
        addAddress_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nickName = nickName_edit.getText().toString().trim();
                name = name_edit.getText().toString().trim();
                phone = phone_edit.getText().toString().trim();
                address1 = address1_edit.getText().toString().trim();
                address2 = address2_edit.getText().toString().trim();

                if(checkBox.isChecked()) {
                    result = "1";
                } else {
                    result = "0";
                }


                if(name.equals("")) {
                    Toast.makeText(AddressActivity.this, "이름을 입력해 주세요.", Toast.LENGTH_SHORT).show();

                } else if(phone.equals("")) {
                    Toast.makeText(AddressActivity.this, "휴대폰번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();

                } else if(address1.equals("")) {
                    Toast.makeText(AddressActivity.this, "주소를 입력해 주세요.", Toast.LENGTH_SHORT).show();

                } else {

                    final String urlStr = "http://192.168.0.17:8080/webapp/webServer/insertAddress.do";

                    insertAddress(urlStr);
                }

            }
        });
/*
        et_address = (EditText) findViewById(R.id.et_address);
        Button btn_search = findViewById(R.id.button);

        if (btn_search != null) {
            btn_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Intent i = new Intent(getApplicationContext(), WebViewActivity.class);
                    startActivityForResult(i, SEARCH_ADDRESS_ACTIVITY);
                }
            });

        }

 */
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case SEARCH_ADDRESS_ACTIVITY:
                if (resultCode == RESULT_OK) {

                    //webViewActivity 에서 선택한 주소 값 가져오기
                    String data = intent.getExtras().getString("data");

                    if (data != null) {
                        address1_edit.setText(data);
                    }
                }
                break;
        }
    }

    public void insertAddress(String urlStr) {

        nickName = nickName_edit.getText().toString().trim();
        name = name_edit.getText().toString().trim();
        phone = phone_edit.getText().toString().trim();
        address1 = address1_edit.getText().toString().trim();
        address2 = address2_edit.getText().toString().trim();

        StringBuilder output = new StringBuilder();

        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if (conn != null) {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);

                OutputStream outputStream = conn.getOutputStream();

                //값 넣어주기
                String params = "id=" + userId_db
                                + "&name=" + nickName
                                + "&userName=" + name
                                + "&phone=" + phone
                                + "&address1=" + address1
                                + "&address2=" + address2
                                + "&result=" + result;

                outputStream.write(params.getBytes());


                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = null;

                while (true) {
                    line = reader.readLine();

                    if (line == null) {
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

//        setInsertAddress(output.toString());
        println();
    }

    /*
    public void setInsertAddress(String str) {
        Document doc = Jsoup.parse(str);
        Elements nickName_db = doc.select("ol > li.nickName");
        Elements userName_db = doc.select("ol > li.userName");
        Elements id_db = doc.select("ol > li.userId");
        Elements phone_db = doc.select("ol > li.phone");
        Elements address1_db = doc.select("ol > li.address1");
        Elements address2_db = doc.select("ol > li.address2");
        Elements result_db = doc.select("ol > li.result");


        ArrayList<String> addInfo = new ArrayList<>();

        for(int i = 0, size = id_db.size(); i < size; i++) {

            addInfo.add(nickName_db.text());
            addInfo.add(userName_db.text());
            addInfo.add(id_db.text());
            addInfo.add(phone_db.text());
            addInfo.add(address1_db.text());
            addInfo.add(address2_db.text());
            addInfo.add(result_db.text());

//           intent.putStringArrayListExtra("addInfo", addInfo);


        }

    }
     */

    public void println() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AddressActivity.this, "배송지가 추가되었습니다.", Toast.LENGTH_SHORT).show();
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

/*
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case SEARCH_ADDRESS_ACTIVITY:
                if (resultCode == RESULT_OK) {

                    //webViewActivity 에서 선택한 주소 값 가져오기
                    String data = intent.getExtras().getString("data");

                    if (data != null) {
                        et_address.setText(data);
                    }
                }
                break;
        }
    }

 */