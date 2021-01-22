package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import dto.UserDto;

public class UserHelper extends SQLiteOpenHelper {

    private static final  String DB_NAME = "User.db";
    private static final int DB_VERSION = 2;


    //디버깅
    public void println(String data) {
        Log.d("DatabaseHelper", data);
    }

    public UserHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        println("helper 의 onCreate 호출");

        String user = "create table if not exists user (" +
                "name text," +
                " userId text PRIMARY KEY," +
                " pw text," +
                " email text, " +
                "phone text," +
                " profilePhoto integer," +
                " point integer)";

        //실행
        db.execSQL(user);
    }

    // DB 스키마가 변경될 때 여기서 데이터를 백업하고
    // 테이블을 삭제 후 재생성 및 데이터 복원 등을 한다
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS USER");
        onCreate(db);
    }


    //가입하기
    public boolean insert(UserDto userDto) {
        boolean result = true;

        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put("name", userDto.getName());
            contentValues.put("userId", userDto.getId());
            contentValues.put("pw", userDto.getPw());
            contentValues.put("email", userDto.getEmail());
            contentValues.put("phone", userDto.getPhone());
            contentValues.put("profilePhoto", "");
            contentValues.put("point", 3000);

            result = db.insert("user", null, contentValues) > 0;

        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    //이미 존재하는 정보인지 확인
    public UserDto checkName(String name) {
        UserDto userDto = null;

        try {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.rawQuery("Select * from user where name = ?", new String[] {name});

            if(cursor.moveToFirst()) {
                userDto = new UserDto();

                userDto.setName(cursor.getString(0));
                userDto.setId(cursor.getString(1));
                userDto.setPw(cursor.getString(2));
                userDto.setEmail(cursor.getString(3));
                userDto.setPhone(cursor.getString(4));
                userDto.setProfilePhoto(cursor.getInt(5));
                userDto.setPoint(cursor.getInt(6));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return userDto;
    }

    //로그인
    public UserDto login(String id, String pw) {
        UserDto userDto = null;

        try {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.rawQuery("Select * from user where userId = ? and pw = ?", new String[] {id, pw});

            if(cursor.moveToFirst()) {
                userDto = new UserDto();

                userDto.setName(cursor.getString(0));
                userDto.setId(cursor.getString(1));
                userDto.setPw(cursor.getString(2));
                userDto.setEmail(cursor.getString(3));
                userDto.setPhone(cursor.getString(4));
                userDto.setProfilePhoto(cursor.getInt(5));
                userDto.setPoint(cursor.getInt(6));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return userDto;
    }

    //아이디 찾기
    public UserDto searchId(String name, String phone) {

        UserDto userDto = null;

        try {

            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.rawQuery("Select userId from user where name =? and phone =?", new String[] {name, phone});

            if(cursor.moveToFirst()) {
                userDto = new UserDto();

                userDto.setId(cursor.getString(0));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return userDto;
    }

    //비밀번호 찾기
    public UserDto searchPw(String id, String phone) {
        UserDto userDto = null;

        try {

            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.rawQuery("Select pw from user where userId =? and phone =?", new String[] {id, phone});

            if(cursor.moveToFirst()) {
                userDto = new UserDto();

                userDto.setPw(cursor.getString(0));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return userDto;
    }

    public boolean update(UserDto userDto) {
        boolean result = true;

        try {
            SQLiteDatabase db = getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put("pw", userDto.getPw());
            contentValues.put("email", userDto.getEmail());
            contentValues.put("phone", userDto.getPhone());
            contentValues.put("profilePhoto", "");

            result = db.update("user", contentValues, "userId =?",
                    new String[] { String.valueOf(userDto.getId()) }) > 0;

        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    public UserDto findId(String id) {
        UserDto userDto = null;

        try {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.rawQuery("Select * from user where userId = ?", new String[] {String.valueOf(id)});

            if(cursor.moveToFirst()) {
                userDto = new UserDto();

                userDto.setName(cursor.getString(0));
                userDto.setId(cursor.getString(1));
                userDto.setPw(cursor.getString(2));
                userDto.setEmail(cursor.getString(3));
                userDto.setPhone(cursor.getString(4));
                userDto.setProfilePhoto(cursor.getInt(5));
                userDto.setPoint(cursor.getInt(6));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return userDto;
    }
}
