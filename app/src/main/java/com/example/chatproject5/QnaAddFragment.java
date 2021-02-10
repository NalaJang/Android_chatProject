package com.example.chatproject5;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import chat.Constants;

/******************************************
                  문의하기
 ******************************************/
public class QnaAddFragment extends Fragment {

    private CheckBox check;
    private EditText title_edit, content_edit, email_edit;

    private String userId_db;
    private String[] subjects = {"일반문의", "계정문의", "환불문의"};
    private String subject_db;

    private final Handler handler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_qna_add, container, false);

        //정보 받기
        Bundle bundle = this.getArguments();
        userId_db = bundle.getString("userId_db");



        check = rootView.findViewById(R.id.check_qna);
        title_edit = rootView.findViewById(R.id.title_qna);
        content_edit = rootView.findViewById(R.id.content_qna);
        email_edit = rootView.findViewById(R.id.email_qna);

        Button cancelButton = rootView.findViewById(R.id.cancel_qna);
        Button insertButton = rootView.findViewById(R.id.insert_qna);



        //spinner 에 값 넣어주기
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, subjects);
        Spinner spinner = rootView.findViewById(R.id.spinner_qna);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {

                    case 0 :
                        subject_db = "1";
                        break;

                    case 1 :
                        subject_db = "2";
                        break;

                    case 2 :
                        subject_db = "3";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        //취소 버튼 -> 입력값 초기화
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                title_edit.setText("");
                content_edit.setText("");
                email_edit.setText("");
            }
        });



        //문의 접수 버튼
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!check.isChecked()) {

                    Toast.makeText(getContext(), "약관을 읽어보시고 동의해 주세요.", Toast.LENGTH_SHORT).show();

                } else if(title_edit.getText().toString().equals("")) {

                    Toast.makeText(getContext(), "제목을 입력해 주세요.", Toast.LENGTH_SHORT).show();

                } else if(content_edit.getText().toString().equals("")) {

                    Toast.makeText(getContext(), "내용을 입력해 주세요.", Toast.LENGTH_SHORT).show();

                } else if(email_edit.getText().toString().equals("")) {

                    Toast.makeText(getContext(), "답변 받으실 이메일 주소를 입력해 주세요.", Toast.LENGTH_SHORT).show();

                } else {

                    final String urlStr = Constants.SERVER_URL + "qnaInsert.do";

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            qnaInsert(urlStr);

                        }
                    }).start();
                }
            }
        });
        return rootView;
    }

    //DB에 문의 등록
    public void qnaInsert(String urlStr) {

        String title_db = title_edit.getText().toString();
        String content_db = content_edit.getText().toString();
        String email_db = email_edit.getText().toString();

        StringBuilder output = new StringBuilder();

        try {

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if(conn != null) {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);

                OutputStream outputStream = conn.getOutputStream();
                String params = "subject=" + subject_db
                            + "&title=" + title_db
                            + "&content=" + content_db
                            + "&id=" + userId_db
                            + "&email=" + email_db;

                outputStream.write(params.getBytes());  //connection 으로부터 outputStream 을 얻어 params 를 byte 배열로 할당

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;

                while(true) {
                    line = reader.readLine();

                    if(line == null) {
                        break;
                    }

                    output.append(line + "\n");
                }
                reader.close();
                conn.disconnect();

            }

        } catch (Exception e) {
            e.printStackTrace();
            println2();
        }
        setQnaInsert(output.toString());
    }


    public void setQnaInsert(String str) {

        Document doc = Jsoup.parse(str);
        Elements result = doc.select("p.result");

        for(int i = 0; i < result.size(); i++) {
            if(result.get(0).text().equals("문의성공")) {

                println();

            } else {

                println2();
            }
        }
    }

    public void println() {
        handler.post(new Runnable() {
            @Override
            public void run() {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.info);
                builder.setMessage("문의가 접수되었습니다.");
                builder.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                        title_edit.setText("");
                        content_edit.setText("");
                        email_edit.setText("");
                    }
                });

                builder.show();
            }
        });
    }

    public void println2() {
        handler.post(new Runnable() {
            @Override
            public void run() {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.info);
                builder.setMessage("오류\n 시스템 점검 중\n");
                builder.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                        title_edit.setText("");
                        content_edit.setText("");
                        email_edit.setText("");
                    }
                });

                builder.show();

            }
        });
    }

}