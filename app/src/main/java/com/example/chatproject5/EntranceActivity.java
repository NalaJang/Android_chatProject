package com.example.chatproject5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import adapter.Chat_room_list_Adapter;
import chat.ChatConnThread;
import chat.MsgUtils;
import chat.ThreadUtils;

import static androidx.viewpager.widget.PagerAdapter.POSITION_NONE;

public class EntranceActivity extends AppCompatActivity {

    //implements ViewPager.OnPageChangeListener

    private static final String TAG = EntranceActivity.class.getSimpleName();

    public static HashSet<String> hashSet = new HashSet<>();

    private Chat_listFragment chat_listFragment;
    private Chat_room_listFragment chat_room_listActivity;
    private MenuFragment menuFragment;

    private Intent intent;
    private String userId_db;
    private Handler chatConnHandler;
    private ChatConnThread chatConnThread;

    private BroadcastReceiver receiver;


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



        //브로드캐스트
        IntentFilter filter = new IntentFilter();
        filter.addAction("broadcast_entrance");

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String intentMsg = intent.getStringExtra("message");

                Toast.makeText(getApplicationContext(), "브로드캐스트 받음" + intentMsg, Toast.LENGTH_SHORT).show();

            }
        };

        //브로드캐스트 등록
        registerReceiver(receiver, filter);


        //정보 받기*****
        intent = getIntent();
        userId_db = intent.getStringExtra("userId_db");
        String userName_db = intent.getStringExtra("userName_db");
        String userPw_db = intent.getStringExtra("userPw_db");
        String userEmail_db = intent.getStringExtra("userEmail_db");
        String userPhone_db = intent.getStringExtra("userPhone_db");
        String userContent_db = intent.getStringExtra("userContent_db");
        String userProfilePhoto_db = intent.getStringExtra("userProfilePhoto_db");


        //정보 전달
        intent.putExtra("userId_db", userId_db);


        //프래그먼트로 정보 전달
        Bundle bundle = new Bundle();
        bundle.putString("userId_db", userId_db);
        bundle.putString("userName_db", userName_db);
        bundle.putString("userPw_db", userPw_db);
        bundle.putString("userEmail_db", userEmail_db);
        bundle.putString("userPhone_db", userPhone_db);
        bundle.putString("userContent_db", userContent_db);
        bundle.putString("userProfilePhoto_db", userProfilePhoto_db);


        //생성자 -> 로그인 성공으로 들어오면 새로운 핸들러 생성과 동시에 스레드 시작
        chatConnHandler = ThreadUtils.GetMultiHandler(TAG + "_Chat");
        chatConnThread = new ChatConnThread(this, userId_db);
        //핸들러 객체에 넣기, 소켓 연결
        chatConnHandler.post(chatConnThread);
        MsgUtils.setConnThread(chatConnThread);//서버에 신호보내기


        //추가
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        //첫 화면 고정
        getSupportFragmentManager().beginTransaction().replace(R.id.container, chat_room_listActivity).commit();

        //정보 보내기
        chat_room_listActivity.setArguments(bundle);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.tab2);  //첫 화면 아이콘 지정
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

//                intent = getIntent();
//                userId_db = intent.getStringExtra("userId_db");

                //정보 보내기
                intent = new Intent(EntranceActivity.this, SearchActivity.class);
                intent.putExtra("userId_db", userId_db);


                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(intent);  //정보를 넘긴 후 startActivity 적어주기

                    }
                }).start();

                break;
        }
        return true;
    }

    //broadcast 해제
    public void onDestroy() {
        if(receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }

        super.onDestroy();
    }
}