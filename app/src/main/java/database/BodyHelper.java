package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import dto.BodyDto;

public class BodyHelper extends SQLiteOpenHelper {

    private static final  String DB_NAME = "body.db";
    private static final int DB_VERSION = 1;

    public BodyHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

//        String body = "create table if not exists body (" +
//                "id text PRIMARY KEY, shoulder text, arm text, bust text, waist text, totalUpperBody text, " +
//                "hip text, thigh text, calf text, totalLowerBody text, " +
//                "foot text, height text, weight text)";

        String body = "create table if not exists body (" +
                "id text PRIMARY KEY, shoulder text, weight text)";

        //실행
        db.execSQL(body);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS BODY");
        onCreate(db);
    }

    //바디 프로필 등록
    public boolean insertBody(BodyDto bodyDto) {
        boolean result = true;

        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put("id", bodyDto.getId());
            contentValues.put("shoulder", bodyDto.getShoulder());
            contentValues.put("weight", bodyDto.getWeight());

            result = db.insert("BODY", null, contentValues) > 0;

        } catch (Exception e) {
            result = false;
        }
        return result;
    }
}
