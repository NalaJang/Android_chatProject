package com.example.chatproject5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import chat.ChatConnThread;
import chat.MsgUtils;
import chat.ThreadUtils;

public class EntranceActivity extends AppCompatActivity{

    private static final String TAG = EntranceActivity.class.getSimpleName();

    Chat_listFragment chat_listFragment;
    Chat_room_listFragment chat_room_listActivity;
    MenuFragment menuFragment;

    Intent intent;
    String userId_db;
    Handler chatConnHandler;
    ChatConnThread chatConnThread;


    //상단 메뉴 추가
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_top, menu);

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance);


        chat_listFragment = new Chat_listFragment();
        chat_room_listActivity = new Chat_room_listFragment();
        menuFragment = new MenuFragment();

        //정보 받기*****
        intent = getIntent();
        userId_db = intent.getStringExtra("userId_db");
        String userName_db = intent.getStringExtra("userName_db");
        String userPw_db = intent.getStringExtra("userPw_db");
        String userEmail_db = intent.getStringExtra("userEmail_db");
        String userPhone_db = intent.getStringExtra("userPhone_db");
        String userContent_db = intent.getStringExtra("userContent_db");
        String userProfilePhoto_db = intent.getStringExtra("userProfilePhoto_db");
        String userPoint_db = intent.getStringExtra("userPoint_db");

        intent.putExtra("userId_db", userId_db);
        Toast.makeText(getApplicationContext(), "userId =" + userId_db, Toast.LENGTH_SHORT).show();

        //프래그먼트로 정보 전달
        Bundle bundle = new Bundle();
        bundle.putString("userId_db", userId_db);
        bundle.putString("userName_db", userName_db);
        bundle.putString("userPw_db", userPw_db);
        bundle.putString("userEmail_db", userEmail_db);
        bundle.putString("userPhone_db", userPhone_db);
        bundle.putString("userContent_db", userContent_db);
        bundle.putString("userProfilePhoto_db", userProfilePhoto_db);
        bundle.putString("userPoint_db", userPoint_db);


        //생성자
        chatConnHandler = ThreadUtils.GetMultiHandler(TAG + "_Chat");
        chatConnThread = new ChatConnThread(this, userId_db);

        Log.d(TAG, "userId ====" + userId_db);

        //핸들러 객체에 넣기
        chatConnHandler.post(chatConnThread);
        MsgUtils.setConnThread(chatConnThread);



        //첫 화면 고정
        getSupportFragmentManager().beginTransaction().replace(R.id.container, chat_room_listActivity).commit();
        //정보 보내기
        chat_room_listActivity.setArguments(bundle);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()) {
                    case R.id.tab1 :

                        getSupportFragmentManager().beginTransaction().replace(R.id.container, chat_listFragment).commit();

                        chat_listFragment.setArguments(bundle);

                        return true;

                    case R.id.tab2 :

                        getSupportFragmentManager().beginTransaction().replace(R.id.container, chat_room_listActivity).commit();

                        chat_room_listActivity.setArguments(bundle);

                        return true;

                    case R.id.tab3 :

                        getSupportFragmentManager().beginTransaction().replace(R.id.container, menuFragment).commit();

                        menuFragment.setArguments(bundle);

                        return true;
                }
                return false;
            }
        }); //end bottom_navigation
    }   //end onCreate


    //상단 메뉴 아이템 클릭
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.search_menu :

                intent = getIntent();
                userId_db = intent.getStringExtra("userId_db");

                //정보 보내기
                intent = new Intent(EntranceActivity.this, SearchActivity.class);
                intent.putExtra("userId_db", userId_db);

                final String urlStr = "http://192.168.0.17:8080/webapp/webServer/workerList.do";

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        search(urlStr);
                        startActivity(intent);  //정보를 넘긴 후 startActivity 적어주기
                    }
                }).start();

                break;
        }
        return true;
    }

    public void search(String urlStr) {

        StringBuilder output = new StringBuilder();

        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if(conn != null) {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);

                OutputStream outputStream = conn.getOutputStream();
                String params = "id=user&pw=1234";
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
        setSearchWorker(output.toString());
    }


    public void setSearchWorker(String urlStr) {

        Document doc = Jsoup.parse(urlStr);
        Elements workerNum_db = doc.select("ol > li.num");
        Elements workerId_db = doc.select("ol > li.id");
        Elements workerContent_db = doc.select("ol > li.content");

        ArrayList<String> items = new ArrayList<>();

        for(int i = 0; i < workerId_db.size(); i++) {

            //num, content 추가
            items.add(workerNum_db.get(i).text());
            items.add(workerId_db.get(i).text());
            items.add(workerContent_db.get(i).text());

            intent.putStringArrayListExtra("strings", items);

        }
    }
}