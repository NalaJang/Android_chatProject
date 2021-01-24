package chat;

import android.content.Context;
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
import java.util.ArrayList;

import database.MessageHelper;
import dto.Message;
import dto.MessageData;

public class ChatConnThread extends Thread{

    private static final String TAG = ChatConnThread.class.getSimpleName();

    private SQLiteDatabase database;
    private MessageHelper dbHelper;

    private Context context;
    private Socket socket;
    private String userId;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm");
    private ArrayList<String> roomMembers;
    private Message message;
    private String currentRoomId;
    private String roomId;
    private String toId;
    private String sql;

    private BufferedReader input;
    private PrintWriter output;

    private String delim1 = "/@";
    private String delim2 = "/&-";
    private String delim4 = "/~=";


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
        Log.d(TAG, "===========sendMsg, signal =======> " + signal); //-> 100
        Log.d(TAG, "===========sendMsg, msg =======> " + msg.getMessage()); //-> 100

        dbHelper = new MessageHelper(context);
        Log.d(TAG, "===========sendMsg, context ======> " + context.toString());
        database = dbHelper.getWritableDatabase();

        switch(Signals.getSignals(signal)) {

            //입장
            case CHECK_IN :
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
            case CHECK_OUT :
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


                try {
                    checkConnSocket();
                    output.println(sendMsg);
                    output.flush();

                    Log.d(TAG, "===========sendMsg, output.println(sendMsg)=======> " + sendMsg);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

        }

    }   //end sendMsg

    @Override
    public void run() {


        //message 읽기
        while(true) {
            try {

                checkConnSocket();
                Log.d(TAG, "while 문");



                String line = input.readLine();
                Log.d(TAG, "run 의 while 문 안, line =========> " + line);
//                String lines[] = line.split(delim1);


                String[] splitData = StringSplit1(line);
                int signal = Integer.parseInt(splitData[0]);
                Log.d(TAG, "run 의 while 문 안, signal =========> " + signal);
                dbHelper = new MessageHelper(context);
                database = dbHelper.getWritableDatabase();  //DB 가져오기

                line = splitData[1];

                switch (Signals.getSignals(signal)) {
                    case MSG :
                        message = StringSplit(line, "msg");

                        if(message.getRoomId().startsWith("GroupChatting:20")) {
                            roomId = message.getRoomId();

                        } else {
                            roomId = message.getFromId();

                        //현재 액티비티가 채팅 보낸 친구의 채팅룸인지
                        } if(roomId.equals(currentRoomId)) {
                            Log.d(TAG, "컨텍스트 : " + context + " 룸아이디 : " + roomId + "메세지 : " + message.getMessage());


                        } else {

                        }
                        break;
                    case MSG_IMG :
                        break;

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void checkConnSocket() {
        while(true && !isOnLine()) {

            Log.d(TAG, "synchronized void checkConnSocket 들어옴 ");

            try {
                socket = new Socket(Constants.SEVER_IP, Constants.SERVER_PORT);
                output = new PrintWriter
                        (new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));


                output.println(Signals.LOGIN.getSignal() + delim1 + userId); //-> 140/@아이디값 : 핸들러가 new 해서 생성됨
//                output.flush();

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
        String[] result = new String[2];

        Log.d(TAG, "======StringSplit1 result======>" + result);

        Log.d(TAG, "======StringSplit1 data1======>" + data);

        String[] datas = data.split("\\,");
        data = "";

        if(datas.length > 2) {
            data = datas[1];

            for(int i = 2; i < data.length(); i++) {
                data += "," + datas[1];
            }

        } else {
            data = datas[1];
        }
        Log.d(TAG, "======data2======" + data);
        datas = data.split(delim1);
        String str = "";

        for(int i = 0; i < datas.length; i++) {
            str = datas[1];

            Log.d( "======StringSplit1======" , str);

            if(i == 0) {
                result[0] = str;

            } else {
                result[1] = str;
            }
        }
        return result;
    }

    public Message StringSplit(String data, String type) {
        message = new Message();

        String[] datas = data.split(delim2);
        String str = "";

        if(type.equals("msg")) {
            for(int i = 0; i < datas.length; i++) {
                str = datas[i];
                Log.d("===========stringSplit msg======", str);

                if(i == 1) {
                    message.setFromId(str);

                } else if(i == 2) {
                    message.setToId(str);

                } else if(i == 3) {
                    message.setPhoto(str);

                } else if (i == 4) {
                    String[] datas2 = str.split(delim4);
                    str = "";

                    for(int j = 0; j < datas2.length; j++) {
                        if(j == 0) {
                            str += datas2[j];

                        } else {
                            str += "\n" + datas2[j];
                        }
                    }
                    message.setMessage(str);
                } else if ( i == 0) {
                    message.setRoomId(str);
                }
            }
        }
        return message;
    }
}
