package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MessageHelper2 extends SQLiteOpenHelper {

    private static MessageHelper2 sInstance;

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "Message2.db";


    public MessageHelper2(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    public static synchronized MessageHelper2 getInstance(Context context) {
        // 액티비티의 context가 메모리 릭(leak)을 발생할 수 있으므로
        // application context를 사용하는 것이 좋다
        if (sInstance == null) {
            sInstance = new MessageHelper2(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String body = "create table if not exists message2 (" +
                "_num integer PRIMARY KEY AUTOINCREMENT, userId text, otherId text, roomPosition text, content text, time text)";
/*
private String signal;
    private String toId;
    private String fromId;
    private String message;
    private String time;
    private String roomId;
    private String photo;

 */
        //실행
        db.execSQL(body);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS MESSAGE2");
        onCreate(db);
    }

}
