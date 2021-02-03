package chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
    private ChattingRoomListHelper chattingRoomListHelper;
    private ChattingRoomListDto roomListDto;
    private MessageData messageData;
    private Message message;

    private Context context;
    private Socket socket;
    private String userId;

    private boolean run = true;


//    private SimpleDateFormat timeNow = new SimpleDateFormat("a K:mm");    //-> timeNow.format(new Date()); , 현재시간을 계속 받아야해서
//    private Date today = new Date();
    private String currentRoomId;
    private String roomId;
    private String toId;

    private BufferedReader input;
    private PrintWriter output;

    private String delim1 = "/@";
    private String delim2 = "/&-";
    private String delim4 = "/~=";

//    private BroadcastReceiver receiver;


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

            //로그아웃
            case LOGOUT :
                System.out.println("logout 들어옴");

                sendMsg = Signals.LOGOUT.getSignal() + delim1 + userId;
//                userId = null;

                System.out.println("여기");
                closeConn();
                closeSocket();

//                try {
//
//                    if(socket != null) {
//                        run = false;
//                        System.out.println("run 값 = " + run);
////                        socket.close();
////                        input.close();
////                        output.close();
//
//
//                        closeSocket();
//                    }

//                } catch (Exception e) {
//                    e.printStackTrace();
//                }


                break;

        }   //end switch

        try {
            System.out.println("try 문안의 run = " + run);
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
        while(run) {
            try {
                System.out.println("run 의 run = " + run);
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
                chattingRoomListHelper = new ChattingRoomListHelper(context);

                database = dbHelper.getWritableDatabase();  //DB 가져오기

                line = splitData[1];

                switch (Signals.getSignals(signal)) {
                    case MSG :
                        message = StringSplit2(line, "msg");


                            roomId = message.getFromId();


                         //현재 채팅중인 방에서 메세지를 받을 때
                        if(roomId.equals(currentRoomId)) {

                            Intent intent = new Intent("broadcast_" + roomId);
                            intent.putExtra("message", message.getMessage());
                            intent.putExtra("fromId", message.getFromId());
                            intent.putExtra("time", new SimpleDateFormat("a K:mm").format(new Date()));
                            context.sendBroadcast(intent);  //-> chatRoomActivity 로 전달


                        //다른 메뉴화면에서 메세지를 받을 때
                        } else {

                            //메세지 저장
                            messageData = new MessageData().setNum(0)
                                                            .setUnread(1)
                                                            .setUserId(message.getFromId())
                                                            .setOtherId(message.getToId())
                                                            .setRoomName(message.getFromId())
                                                            .setContent(message.getMessage())
                                                            .setTime(new SimpleDateFormat("a K:mm").format(new Date(System.currentTimeMillis())));


                            dbHelper.insert(messageData);


//                            roomListDto = chattingRoomListHelper.findRoom(message.getFromId());

                            //채팅룸 생성
                            //메세지를 보낸 사람과의 채팅룸이 없다면,
                            if(chattingRoomListHelper.findRoom(message.getFromId()) == null ) {

                                roomListDto = new ChattingRoomListDto().setRoomName(message.getFromId())
                                                                        .setMyId(message.getToId())
                                                                        .setOtherId(message.getFromId())
                                                                        .setLastContent(message.getMessage())
                                                                        .setProfileImage("")
                                                                        .setTime(new SimpleDateFormat("a K:mm").format(new Date(System.currentTimeMillis())));

                                chattingRoomListHelper.insert(roomListDto);


                            } else {

                                Toast.makeText(context, "존재하는 채팅방" , Toast.LENGTH_SHORT).show();
                            }
                        }

                        //putExtra 로 값을 넘긴다.
                        Intent intent = new Intent("broadcast_entrance");
                        intent.putExtra("message", message.getMessage());
                        intent.putExtra("fromId", message.getFromId());
                        intent.putExtra("time", new SimpleDateFormat("a K:mm").format(new Date()));

                        context.sendBroadcast(intent);


                        break;

                    case MSG_IMG :
                        break;

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void closeConn() {
        run = false;
    }

    //연결 확인
    public synchronized void checkConnSocket() {

        while(run && !isOnLine()) {
            System.out.println("run =  " + run);
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

//        }

        datas = data.split(delim1); //-> "/@"

        String str = "";

        for(int i = 0; i < datas.length; i++) {
            str = datas[i]; //-> 수정


            if(i == 0) {
                result[0] = str;

            } else {
                result[1] = str;
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

                        if(j == 0) {
                            str += datas2[j];

                        } else {
                            str += "\n" + datas2[j];
                        }
                    }
                }
                message.setMessage(str);
            }
        }
        return message;
    }
}
