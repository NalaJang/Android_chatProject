package com.example.chatproject5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import adapter.PointAdapter;
import dto.PointDto;

public class MyPointActivity extends AppCompatActivity {

    Intent intent;
    ArrayList<PointDto> pointList =  new ArrayList<>();

    String userId_db;
    TextView total_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_my_point);

        setTitle("포인트");

        //정보받기
        intent = getIntent();
        userId_db = intent.getStringExtra("userId_db");

        total_text = findViewById(R.id.total_point);
        RecyclerView recyclerView = findViewById(R.id.recyclerView_point);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);

        PointAdapter adapter = new PointAdapter();
        adapter.addItem(new PointDto("111", "스웨트셔츠",100,"2021-01-10"));
        adapter.addItem(new PointDto("111", "스커트",100,"2021-01-10"));
        recyclerView.setAdapter(adapter);

        final String urlStr = "http://192.168.0.17:8080/webapp/webServer/pointList.do";

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
        setPointList(output.toString());
        Log.d("setPointList", output.toString());
    }

    public void setPointList(String str) {
        Document doc = Jsoup.parse(str);
        Elements result = doc.select("p.result");
        Elements product = doc.select("ol > li.product");
        Elements point = doc.select("ol > li.point");
        Elements indate = doc.select("ol > li.indate");

        int total = 0;

        for(int i = 0; i < result.size(); i++) {
            if(result.get(0).text().equals("내역있음")) {
                for(int j = 0; j < indate.size(); j++) {

                    PointDto pointDto = new PointDto();

                    pointDto.setProduct(product.get(j).text());
                    pointDto.setPoint(Integer.parseInt(point.get(j).text()));
                    pointDto.setIndate(indate.get(j).text());

                    pointList.add(pointDto);

                    total  += Integer.parseInt(point.get(j).text());
                    total_text.setText(Integer.toString(total) + " point");
                }

            }
        }
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