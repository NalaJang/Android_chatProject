package com.example.chatproject5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

public class QnaContentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qna_content);

        TextView result_text = findViewById(R.id.result_content);
        TextView indate_text = findViewById(R.id.indate_content);
        TextView subject_text = findViewById(R.id.subject_content);
        TextView title_text = findViewById(R.id.title_content);
        TextView content_text = findViewById(R.id.content_content);


        //정보 받기
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        String subject = intent.getStringExtra("subject");
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        String indate = intent.getStringExtra("indate");
        String result = intent.getStringExtra("result");

        result_text.setText(email);
        indate_text.setText(indate);
        subject_text.setText(subject);
        title_text.setText(title);
        content_text.setText(content);
        result_text.setText(result);

        switch (subject) {
            case "1" :
                subject_text.setText("일반문의");
                break;

            case "2" :
                subject_text.setText("계정문의");
                break;

            case "3" :
                subject_text.setText("환불문의");
                break;
        }


        switch (result) {
            case "1" :
                result_text.setText("답변 준비중");

                break;

            case "2" :
                result_text.setText("답변 완료");
                result_text.setTextColor(Color.parseColor("#D81B60"));
                break;
        }
    }
}