package com.example.chatproject5;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import database.UserHelper;
import dto.UserDto;

/******************************************
              아이디 비밀번호 찾기
 ******************************************/
public class Search_userInfoActivity extends AppCompatActivity {

    UserHelper db;
    EditText userName, userId, userPhone, userPhone2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user_info);

        db = new UserHelper(this);

        userName = findViewById(R.id.userName_search);
        userId = findViewById(R.id.userId_search);
        userPhone = findViewById(R.id.userPhone_search);
        userPhone2 = findViewById(R.id.userPhone2_search);

        Button button = findViewById(R.id.button_search);
        Button button2 = findViewById(R.id.button2_search);


        /******************************************
                        아이디 찾기
         ******************************************/
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = userName.getText().toString().trim();
                String phone = userPhone.getText().toString().trim();

                UserDto userDao = db.searchId(name, phone);

                //공란일 경우
                if(name.equals("") || phone.equals("")) {

                    Toast.makeText(Search_userInfoActivity.this, "정보를 입력해 주세요.", Toast.LENGTH_SHORT).show();


                //정보가 존재 할 경우
                } else if (userDao != null) {

                    userName.setText("");
                    userPhone.setText("");

                    AlertDialog.Builder builder = new AlertDialog.Builder(Search_userInfoActivity.this);

                    builder.setTitle("정보확인");
                    builder.setMessage("회원님의 아이디는 '" + userDao.getId() + "' 입니다.");

                    builder.setPositiveButton("로그인 하러가기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(Search_userInfoActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        }
                    });

                    builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });

                    builder.show();

                //정보가 없을 경우
                } else {

                    invalid();

                }
            }
        }); //end button


        /******************************************
                        비밀번호 찾기
         ******************************************/
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = userId.getText().toString();
                String phone = userPhone2.getText().toString();

                UserDto userDao = db.searchPw(id, phone);

                //공란일 경우
                if(id.equals("") || phone.equals("")) {

                    Toast.makeText(Search_userInfoActivity.this, "정보를 입력해 주세요.", Toast.LENGTH_SHORT).show();


                //정보가 존재 할 경우
                } else if (userDao != null) {

                    userId.setText("");
                    userPhone2.setText("");

                    AlertDialog.Builder builder = new AlertDialog.Builder(Search_userInfoActivity.this);

                    builder.setTitle("정보확인");
                    builder.setMessage("회원님의 비밀번호는 '" + userDao.getPw() + "' 입니다.");

                    builder.setPositiveButton("로그인 하러가기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(Search_userInfoActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        }
                    });

                    builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });

                    builder.show();

                //정보가 없을 경우
                } else {

                   invalid();

                }

            }
        }); //end button2
    }   //end onCreate


    public void invalid() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Search_userInfoActivity.this);

        builder.setTitle(R.string.info);
        builder.setMessage("가입된 정보가 없습니다.");

        builder.setPositiveButton(R.string.close, null);
        builder.show();
    }
}