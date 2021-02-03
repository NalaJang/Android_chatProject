package com.example.chatproject5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import adapter.MessageAdapter3;
import chat.MsgUtils;
import chat.Signals;
import database.ChattingRoomListHelper;
import database.MessageHelper;
import dto.ChattingRoomListDto;
import dto.Message;
import dto.MessageData;

public class Chat_roomActivity extends AppCompatActivity {
    private static final String TAG = Chat_roomActivity.class.getSimpleName();


    private String userId_db;
    private String roomName;


    private EditText et;
    private ListView listView;
    private ArrayList<MessageData> messageItems = new ArrayList<>();
//    private MessageAdapter2 adapter;

//    private Date today = new Date();
//    private SimpleDateFormat timeNow = new SimpleDateFormat("a K:mm");

    //추가
    private MessageAdapter3 adapter3;
    private ArrayList<Message> messageItems3 =new ArrayList<>();


    private ArrayList<MessageData> newMessageList = new ArrayList<>();
    private MessageData messageData;
    private MessageHelper msgHelper;
    private Message message;

    private ChattingRoomListHelper roomListHelper;

    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        Intent intent = getIntent();
        userId_db = intent.getStringExtra("userId_db");
        roomName = intent.getStringExtra("roomName");


        setTitle(roomName);


        et = findViewById(R.id.et);
        listView = findViewById(R.id.listView_chat);


        msgHelper = new MessageHelper(this);
        message = new Message();

        //DB 에서 채팅 목록 가져오기
        messageItems = msgHelper.messageList(roomName);


        adapter3 = new MessageAdapter3(messageItems, getLayoutInflater(), userId_db);
        listView.setAdapter(adapter3);

        //현재 위치 서버에 알리기
//        MsgUtils.setContext(this);
        MsgUtils.setCurrentRoom(roomName);



        //////////////////////////채팅 관련
        //실제 배포시에는 적용 x
        /*
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build()); //네트워크 위반시 로그로 알림

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build());  //위반시 강제종료

         */

        //추가
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        /**/
        IntentFilter filter = new IntentFilter();
        filter.addAction("broadcast_" + roomName);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String intentMsg = intent.getStringExtra("message");
                String fromId = intent.getStringExtra("fromId");
                String time = intent.getStringExtra("time");


                messageData = new MessageData();
                messageData.setUnread(0);
                messageData.setUserId(fromId);    //*****
                messageData.setOtherId(userId_db);//*****
                messageData.setRoomName(fromId);
                messageData.setContent(intentMsg);
                messageData.setTime(time);

                newMessageList.add(messageData);
                adapter3.addItem(messageData);

                adapter3.notifyDataSetChanged();              //새로고침
                listView.setSelection(messageItems.size() -1);//리스트뷰의 마지막 위치로 스크롤 위치 이동

            }
        };

        registerReceiver(receiver, filter);
        /**/



        //메세지 전송 버튼
        Button send_button = findViewById(R.id.send_button_chat);
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String msg = et.getText().toString();

                if(msg.equals("")) {

                    Toast.makeText(getApplicationContext(), "메세지를 입력하세요.", Toast.LENGTH_SHORT).show();

                } else {
                    Log.e(TAG, "sendButton 클릭");


                    roomListHelper = new ChattingRoomListHelper(getApplicationContext());
                    ChattingRoomListDto roomListDto;

                    roomListDto = roomListHelper.findRoom(roomName);
                    //선택한 상담사와의 채팅방이 없을 경우
                    if( roomListDto == null ) {

                        System.out.println("채팅방 없음");

                        roomListDto = new ChattingRoomListDto().setRoomName(roomName)
                                                                .setMyId(userId_db)
                                                                .setOtherId(roomName)
                                                                .setTime(new SimpleDateFormat("a K:mm").format(new Date()));
//                                                                .setTime(timeNow.format(today));

                        roomListHelper.insert(roomListDto);


                    //채팅방이 이미 존재할 경우
                    } else {

                        System.out.println("이미 존재하는 채팅방");

                    }


                    messageData = new MessageData();

                    //값 넣어주기*****
                    messageData.setUnread(0);
                    messageData.setUserId(userId_db);
                    messageData.setOtherId(roomName);
                    messageData.setRoomName(roomName);
                    messageData.setContent(msg);   //리스트에 목록이 추가되는 것처럼 대화가 추가되는 것
                    messageData.setTime(new SimpleDateFormat("a K:mm").format(new Date()));

                    newMessageList.add(messageData);
                    messageItems.add(messageData); //추가



                    Message msgData = new Message();
                    msgData.setSignal("100");
                    msgData.setToId(roomName);
                    msgData.setFromId(userId_db);
                    msgData.setMessage(et.getText().toString());
                    msgData.setTime(new SimpleDateFormat("a K:mm").format(new Date()));
                    msgData.setRoomId(roomName);
                    msgData.setPhoto("");

                    Log.d(TAG, msgData.toString());

                    messageItems3.add(msgData);
                    MsgUtils.sendMsg(msgData);  //***** 서버에 신호 보내기


                    /**/
                    roomListHelper.update(msg, new SimpleDateFormat("a K:mm").format(new Date()), roomName);    //업데이트
                    /**/



                    et.setText("");            //메세지 보낸 후 비우기

                    adapter3.notifyDataSetChanged();//새로고침
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

    @Override   //채팅방에서 나오면 대화 내용 저장
    protected void onPause() {
        super.onPause();

        System.out.println("onPause 들어옴");


        for(int i = 0; i < newMessageList.size(); i++) {

            MessageData data = new MessageData().setUnread(0)
                                                .setUserId(messageData.getUserId())
                                                .setOtherId(messageData.getOtherId())
                                                .setRoomName(roomName)
                                                .setContent(newMessageList.get(i).getContent())
                                                .setTime(new SimpleDateFormat("a K:mm").format(new Date()));


            boolean insert = msgHelper.insert(data);
            if(insert) {
                Toast.makeText(getApplicationContext(), "onPause, 메세지 저장", Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(getApplicationContext(), "onPause, 메세지 저장실패", Toast.LENGTH_SHORT).show();
            }
        }

        message.setSignal(Signals.CHECK_OUT.getSignal() + "");
        MsgUtils.sendMsg(message);
        MsgUtils.setCurrentRoom("");    //***** 현재 채팅방 신호 다시 제로
    }


    //broadcast 해제
    public void onDestroy() {
        if(receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }

        super.onDestroy();
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