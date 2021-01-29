package database;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import dto.Message;
import dto.MessageData;

public class MessageHelper extends SQLiteOpenHelper {



    private static final int DB_VERSION = 3;
    private static final String DB_NAME = "Message.db";


    public MessageHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists message (" +
                "_no integer PRIMARY KEY AUTOINCREMENT, unread integer, userId text, otherId text, roomName text, content text, time text)";

        //실행
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS MESSAGE");
        onCreate(db);
    }
    /*
    int num;
    int unread;
    String userId;
    private String otherId;
    private String roomName;
    private String content;
    private String time;
     */
    public boolean insert(MessageData data) {
        boolean result = true;

        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put("unread", data.getUnread());
            contentValues.put("userId", data.getUserId());
            contentValues.put("otherId", data.getOtherId());
            contentValues.put("roomName", data.getRoomName());
            contentValues.put("content", data.getContent());
            contentValues.put("time", data.getTime());

            result = db.insert("message", null, contentValues) > 0;

        } catch (Exception e) {
            result = false;
        }
        return result;
    }


    //채팅내용 가져오기
    public ArrayList<MessageData> messageList(String roomName) {

        ArrayList<MessageData> messageList = new ArrayList<>();

        try {

            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.rawQuery
                    ("Select * from message where roomName = '" + roomName + "'", new String[] {});


            while(cursor.moveToNext()) {

                MessageData data = new MessageData().setNum(cursor.getInt(0))
                                                    .setUnread(cursor.getInt(1))
                                                    .setUserId(cursor.getString(2))
                                                    .setOtherId(cursor.getString(3))
                                                    .setRoomName(cursor.getString(4))
                                                    .setContent(cursor.getString(5))
                                                    .setTime(cursor.getString(6));

                messageList.add(data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return messageList;
    }


    //채팅 내용 삭제
    public int deleteMessage(String roomName) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.delete("message", "roomName=?",new String[]{roomName});
    }

}
