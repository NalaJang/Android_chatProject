package com.example.chatproject5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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

public class WorkerProfileActivity extends AppCompatActivity {

    TextView workerId, workerContent;
    Handler handler = new Handler();

    Intent intent;
    String userId_db, workerId_db;
    int num_db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_worker_profile);

        workerId = findViewById(R.id.workerId_profile);
        workerContent = findViewById(R.id.workerContent_profile);

        //정보 받기
        intent = getIntent();
        workerId_db = intent.getStringExtra("workerId_db");
        workerId.setText(workerId_db);


        //등록 클릭
        Button addCoordinator = findViewById(R.id.addCoordinator_button);
        addCoordinator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String urlStr = "http://192.168.0.17:8080/webapp/webServer/selectWorker.do";

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        addCoordinator(urlStr);

                    }
                }).start();
            }
        });
    }   //end onCreate

    public void close_workerProfile(View v) {
        finish();
    }

    public void addCoordinator(String urlStr) {
        StringBuilder output = new StringBuilder();

        intent = getIntent();
        userId_db = intent.getStringExtra("userId_db");
        workerId_db = intent.getStringExtra("workerId_db");

        Log.d("workerId = ", workerId_db);/**************** 주석 *********************/

//        num_db = intent.getStringExtra("position");
        num_db = intent.getIntExtra("position", 0);


        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if(conn != null) {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);

                OutputStream outputStream = conn.getOutputStream();
                String params = "id=" + userId_db + "&counselorId=" + workerId_db;
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
        setSelectWorker(output.toString());
        println();
    }

    public void setSelectWorker(String urlStr) {

        Document doc = Jsoup.parse(urlStr);
        Elements result = doc.select("p.result");
        Elements userId_db = doc.select("ol > li.id");
        Elements workerId_db = doc.select("ol > li.workerId");

        for(int i = 0; i < result.size(); i++) {

            if(result.get(0).text().equals("등록성공")) {
                intent.putExtra("workerId_db", workerId_db);
                intent.putExtra("userId_db", userId_db);

            }
        }

    }

    //출력 메소드
    public  void println() {
        handler.post(new Runnable() {
            @Override
            public void run() {

                Toast.makeText(getApplicationContext(), "등록성공", Toast.LENGTH_SHORT).show();
            }
        });
    }
}