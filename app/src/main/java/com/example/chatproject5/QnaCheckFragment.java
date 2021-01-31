package com.example.chatproject5;

import android.content.Intent;
import android.os.Bundle;

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

import adapter.QnaCheckAdapter;
import dto.QnaDto;


public class QnaCheckFragment extends Fragment {

    private String userId_db;
    private ListView listview;
    private TextView textView;

//    private ArrayAdapter adapter;
    private QnaCheckAdapter adapter2;
    private Handler handler = new Handler();
    private ArrayList<QnaDto> qnaList;
//    private QnaDto qnaDto;

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
//        qnaDto = new QnaDto();
//        adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, qnaList);

        adapter2 = new QnaCheckAdapter(qnaList, getContext());

        /*
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String clickedItem = (String)parent.getAdapter().getItem(position);

                Toast.makeText(getContext(), "이벤트" + clickedItem + position, Toast.LENGTH_SHORT).show();

            }
        });

         */


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
        Elements num = doc.select("ol > li.num");
        Elements title = doc.select("ol > li.title");
        Elements indate = doc.select("ol > li.indate");
        Elements replyResult = doc.select("ol > li.replyResult");

        for(int i = 0; i < result.size(); i++) {

            if(result.get(0).text().equals("문의없음")) {

                println1();


            } else if(result.get(0).text().equals("문의있음")) {

                for(int j = 0; j < num.size(); j++) {

                    QnaDto qna = new QnaDto()
                            .setNum(num.get(j).text())
                            .setTitle(title.get(j).text())
                            .setIndate(indate.get(j).text())
                            .setResult(replyResult.get(j).text());

                    //qnaList.add(qna);
                    //adapter.add(qna);
                    System.out.println("qnaCheck, num : " + num.get(j).text());
                    adapter2.addItem(qna);    //*****

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

                listview.setAdapter(adapter2);
                adapter2.notifyDataSetChanged();

                listview.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
            }
        });
    }
}