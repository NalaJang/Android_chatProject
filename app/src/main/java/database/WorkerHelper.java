package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import dto.CoordinatorDto;

public class WorkerHelper extends SQLiteOpenHelper {

    private static final  String DB_NAME = "Worker.db";
    private static final int DB_VERSION = 2;


    //디버깅
    public void println(String data) {
        Log.d("DatabaseHelper", data);
    }

    public WorkerHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        println("helper 의 onCreate 호출");


        String coordinator = "create table if not exists coordinator (" +
                "name text," +
                " workerId text PRIMARY KEY," +
                " pw text," +
                " email text," +
                " profilePhoto integer)";

        //실행
        db.execSQL(coordinator);
    }

    // DB 스키마가 변경될 때 여기서 데이터를 백업하고
    // 테이블을 삭제 후 재생성 및 데이터 복원 등을 한다
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS coordinator");
        onCreate(db);
    }


    //가입하기
    public boolean insert(CoordinatorDto coordinator) {
        boolean result = true;

        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put("name", coordinator.getName());
            contentValues.put("workerId", coordinator.getWorkerId());
            contentValues.put("pw", coordinator.getPw());
            contentValues.put("email", coordinator.getEmail());
            contentValues.put("profilePhoto", "");

            result = db.insert("coordinator", null, contentValues) > 0;

        } catch (Exception e) {
            result = false;
        }
        return result;
    }


    //로그인
    public CoordinatorDto login(String id, String pw) {
        CoordinatorDto coordinatorDto = null;

        try {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.rawQuery("Select * from coordinatorDto where workerId = ? and pw = ?", new String[] {id, pw});

            if(cursor.moveToFirst()) {
                coordinatorDto = new CoordinatorDto();

                coordinatorDto.setName(cursor.getString(0));
                coordinatorDto.setWorkerId(cursor.getString(1));
                coordinatorDto.setPw(cursor.getString(2));
                coordinatorDto.setEmail(cursor.getString(3));
                coordinatorDto.setProfilePhoto(cursor.getInt(4));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return coordinatorDto;
    }


}
