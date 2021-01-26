package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import dto.ChattingRoomListDto;
import dto.MessageData;

public class ChattingRoomListHelper extends SQLiteOpenHelper {

    private static ChattingRoomListHelper sInstance;

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "ChattingRoomList.db";


    public ChattingRoomListHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists chattingRoomList (" +
                "_no integer PRIMARY KEY AUTOINCREMENT, " +
                "roomName text, myId text, otherId text, lastContent text, profileImage text, time text)";

        //실행
        db.execSQL(sql);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS chattingRoomList");
        onCreate(db);
    }





    /*
    private String roomName;
    private String myId;
    private String otherId;
    private String lastContent;
    private String profileImage;
    private String time;
     */


    public boolean insert(ChattingRoomListDto roomListDto) {
        boolean result = true;

        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put("roomName", roomListDto.getRoomName());
            contentValues.put("myId", roomListDto.getMyId());
            contentValues.put("otherId", roomListDto.getOtherId());
            contentValues.put("lastContent", roomListDto.getLastContent());
            contentValues.put("profileImage", roomListDto.getProfileImage());
            contentValues.put("time", roomListDto.getTime());

            result = db.insert("chattingRoomList", null, contentValues) > 0;

        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    public ChattingRoomListDto roomList(String id) {
        ChattingRoomListDto roomListDto = null;

        try {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.rawQuery("Select * from chattingRoomList where id = ? order by time desc", new String[] {String.valueOf(id)});

            while(cursor.moveToNext()) {
                roomListDto.setRoomName(cursor.getString(0));
                roomListDto.setOtherId(cursor.getString(1));
                roomListDto.setProfileImage(cursor.getString(2));
                roomListDto.setLastContent(cursor.getString(3));
                roomListDto.setTime(cursor.getString(4));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return roomListDto;
    }


}
