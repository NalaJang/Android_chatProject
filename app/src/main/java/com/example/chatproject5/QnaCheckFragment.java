package com.example.chatproject5;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import dto.QnaDto;


public class QnaCheckFragment extends Fragment {

    String userId_db;
    ListView listview;
    TextView textView;

    Handler handler = new Handler();
    ArrayList<QnaDto> qnaList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_qna_check, container, false);

        //정보 받기
        Bundle bundle = this.getArguments();
        userId_db = bundle.getString("userId_db");

        listview = rootView.findViewById(R.id.listView_qna);
        textView = rootView.findViewById(R.id.textView_qna);

        qnaList = new ArrayList<>();
//        qnaList.add("list");
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, qnaList);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(getContext(), "이벤트", Toast.LENGTH_SHORT).show();
            }
        });


        //문의 내역 가져오기
        final String urlStr = "http://192.168.0.17:8080/webapp/webServer/qnaList.do";

        new Thread(new Runnable() {
            @Override
            public void run() {

                qnaList(urlStr);

            }
        }).start();

        return rootView;
    }

    public void qnaList(String urlStr) {

        StringBuilder output = new StringBuilder();

//        Bundle bundle = this.getArguments();
//        userId_db = bundle.getString("userId_db");

        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if(conn != null) {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);

                OutputStream outputStream = conn.getOutputStream();
                String params = "id=" + userId_db;
                outputStream.write(params.getBytes());

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = null;

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

        }
        setQnaList(output.toString());
        Log.d("output ===", output.toString());

    }

    public void setQnaList(String str) {

        Document doc = Jsoup.parse(str);
        Elements result = doc.select("p.result");
        Elements email = doc.select("ol > li.email");
        Elements subject = doc.select("ol > li.subject");
        Elements title = doc.select("ol > li.title");
        Elements content = doc.select("ol > li.content");
        Elements indate = doc.select("ol > li.indate");

        for(int i = 0; i < result.size(); i++) {

            if(result.get(0).text().equals("문의없음")) {

                println1();


            } else if(result.get(0).text().equals("문의있음")) {

                for(int j = 0; j < title.size(); j++) {

                    QnaDto qna = new QnaDto()
//                            .setEmail(email.get(j).text())
//                            .setSubject(subject.get(j).text())
                            .setTitle(title.get(j).text());
//                            .setContent(content.get(j).text())
//                            .setIndate(indate.get(j).text());

                    qnaList.add(qna);


                    Log.d("===", title.get(j).text());

                   println2();
                }
            }
        }
    }   //end setQnaList

    //"문의없음" 에 대한 출력 결과
    public void println1() {
        handler.post(new Runnable() {
            @Override
            public void run() {

                listview.setVisibility(View.GONE);
            }
        });
    }

    //"문의있음"에 대한 출력 결과
    public void println2() {
        handler.post(new Runnable() {
            @Override
            public void run() {

                listview.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
            }
        });
    }
}