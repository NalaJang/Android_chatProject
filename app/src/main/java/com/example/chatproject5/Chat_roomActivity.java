package com.example.chatproject5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;

import adapter.MessageAdapter2;
import adapter.MessageAdapter3;
import chat.ChatConnThread;
import chat.MsgUtils;
import chat.Signals;
import chat.ThreadUtils;
import database.MessageHelper;
import dto.Message;
import dto.MessageData;

public class Chat_roomActivity extends AppCompatActivity {

    private static final String TAG = Chat_roomActivity.class.getSimpleName();


    String userId_db;
    String roomName;

    TextView message_text;
    EditText message_edit;
    Button send_button;

    //추가
    EditText et;
    ListView listView;
    ArrayList<MessageData> messageItems = new ArrayList<>();
    MessageAdapter2 adapter;

    Date today = new Date();
    SimpleDateFormat timeNow = new SimpleDateFormat("a K:mm");

    //추가(21.01.23)
    MessageAdapter3 adapter3;
    ArrayList<Message> messageItems3 =new ArrayList<>();

    //추가(21.01.25)
    ArrayList<MessageData> newMessage = new ArrayList<>();
    MessageHelper msgHelper;
    Message message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        userId_db = intent.getStringExtra("userId_db");
        roomName = intent.getStringExtra("roomName");

        //추가
        setTitle(roomName);  // 방 이름


        //추가
        et = findViewById(R.id.et);
        listView = findViewById(R.id.listView_chat);
        adapter = new MessageAdapter2(messageItems, getLayoutInflater(), userId_db);
        listView.setAdapter(adapter);

        //추가(21.01.23)
        adapter3 = new MessageAdapter3(messageItems3, getLayoutInflater(), userId_db);
        listView.setAdapter(adapter3);

        //추가(21.01.25)
        MsgUtils.setContext(this);
        MsgUtils.setCurrentRoom(roomName);

        //추가(21.01.25)
        msgHelper = new MessageHelper(this);
        message = new Message();

        //////////////////////////채팅 관련
        //실제 배포시에는 적용 x
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build()); //네트워크 위반시 로그로 알림

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build());  //위반시 강제종료

        message_text = findViewById(R.id.message_text);
        message_edit = findViewById(R.id.message_edit);
        send_button = findViewById(R.id.send_button_chat);

        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String msg = et.getText().toString();

                if(msg.equals("")) {

                    Toast.makeText(getApplicationContext(), "메세지를 입력하세요.", Toast.LENGTH_SHORT).show();

                } else {
                    Log.d(TAG, "sendButton 클릭");



                    MessageData data = new MessageData();

                    //값 넣어주기*****
                    data.setUserId(userId_db);
                    data.setContent(msg);   //리스트에 목록이 추가되는 것처럼 대화가 추가되는 것
                    data.setTime(timeNow.format(today));


                    //추가(21.01.23)
                    Message msgData = new Message();
                    msgData.setSignal("100");
                    msgData.setToId(roomName);
                    msgData.setFromId(userId_db);
                    msgData.setMessage(et.getText().toString());
                    msgData.setTime(timeNow.format(today));
                    msgData.setRoomId(roomName);
                    msgData.setPhoto("");
                    Log.d(TAG, "-------msg-------------------------> " + et.getText().toString()); //-> 100

                    messageItems3.add(msgData);
                    MsgUtils.sendMsg(msgData);  //*****


                    et.setText(""); //보낸 후 지우기

                    messageItems.add(data);//추가
                    adapter.notifyDataSetChanged();//새로고침
                    listView.setSelection(messageItems.size() -1);//리스트뷰의 마지막 위치로 스크롤 위치 이동
                }

            }
        });

    }   //end onCreate


    //기존의 채팅 스레드 -> chatConnThread 에서 실행하기 때문에 여기에 쓰지않는다.
    /*
    @Override
    public void run() {

        try {
            Socket socket = new Socket(ip, port);
                    . . .
    }

     */

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

    @Override   //대화 내용 저장
    protected void onPause() {
        super.onPause();

        MessageData data = new MessageData();
        data.setUserId(userId_db);
        data.setOtherId(roomName);
        data.setRoomName(roomName);
//        data.setContent(message_edit.getText().toString());   //-> NULL 에러
        data.setContent("");
        data.setTime(timeNow.format(today));

        Boolean insert = msgHelper.insert(data);

        if(insert == true) {
            Toast.makeText(getApplicationContext(), "onPause, 메세지 저장", Toast.LENGTH_SHORT).show();
        }

        message.setSignal(Signals.CHECK_OUT.getSignal() + "");
        MsgUtils.sendMsg(message);
    }

/*
    //대화내용저장
    public void onBackPressed() {
        long mMemoId = -1;

        ContentValues contentValues = new ContentValues();
        contentValues.put("userId", userId_db);
        contentValues.put("otherId", roomName);

        //db 에 작성하기 위한 writable
        SQLiteDatabase db = MessageHelper.getInstance(this).getWritableDatabase();

        if (mMemoId == -1) {
            // DB 에 저장하는 처리
            long newRowId = db.insert("message", null, contentValues);

            if (newRowId == -1) {
                Toast.makeText(this, "저장에 문제가 발생하였습니다.",
                        Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "메모가 저장되었습니다.",
                        Toast.LENGTH_SHORT).show();

                setResult(RESULT_OK);
            }
        }
        super.onBackPressed();
    }   //end backPressed

 */
}