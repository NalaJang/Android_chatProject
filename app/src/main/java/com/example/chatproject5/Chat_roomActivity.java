package com.example.chatproject5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
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
import chat.ThreadUtils;
import database.MessageHelper;
import dto.Message;
import dto.MessageData;

public class Chat_roomActivity extends AppCompatActivity implements Runnable{

    private static final String TAG = Chat_roomActivity.class.getSimpleName();

    String str1;
    String ip = "192.168.0.17";
    int port = 8888;
    PrintWriter output;
    Handler handler = new Handler();

    String userId_db;
    String roomName;
    Vector<MessageData> messageData = new Vector<>();

    TextView message_text;
    EditText message_edit;
    Button send_button;

    //추가
    EditText et;
    ListView listView;
    ArrayList<MessageData> messageItems = new ArrayList<>();
    MessageAdapter2 adapter;

    //추가(21.01.23)
    MessageAdapter3 adapter3;
    ArrayList<Message> messageItems3 =new ArrayList<>();

    Handler chatConnHandler;
    ChatConnThread chatConnThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        Intent intent = getIntent();
        userId_db = intent.getStringExtra("userId_db");
        roomName = intent.getStringExtra("roomName");

        //추가
        getSupportActionBar().setTitle(roomName);  // 방 이름
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //추가
        et = findViewById(R.id.et);
        listView = findViewById(R.id.listView_chat);
        adapter = new MessageAdapter2(messageItems, getLayoutInflater(), userId_db);
        listView.setAdapter(adapter);

        messageItems.add(new MessageData("dd", "sdf", "19:25"));
        messageItems.add(new MessageData("12", "ㅏㅏㅏㅏㅏㅏㅓㅓㅓ", "19:29"));

        //추가(21.01.23)
        adapter3 = new MessageAdapter3(messageItems3, getLayoutInflater(), userId_db);
        listView.setAdapter(adapter3);


        //생성자
        chatConnHandler = ThreadUtils.GetMultiHandler(TAG + "_Chat");
        chatConnThread = new ChatConnThread(this, userId_db);

        Log.d(TAG, "userId ====" + userId_db);

        //핸들러 객체에 넣기
        chatConnHandler.post(chatConnThread);




        //////////////////////////채팅 관련
        Thread thread = new Thread(this);
        thread.start();

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

//                String msg = message_edit.getText().toString();
                String msg = et.getText().toString();

                if(msg.equals("")) {

                    Toast.makeText(getApplicationContext(), "메세지를 입력하세요.", Toast.LENGTH_SHORT).show();

                } else {

                    Date today = new Date();
                    SimpleDateFormat timeNow = new SimpleDateFormat("a K:mm");

                    MessageData data = new MessageData();

                    //값 넣어주기*****
                    data.setUserId(userId_db);
                    data.setMessage(msg);   //리스트에 목록이 추가되는 것처럼 대화가 추가되는 것
                    data.setTime(timeNow.format(today));


//                    message_edit.setText("");
                    et.setText("");

                    //추가(21.01.23)
                    Message msgData = new Message();
                    msgData.setSignal("100");
                    msgData.setToId(userId_db);
                    msgData.setFromId(userId_db);
                    msgData.setMessage(et.getText().toString());
                    msgData.setTime(timeNow.format(today));
                    msgData.setRoomId(roomName);
                    msgData.setPhoto("");

                    messageItems3.add(msgData);
                    MsgUtils.sendMsg(msgData);
                    Log.d(TAG, "sendMsg 들어옴");
                    Log.d(TAG, et.getText().toString());

//                    MsgUtils.sendMsg(100, userId_db, userId_db, et.getText().toString(), timeNow.format(today), roomName, "");


//                    output.println(msg);    //메세지 출력
                    messageItems.add(data);//추가
                    adapter.notifyDataSetChanged();//새로고침
                    listView.setSelection(messageItems.size() -1);//리스트뷰의 마지막 위치로 스크롤 위치 이동
                }

            }
        });

    }   //end onCreate


    //채팅 스레드
    @Override
    public void run() {

        try {
            Socket socket = new Socket(ip, port);

            Log.d("=========", "소켓 연결");

            output = new PrintWriter
                    (new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

            BufferedReader input = new BufferedReader
                    (new InputStreamReader(socket.getInputStream()));

            while(true) {
                String msg = input.readLine();

//                StringTokenizer st = new StringTokenizer(msg);
                StringTokenizer st = new StringTokenizer(msg, "/");
                String str = "";

                while(st.hasMoreElements()) {
                    str = st.nextToken();

                    Log.d("===========str=========", str);
                    //str = 메시지
                    //message = 사용자 리스트

                    if (0 != st.countTokens()) {

//                        data.add(str);    //리스트에 유저 추가

                    } else {
                        str1 = str;
                    }
                }
//                    println(str1);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    /* listView 로 값을 넣었기 때문에 없어도 됨.
    public void println(String msg){
        handler.post(new Runnable() {
            @Override
            public void run() {
//                message_text.append(msg + "\n");

                MessageData data = new MessageData();


                data.setMessage(msg);

            }
        });


    }
    */

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