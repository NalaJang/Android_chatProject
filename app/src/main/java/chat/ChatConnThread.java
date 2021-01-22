package chat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import database.MessageHelper;
import dto.Message;
import dto.MessageData;

public class ChatConnThread extends Thread{

    private SQLiteDatabase database;
    private MessageHelper dbHelper;
    private Context context;
    private String roomId;
    private String toId;

    private BufferedReader input;
    private PrintWriter output;

    String delim1 = "/@";
    String delim2 = "/&-";

    public ChatConnThread(Context context) {
        this.context = context;

    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void sendMsg(Message msg) {
        String sendMsg = "";

        int signal = Integer.parseInt(msg.getSignal());

        switch(Signals.getSignals(signal)) {

            //입장
            case CHECK_IN :
                sendMsg = msg.getSignal() + delim1
                        + msg.getRoomId() + delim2
                        + msg.getToId()   + delim2
                        + msg.getPhoto()  + delim2
                        + msg.getMessage();

                roomId = msg.getRoomId();
                toId = msg.getToId();
                break;

            //퇴장
            case CHECK_OUT :
                sendMsg = msg.getSignal() + delim1;

                roomId = null;
                break;

            //메세지
            case MSG :
                sendMsg = msg.getSignal() + delim1
                        + msg.getMessage();

                break;

        }

    }   //end sendMsg

    @Override
    public void run() {


        //messga 읽기
        while(true) {
            try {
                Socket socket = new Socket(Constants.SEVER_IP, Constants.SERVER_PORT);

                output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String line = input.readLine();
                String lines[] = line.split(delim1);

                dbHelper = new MessageHelper(context);
                database = dbHelper.getWritableDatabase();  //DB 가져오기

                int signal = Integer.parseInt(lines[0]);

                switch (Signals.getSignals(signal)) {
                    case MSG :


                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
