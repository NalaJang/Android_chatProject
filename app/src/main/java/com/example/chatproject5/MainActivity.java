package com.example.chatproject5;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
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

import chat.Constants;

/******************************************
                  로그인
 ******************************************/
public class MainActivity extends AppCompatActivity {

    private EditText userId, userPw;

    private Intent intent;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        userId = findViewById(R.id.userId_text_login);
        userPw = findViewById(R.id.userPw_text_login);

        Button button_login = findViewById(R.id.button_login);
        TextView search_id = findViewById(R.id.search_id_login);
        TextView sign_up = findViewById(R.id.sign_up_login);



        //로그인 버튼 클릭
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = userId.getText().toString().trim();
                String pw = userPw.getText().toString().trim();

                //공란일 경우
                if(id.equals("") || pw.equals("")) {

                    Toast.makeText(MainActivity.this, "정보를 입력해 주세요.", Toast.LENGTH_SHORT).show();

                } else {

                    final String urlStr = Constants.SERVER_URL + "login.do";

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            login(urlStr);
                        }
                    }).start();
                }
            }
        });   //end button_login


        //아이디,비밀번호 찾기 클릭
        search_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = new Intent(MainActivity.this, Search_userInfoActivity.class);

                startActivity(intent);
            }
        });   //end search_id



        //회원가입 클릭
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = new Intent(MainActivity.this, Sign_upActivity.class);

                startActivity(intent);
            }
        }); //end sign_up
    }   //end onCreate


    public void login(String urlStr) {
        StringBuilder output = new StringBuilder();

        String id = userId.getText().toString();
        String pw = userPw.getText().toString();


        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if(conn != null) {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);

                OutputStream outputStream = conn.getOutputStream();

                //값 넣어주기
                String params = "id=" + id + "&pw=" + pw;

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

        setLogin(output.toString());    //잘라줄 값

    }

    public void setLogin(String str) {

        Document doc = Jsoup.parse(str);
        Elements result = doc.select("p.result");
        Elements userId = doc.select("ol > li.id");
        Elements userName = doc.select("ol > li.name");
        Elements userPw = doc.select("ol > li.pw");
        Elements userEmail = doc.select("ol > li.email");
        Elements userPhone = doc.select("ol > li.phone");
        Elements userContent = doc.select("ol > li.content");
        Elements userProfilePhoto = doc.select("ol > li.profilePhoto");


        for(int i = 0; i < result.size(); i++) {

            //로그인 성공
            if(result.get(0).text().equals("로그인 성공")) {

                intent = new Intent(MainActivity.this, EntranceActivity.class);
                intent.putExtra("userId_db", userId.text());
//                intent.putExtra("userName_db", userName.text());
                intent.putExtra("userPw_db", userPw.text());
//                intent.putExtra("userEmail_db", userEmail.text());
//                intent.putExtra("userPhone_db", userPhone.text());
                intent.putExtra("userContent_db", userContent.text());
                intent.putExtra("userProfilePhoto_db", userProfilePhoto.text());
                startActivity(intent);
                finish();


            //로그인 실패
            } else if(result.get(0).text().equals("로그인 실패")){

                println();
            }
        }
    }   //end setLogin

    //출력 메소드
    public void println() {
        handler.post(new Runnable() {
            @Override
            public void run() {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle(R.string.info);
                builder.setMessage("다시 생각해보세요.");
                builder.setPositiveButton(R.string.close, null);
                builder.show();
            }
        });
    }
    //thread 안에서는 toast 안됨
    //Toast.makeText(MainActivity.this, "환영합니다.", Toast.LENGTH_SHORT).show();
                /*
                public  void println(final String data) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            textView.append(data + "\n");
                        }
                    });
                }
                이런식으로 handler 를 이용해야한다.
                 */

}