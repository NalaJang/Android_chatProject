package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MessageHelper extends SQLiteOpenHelper {

    private static MessageHelper sInstance;

    private static final int DB_VERSION = 1;
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
        String body = "create table if not exists message (" +
                "userId text, otherId text, roomPosition text, content text, time text)";

        //실행
        db.execSQL(body);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS MESSAGE");
        onCreate(db);
    }

}
