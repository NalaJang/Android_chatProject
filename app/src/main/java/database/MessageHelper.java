package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import dto.MessageData;

public class MessageHelper extends SQLiteOpenHelper {

    private static MessageHelper sInstance;

    private static final int DB_VERSION = 2;
    private static final String DB_NAME = "Message.db";


    public MessageHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    public static synchronized MessageHelper getInstance(Context context) {
        // 액티비티의 context가 메모리 릭(leak)을 발생할 수 있으므로
        // application context를 사용하는 것이 좋다
        if (sInstance == null) {
            sInstance = new MessageHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists message (" +
                "_no integer PRIMARY KEY AUTOINCREMENT, userId text, otherId text, roomName text, content text, time text)";

        //실행
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS MESSAGE");
        onCreate(db);
    }
    /*
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

}
