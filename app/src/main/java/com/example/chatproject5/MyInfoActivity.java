package com.example.chatproject5;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

public class MyInfoActivity extends AppCompatActivity {

    Intent intent;
    Handler handler = new Handler();

    String userId_db;
    EditText userContent, userName, userId, userPw, userEmail, userPhone;
    MenuFragment menuFragment;
    AddAddressFragment addressFragment;

    final String urlStr = "http://192.168.0.17:8080/webapp/webServer/userInfoUpdate.do";
    static final int REQUEST_CODE_UPDATE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);

        setTitle("내 정보");

        //정보 받기
        intent = getIntent();
        userId_db = intent.getStringExtra("userId_db");
        String userContent_db = intent.getStringExtra("userContent_db");
        String userName_db = intent.getStringExtra("userName_db");
        String userPw_db = intent.getStringExtra("userPw_db");
        String userEmail_db = intent.getStringExtra("userEmail_db");
        String userPhone_db = intent.getStringExtra("userPhone_db");


        userContent = findViewById(R.id.userContent_info);
        userName = findViewById(R.id.userName_info);
        userId = findViewById(R.id.userId_info);
        userPw = findViewById(R.id.userPw_info);
        userEmail = findViewById(R.id.userEmail_info);
        userPhone = findViewById(R.id.userPhone_info);
        menuFragment = new MenuFragment();

        userContent.setText(userContent_db);
        userName.setText(userName_db);
        userId.setText(userId_db);
        userPw.setText(userPw_db);
        userEmail.setText(userEmail_db);
        userPhone.setText(userPhone_db);




        /***************** 정보 수정 버튼 *****************/
        Button edit_button = findViewById(R.id.edit_button);
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String password = userPw.getText().toString().trim();
                String email = userEmail.getText().toString().trim();

                if(!password.isEmpty() || !email.isEmpty()) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            update(urlStr);

//                            menuFragment.setArguments(bundle);
//                            setResult(RESULT_OK);
//                            intent = new Intent(getApplicationContext(), menuFragment)
//                            startActivityForResult(intent);

                        }
                    }).start();

                } else {
                    Toast.makeText(getApplicationContext(), "모두 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }

            }
        }); //end editButton onClick




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
    }

    //DB 회원 정보 업데이트
    public void update(String urlStr) {
        StringBuilder output = new StringBuilder();

        String pw = userPw.getText().toString();
        String content = userContent.getText().toString();
        String email = userEmail.getText().toString();
        String phone = userPhone.getText().toString();


        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if(conn != null) {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);

                OutputStream outputStream = conn.getOutputStream();

                //값 넣어주기
                String params = "id=" + userId_db + "&content=" + content + "&pw=" + pw + "&email=" + email + "&phone=" + phone;
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

        setUpdate(output.toString());
        Log.d("======", output.toString());
    }

    public void setUpdate(String outputStr) {
        Document doc = Jsoup.parse(outputStr);
        Elements result = doc.select("p.result");
        Elements userContent_db = doc.select("ol > li.content");
        Elements userPw_db = doc.select("ol > li.pw");
        Elements userEmail_db = doc.select("ol > li.email");
        Elements userPhone_db = doc.select("ol > li.phone");

        for(int i = 0; i < result.size(); i++) {
            if(result.get(0).text().equals("수정성공")) {


//                bundle = new Bundle();
//                bundle.putString("userNewPW", userPw_db.text());
//                bundle.putString("userNewEmail", userEmail_db.text());
//                bundle.putString("userNewPhone", userPhone_db.text());


            } else if(result.get(0).text().equals("수정실패")) {

                println();
            }
        }
    }   //end setUpdate

    //수정 실패시 실행 메소드
    public  void println() {
        handler.post(new Runnable() {
            @Override
            public void run() {

                AlertDialog.Builder builder = new AlertDialog.Builder(MyInfoActivity.this);

                builder.setTitle(R.string.info);
                builder.setMessage("다시 시도해 주세요.");
                builder.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }

}