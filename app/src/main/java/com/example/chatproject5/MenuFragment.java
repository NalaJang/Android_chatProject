package com.example.chatproject5;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MenuFragment extends Fragment {

    Bundle bundle;
    Intent intent;

    TextView userId, userContent;

    String userId_db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_menu, container, false);

        //타이틀 변경
        ActionBar actionBar = ((EntranceActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle("더보기");
        actionBar.setDisplayHomeAsUpEnabled(false); //뒤로 가기 버튼 비활성화

        //정보 받기
        bundle = this.getArguments();
        userId_db = bundle.getString("userId_db");
        String userName_db = bundle.getString("userName_db");
        String userPw_db = bundle.getString("userPw_db");
        String userEmail_db = bundle.getString("userEmail_db");
        String userPhone_db = bundle.getString("userPhone_db");
        String userContent_db = bundle.getString("userContent_db");
        String userProfilePhoto_db = bundle.getString("userProfilePhoto_db");
        String userPoint_db = bundle.getString("userPoint_db");

        userId = rootView.findViewById(R.id.userId_menu);
        userContent = rootView.findViewById(R.id.userContent_menu);
        userId.setText(userId_db);
        userContent.setText(userContent_db);

        Button myInfoButton = rootView.findViewById(R.id.myInfo_button);
        Button myOrderButton = rootView.findViewById(R.id.order_menu);
        Button myBodyButton = rootView.findViewById(R.id.body_menu);
        Button myPointButton = rootView.findViewById(R.id.point_menu);
        Button qnaButton = rootView.findViewById(R.id.qna_menu);



        /***************** 내 정보 *****************/
        myInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = new Intent(getContext(), MyInfoActivity.class);
                intent.putExtra("userId_db", userId_db);
                intent.putExtra("userContent_db", userContent_db);
                intent.putExtra("userName_db", userName_db);
                intent.putExtra("userPw_db", userPw_db);
                intent.putExtra("userEmail_db", userEmail_db);
                intent.putExtra("userPhone_db", userPhone_db);

                startActivity(intent);

            }
        });

        /***************** 주문 배송 *****************/
        myOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = new Intent(getContext(), MyOrderListActivity.class);
                intent.putExtra("userId_db", userId_db);

                startActivity(intent);
            }
        });



        /***************** 내 체형 *****************/
        myBodyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = new Intent(getContext(), MyBodyActivity.class);
                intent.putExtra("userId_db", userId_db);

                final String urlStr = "http://192.168.0.17:8080/webapp/webServer/mybodyList.do";

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        myBodyList(urlStr);
                    }
                }).start();

                startActivity(intent);
            }
        });



        /***************** 포인트 *****************/
        myPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = new Intent(getContext(), MyPointActivity.class);
                intent.putExtra("userId_db", userId_db);
                startActivity(intent);
            }
        });


        /***************** 문의 내역 *****************/
        qnaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = new Intent(getContext(), QnaActivity.class);
                intent.putExtra("userId_db", userId_db);
                startActivity(intent);
            }
        });
        return rootView;
    }

    public void myBodyList(String urlStr) {
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
            } else {

                Log.i("통신 결과", conn.getResponseCode()+"에러");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        setBodyProfile(output.toString());
        Log.d("====", output.toString());
        Log.d("id", userId_db);
    }

    public void setBodyProfile(String str) {
        Document doc = Jsoup.parse(str);
        Elements result = doc.select("p.result");
        Elements shoulder_db = doc.select("ol > li.shoulder");
        Elements arm = doc.select("ol > li.arm");
        Elements bust = doc.select("ol > li.bust");
        Elements waist = doc.select("ol > li.waist");
        Elements totalUpperBody = doc.select("ol > li.totalUpperBody");
        Elements hip = doc.select("ol > li.hip");
        Elements thigh = doc.select("ol > li.thigh");
        Elements calf = doc.select("ol > li.calf");
        Elements totalLowerBody = doc.select("ol > li.totalLowerBody");
        Elements foot = doc.select("ol > li.foot");
        Elements height = doc.select("ol > li.height");
        Elements weight = doc.select("ol > li.weight");

        for(int i = 0; i < result.size(); i++) {
            if(result.get(0).text().equals("성공")) {

//                intent.putExtra("shoulder_db", shoulder_db.get(0).text());
                Log.d("shoulder 값 === ", shoulder_db.get(0).text());

            } else if(result.get(0).text().equals("실패")) {

            }
        }

    }
}