package com.example.chatproject5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import adapter.OrderAdapter;
import chat.Constants;
import dto.OrderDto;

/******************************************
                   주문배송
 ******************************************/
public class MyOrderListActivity extends AppCompatActivity {


    private OrderAdapter adapter;
    private RecyclerView recyclerView;

    private String userId_db;
    private final Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_order_list);

        setTitle("주문배송");

        //정보 받기
        Intent intent = getIntent();
        userId_db = intent.getStringExtra("userId_db");

        adapter = new OrderAdapter(getApplicationContext());

        recyclerView = findViewById(R.id.recyclerView_order);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);

        // E/RecyclerView: No adapter attached; skipping layout 에러
        //  ↓ println() 에도 넣었지만 여기에도 선언
        recyclerView.setAdapter(adapter);

        //DB 에서 주문 목록가져오기
        final String urlStr = Constants.SERVER_URL + "orderList.do";

        new Thread(new Runnable() {
            @Override
            public void run() {

                myOrderList(urlStr);

            }
        }).start();

    }   //end onCreate

    public void myOrderList(String urlStr) {
        StringBuilder output = new StringBuilder();

        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if(conn != null) {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);

                OutputStream outputStream = conn.getOutputStream();

                //값 넣어주기
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

        setOrderList(output.toString());
    }

    public void setOrderList(String str) {
        Document doc = Jsoup.parse(str);
        Elements pname = doc.select("ol > li.pname");
        Elements image = doc.select("ol > li.image");
        Elements quantity = doc.select("ol > li.quantity");
        Elements content = doc.select("ol > li.content");
        Elements price = doc.select("ol > li.price");
        Elements result = doc.select("ol > li.result");
        Elements indate = doc.select("ol > li.indate");


        for(int i = 0; i < pname.size(); i++) {

            OrderDto orderDto = new OrderDto();
            orderDto.setPname(pname.get(i).text());
            orderDto.setImage(image.get(i).text());
            orderDto.setQuantity(Integer.parseInt(quantity.get(i).text()));
            orderDto.setSizeAndColor(content.get(i).text());
            orderDto.setPrice(Integer.parseInt(price.get(i).text()));
            orderDto.setResult(result.get(i).text());
            orderDto.setIndate(indate.get(i).text());

            adapter.addItem(orderDto);

            println();
        }
    }


    public void println() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

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