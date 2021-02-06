package com.example.chatproject5;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import android.os.Handler;
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

import chat.MsgUtils;
import chat.Signals;
import database.ChattingRoomListHelper;
import dto.Message;

public class MenuFragment extends Fragment {

    private Bundle bundle;
    private Intent intent;
    private Handler handler = new Handler();

    private TextView userId_text, userContent_text;

    private String userId_db, userPw_db, userContent_db;

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
        userPw_db = bundle.getString("userPw_db");
        String userEmail_db = bundle.getString("userEmail_db");
        String userPhone_db = bundle.getString("userPhone_db");
        userContent_db = bundle.getString("userContent_db");
        String userProfilePhoto_db = bundle.getString("userProfilePhoto_db");


        userId_text = rootView.findViewById(R.id.userId_menu);
        userContent_text = rootView.findViewById(R.id.userContent_menu);
        userId_text.setText(userId_db);
        userContent_text.setText(userContent_db);


        final String urlStr = "http://192.168.0.17:8080/webapp/webServer/login.do";
        new Thread(new Runnable() {
            @Override
            public void run() {

                myProfile(urlStr);
            }
        }).start();



        Button myInfoButton = rootView.findViewById(R.id.myInfo_button);
        Button myOrderButton = rootView.findViewById(R.id.order_menu);
        Button myBodyButton = rootView.findViewById(R.id.body_menu);
        Button myPointButton = rootView.findViewById(R.id.point_menu);
        Button qnaButton = rootView.findViewById(R.id.qna_menu);
        Button goShopButton = rootView.findViewById(R.id.goShop_menu);
        Button logoutButton = rootView.findViewById(R.id.logout_button);


        /* **************** 내 정보 *****************/
        myInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = new Intent(getContext(), MyInfoActivity.class);
                intent.putExtra("userId_db", userId_db);
                intent.putExtra("userPw_db", userPw_db);

                startActivity(intent);
            }
        });


        /* **************** 주문 배송 *****************/
        myOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = new Intent(getContext(), MyOrderListActivity.class);
                intent.putExtra("userId_db", userId_db);

                startActivity(intent);
            }
        });



        /* **************** 내 체형 *****************/
        myBodyButton.setOnClickListener(v -> {

            intent = new Intent(getContext(), MyBodyActivity.class);
            intent.putExtra("userId_db", userId_db);
            startActivity(intent);

        });


        /* **************** 포인트 *****************/
        myPointButton.setOnClickListener(v -> {

            intent = new Intent(getContext(), MyPointActivity.class);
            intent.putExtra("userId_db", userId_db);
            startActivity(intent);
        });


        /* **************** 문의 내역 *****************/
        qnaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = new Intent(getContext(), QnaActivity.class);
                intent.putExtra("userId_db", userId_db);
                startActivity(intent);
            }
        });


        /* **************** 쇼핑 하기 *****************/
        goShopButton.setOnClickListener(v -> {

            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://m.stylenanda.com/product/list_made.html?src=image&kw=00040A&cate_no=3080&utm_source=Search&utm_medium=search_brand&utm_content=allkr_all_0_cv&utm_campaign=naver_nhnad&utm_term=0&n_media=8753&n_query=스타일난다&n_rank=1&n_ad_group=grp-a001-04-000000017381790&n_ad=nad-a001-04-000000120725008&n_keyword_id=nkw-a001-04-000003181272922&n_keyword=스타일난다&n_campaign_type=4&n_contract=tct-a001-04-000000000325860&n_ad_group_type=5&NaPm=ct%3Dkki1ukr4%7Cci%3D0za0000oNfbuCRWtX1pi%7Ctr%3Dbrnd%7Chk%3D52b770d1e5e5b5960d162b350875b4ad141e9174"));
            startActivity(intent);
        });


        /* **************** 로그아웃 *****************/
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //서버에 신호 보내기
                Message message = new Message();
                message.setSignal(Signals.LOGOUT.getSignal() + "");

                MsgUtils.sendMsg(message);


                intent = new Intent(getContext(), MainActivity.class);
                intent.putExtra("userId_db", "");
                intent.putExtra("userPw_db", "");
                startActivity(intent);

            }
        });


        return rootView;
    }   //end onCreate;

    public void myProfile(String urlStr) {
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
                String params = "id=" + userId_db + "&pw=" + userPw_db;

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
        setMyProfile(output.toString());
        System.out.println("수정된 정보 ; " + output.toString());
    }

    public void setMyProfile(String str) {
        Document doc = Jsoup.parse(str);
        Elements userId = doc.select("ol > li.id");
        Elements userContent = doc.select("ol > li.content");

        userContent_db = userContent.text();

        println();
    }

    public void println() {
        handler.post(new Runnable() {
            @Override
            public void run() {

                System.out.println("들어옴");
                userContent_text.setText(userContent_db);
            }
        });
    }

}