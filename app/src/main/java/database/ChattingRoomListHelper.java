package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import dto.ChattingRoomListDto;

public class ChattingRoomListHelper extends SQLiteOpenHelper {


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


    public void onOpen(SQLiteDatabase db) {

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

    //채팅방 만들기
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


    //채팅방 찾기
    public ChattingRoomListDto findRoom(String otherId) {

        //ChattingRoomListDto roomListDto = new ChattingRoomListDto(); -> 채팅방을 못 찾아서 수정
        ChattingRoomListDto roomListDto = null;

        try {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.rawQuery
                    ("Select roomName from chattingRoomList where otherId = '" + otherId +"'", new String[] {});


            if(cursor.moveToNext()) {
                roomListDto = new ChattingRoomListDto();
                roomListDto.setRoomName(cursor.getString(0));

                System.out.println("if 문 들어옴======");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return roomListDto;

    }



    //채팅방 목록
    public ArrayList<ChattingRoomListDto> roomList(String id) {

        ArrayList<ChattingRoomListDto> roomList = new ArrayList<>();

        try {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.rawQuery
                    ("Select * from chattingRoomList where myId = '" + id + "' order by time desc", new String[] {});

            while(cursor.moveToNext()) {

                ChattingRoomListDto roomListDto = new ChattingRoomListDto();

                roomListDto.setNum(cursor.getInt(0));
                roomListDto.setRoomName(cursor.getString(1));
                roomListDto.setMyId(cursor.getString(2));
                roomListDto.setOtherId(cursor.getString(3));
                roomListDto.setLastContent(cursor.getString(4));
                roomListDto.setProfileImage(cursor.getString(5));
                roomListDto.setTime(cursor.getString(6));


                roomList.add(roomListDto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return roomList;
    }


    //마지막 메세지, 시간 업데이트
    public boolean update(String lastContent, String time, String roomName) {

        try {
            SQLiteDatabase db = this.getReadableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put("lastContent", lastContent);
            contentValues.put("time", time);
            contentValues.put("roomName", roomName);

            db.update("chattingRoomList", contentValues, "roomName=?",
                    new String[]{String.valueOf(  roomName  )});

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }


    //채팅방 나가기
    public int deleteRoom(int num) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.delete("chattingRoomList", "_no=?",new String[]{Integer.toString(num)});
    }

}
