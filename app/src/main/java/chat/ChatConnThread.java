package chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import database.ChattingRoomListHelper;
import database.MessageHelper;
import dto.ChattingRoomListDto;
import dto.Message;
import dto.MessageData;

public class ChatConnThread extends Thread{

    private static final String TAG = ChatConnThread.class.getSimpleName();

    private SQLiteDatabase database;
    private MessageHelper dbHelper;
    private MessageData messageData;
    private Message message;

    private Context context;
    private Socket socket;
    private String userId;

//    private SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm");
    private SimpleDateFormat timeNow = new SimpleDateFormat("a K:mm");
    private Date today = new Date();
    private String currentRoomId;
    private String roomId;
    private String toId;

    private BufferedReader input;
    private PrintWriter output;

    private String delim1 = "/@";
    private String delim2 = "/&-";
    private String delim4 = "/~=";

    private BroadcastReceiver receiver;


    //생성자 생성
    public ChatConnThread(Context context, String userId) {
        this.context = context;
        this.userId = userId;
        Log.d(TAG, "ChatConnThread, 소켓 연결");
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setRoomId(String currentRoomId) {
        this.currentRoomId = currentRoomId;
    }




    //메세지 전송
    public void sendMsg(Message msg) {
        String sendMsg = "";

        int signal = Integer.parseInt(msg.getSignal());
        Log.d(TAG, "===========sendMsg, signal =======> " + signal); // 110-> 100-> 120
        Log.d(TAG, "===========sendMsg, msg ==========> " + msg.getMessage());

        dbHelper = new MessageHelper(context);
        Log.d(TAG, "===========sendMsg, context =======> " + context.toString());
        database = dbHelper.getWritableDatabase();
        Log.d(TAG, "===========sendMsg, database ======> " + database.toString());

        switch(Signals.getSignals(signal)) {

            //입장
            case CHECK_IN : //-> 110
//                sendMsg = msg.getSignal() + delim1
//                        + msg.getRoomId() + delim2
//                        + msg.getToId()   + delim2
//                        + msg.getPhoto()  + delim2
//                        + msg.getMessage();

                sendMsg = Signals.CHECK_IN.getSignal() + delim1
                        + msg.getRoomId() + delim2
                        + msg.getToId()   + delim2
                        + msg.getPhoto();

                roomId = msg.getRoomId();
                toId = msg.getToId();

                Log.d(TAG, "checkIn 의 roomId ====> " + roomId);
                Log.d(TAG, "checkIn 의 toId ======> " + toId);

                break;

            //퇴장
            case CHECK_OUT :    //-> 120
//                sendMsg = msg.getSignal() + delim1;

                sendMsg = Signals.CHECK_OUT.getSignal() + delim1;

                roomId = null;
                break;

            //메세지
            case MSG :  //-> signal : 100
//                sendMsg = msg.getSignal() + delim1
//                        + msg.getMessage();

                sendMsg = Signals.MSG.getSignal() + delim1 //-> 100/@
                        + msg.getMessage();

                break;

        }   //end switch

        try {
            checkConnSocket();
            output.println(sendMsg);
//            output.flush();


            Log.d(TAG, "===========sendMsg, switch 밖 =======> " + sendMsg);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }   //end sendMsg




    @Override
    public void run() {

        //message 읽기
        while(true) {
            try {

                Log.d(TAG, "while 문");

                checkConnSocket();  //소켓 연결 및 output, input 생성


                String line = input.readLine();
                Log.d(TAG, "run 의 while 문 안, line =========> " + line);
//                String lines[] = line.split(delim1);


                String[] splitData = StringSplit1(line);
//                splitData[0] = "100";
                int signal = Integer.parseInt(splitData[0]);

                Log.d(TAG, "run 의 while 문 안, signal =========> " + signal);

                dbHelper = new MessageHelper(context);
                database = dbHelper.getWritableDatabase();  //DB 가져오기

                line = splitData[1];

                switch (Signals.getSignals(signal)) {
                    case MSG :
                        message = StringSplit2(line, "msg");


                            roomId = message.getFromId();


                        if(roomId.equals(currentRoomId)) {
                            Log.d(TAG, "룸아이디 : " + roomId + "메세지 : " + message.getMessage());

                            Intent intent = new Intent("broadcast_" + roomId);
                            context.sendBroadcast(intent);

                        } else {

                            //메세지 저장
                            messageData = new MessageData();
                            messageData.setUnread(1);
                            messageData.setUserId(message.getToId());
                            messageData.setOtherId(message.getFromId());
                            messageData.setRoomName(message.getFromId());
                            messageData.setContent(message.getMessage());
                            messageData.setTime(timeNow.format(today));


                            dbHelper.insert(messageData);
                            Log.d(TAG, "messageData.toString => " + messageData.toString());

                            //채팅룸 생성
                            ChattingRoomListHelper chattingRoomListHelper = new ChattingRoomListHelper(context);
                            if(chattingRoomListHelper.findRoom(message.getToId(), message.getFromId()) == null) {

                                ChattingRoomListDto roomListDto = new ChattingRoomListDto();
                                roomListDto.setRoomName(message.getFromId());
                                roomListDto.setMyId(message.getToId());
                                roomListDto.setOtherId(message.getFromId());
                                roomListDto.setLastContent(message.getMessage());
                                roomListDto.setProfileImage("");
                                roomListDto.setTime(timeNow.format(today));

                                chattingRoomListHelper.insert(roomListDto);
                            }

                        }

                        Intent intent = new Intent("broadcast_entrance");
                        context.sendBroadcast(intent);

                        Toast.makeText(context, "신호받음" + message.getMessage() , Toast.LENGTH_SHORT).show();

                        break;

                    case MSG_IMG :
                        break;

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //연결 확인
    public synchronized void checkConnSocket() {
        while(true && !isOnLine()) {

            Log.d(TAG, "synchronized void checkConnSocket 들어옴 ");

            try {
                socket = new Socket(Constants.SEVER_IP, Constants.SERVER_PORT);
                output = new PrintWriter
                        (new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));


                output.println(Signals.LOGIN.getSignal() + delim1 + userId); //-> 140/@아이디값 : 핸들러가 new 해서 생성됨

                Log.d(TAG, "checkConnSocket : output.println => " + Signals.LOGIN.getSignal() + delim1 + userId);

                Thread.sleep(100);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }   //end checkConnSocket


    public boolean isOnLine() {

        if(socket == null) {
            return false;
        }

        boolean ret = true;

        try {
            /*
            sendUrgentData(int data) : 소켓상의 1바이트의 긴급 데이터를 송신
             */
            socket.sendUrgentData(0xFF);

        } catch (Exception e) {
            Log.d(TAG, "소켓 연결 끊김");
            ret = false;
            closeSocket();

        }
        return ret;
    }

    public void closeSocket() {

        try {
             if(socket != null) {
                 output.close();
                 input.close();
                 socket.close();

                 socket = null;
                 Log.d(TAG, "Socket 닫음");
             }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String[] StringSplit1(String data) {

        Log.d(TAG, "======StringSplit1 data 1 =======> " + data);

        String[] result = new String[2];
        String[] datas = data.split("\\,");

        data = "";
        Log.d(TAG, "======StringSplit1 data='' ======> " + data);

//        if(datas.length > 2) {
//            data = datas[1];
//
//            for(int i = 2; i < data.length(); i++) {
//                data += "," + datas[i];
//
//                Log.d(TAG, "======StringSplit1 for 문, data2 ======> " + data);
//
//            }
//
//        }

//        else {
            data = datas[1];
            Log.d(TAG, "======StringSplit1 else 문, data3 ======> " + data);
//        }

        datas = data.split(delim1); //-> "/@"

        Log.d(TAG, "======StringSplit1 else 문 밖의 data4 ======> " + data);
        String str = "";

        for(int i = 0; i < datas.length; i++) {
            str = datas[i]; //-> 수정

            Log.d(TAG , "======StringSplit1 datas[0] ======> " + datas[0]);
            Log.d(TAG , "======StringSplit1  datas[1] = str1 ======> " + str);

            if(i == 0) {
                result[0] = str;
                Log.d(TAG , "======StringSplit1 result[0] = str ======> " + result[0]);

            } else {
                result[1] = str;
                Log.d(TAG , "======StringSplit1 result[1] = str ======> " + result[1]);
            }

        }

        return result;
    }


    public Message StringSplit2(String data, String type) {
        message = new Message();

        String[] datas = data.split(delim2);    //-> /&-
        String str = "";

        if(type.equals("msg")) {
            for(int i = 0; i < datas.length; i++) {
                str = datas[i];
                Log.d("===========stringSplit msg======", str +" i => " + i);

                if(i == 0) {
                    message.setRoomId(str);

                } else if(i == 1) {
                    message.setFromId(str);

                } else if(i == 2) {
                    message.setToId(str);

                } else if(i == 3) {
                    message.setPhoto(str);

                } else if (i == 4) {
                    String[] datas2 = str.split(delim4);    //-> /~=

                    str = "";

                    for(int j = 0; j < datas2.length; j++) {
                        Log.d("===========stringSplit datas2======", Integer.toString(datas2.length));
                        Log.d("===========stringSplit datas2======", datas2[0]);
//                        Log.d("===========stringSplit datas2======", datas2[1]);
                        if(j == 0) {
                            str += datas2[j];
                            Log.d("===========stringSplit str++======", str);

                        } else {
                            str += "\n" + datas2[j];
                        }
                    }
                }
                message.setMessage(str);
                Log.d("===========stringSplit message.setMessage(str)======", message.setMessage(str).toString());
//                else if ( i == 0) {
//                    message.setRoomId(str);
//                    Log.d("===========stringSplit setRoomId======", message.setRoomId(str).toString());
//                }
            }
        }
        return message;
    }
}
