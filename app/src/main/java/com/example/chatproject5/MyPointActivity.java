package com.example.chatproject5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import adapter.PointAdapter;
import chat.Constants;
import dto.PointDto;

public class MyPointActivity extends AppCompatActivity {

    //    private ArrayList<PointDto> pointList =  new ArrayList<>();

    private String userId_db;
    private TextView total_text;

    private RecyclerView recyclerView;
    private PointAdapter adapter;

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_my_point);

        setTitle("포인트");

        //정보받기
        Intent intent = getIntent();
        userId_db = intent.getStringExtra("userId_db");

        total_text = findViewById(R.id.total_point);
        recyclerView = findViewById(R.id.recyclerView_point);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);

        adapter = new PointAdapter();
        recyclerView.setAdapter(adapter);

        final String urlStr = Constants.SERVER_URL + "pointList.do";

        new Thread(new Runnable() {
            @Override
            public void run() {

                point(urlStr);

            }
        }).start(); //스레드 시작
    }

    public void point(String urlStr) {
        StringBuilder output = new StringBuilder();


        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            if(conn != null) {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);

                OutputStream outputStream = conn.getOutputStream();
                String params = "id=" + userId_db;
                outputStream.write(params.getBytes());

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
        }

        setPointList(output.toString());
//        recyclerView.setAdapter(adapter);


    }   //end point



    public void setPointList(String str) {
        Document doc = Jsoup.parse(str);
//        Elements result = doc.select("p.result");
        Elements product = doc.select("ol > li.pname");
        Elements point = doc.select("ol > li.point");
        Elements indate = doc.select("ol > li.indate");

        int total = 0;

//        Log.d("resultSize  ===>", Integer.toString(result.size())); //-> 1

//        for(int i = 0; i < result.size(); i++) {
//            if(result.get(0).text().equals("내역있음")) {
                for(int j = 0; j < indate.size(); j++) {

                    PointDto pointDto = new PointDto();

                    pointDto.setProduct(product.get(j).text());
                    pointDto.setPoint(Integer.parseInt(point.get(j).text()));
                    pointDto.setIndate(indate.get(j).text());

//                    pointList.add(pointDto);
                    adapter.addItem(pointDto);


                    total  += Integer.parseInt(point.get(j).text());
                    total_text.setText(total + " point");
                }

//            }
//        }

//        recyclerView.setAdapter(adapter);
        println();


    }   //end setPointList

    public void println() {
        handler.post(new Runnable() {
            @Override
            public void run() {

                recyclerView.setAdapter(adapter);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar 의 back 키 눌렀을 때 동작

                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}