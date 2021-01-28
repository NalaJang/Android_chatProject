package com.example.chatproject5;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import adapter.ChatListAdapter;
import database.ChattingRoomListHelper;
import dto.ChatListDto;
import dto.ChattingRoomListDto;
import dto.Message;


public class Chat_listFragment extends Fragment {

    private String userId_db;
    private String userContent_db;

    private Date today = new Date();
    private SimpleDateFormat timeNow = new SimpleDateFormat("a K:mm");


    private ArrayList<ChatListDto> chatList = new ArrayList<>();
    private ChattingRoomListDto roomListDto;
    private Message message;

    private ChattingRoomListHelper roomListHelper;

    private Handler handler = new Handler();
    private RecyclerView recyclerView;
    private ChatListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_chat_list, container, false);

        //타이틀 변경
        ActionBar actionBar = ((EntranceActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle("상담사 목록");

        //정보 받기
        Bundle bundle = this.getArguments();
        userId_db = bundle.getString("userId_db");
        userContent_db = bundle.getString("userContent_db");

        //사용자 정보 세팅
        TextView userId = rootView.findViewById(R.id.userId_list);
        TextView content = rootView.findViewById(R.id.content_list);
        userId.setText(userId_db);
        content.setText(userContent_db);

        //자신의 프로필 클릭
        RelativeLayout relativeLayout = rootView.findViewById(R.id.userProfile_chat_list);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), MyProfileActivity.class);
                intent.putExtra("userId_db", userId_db);
                intent.putExtra("userContent_db", userContent_db);

                startActivity(intent);

            }
        }); //end 프로필 클릭



        //코디네이터 목록
        recyclerView = rootView.findViewById(R.id.recyclerView_chatList);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);

        //어댑터 설정
        adapter = new ChatListAdapter(chatList, getContext(), userId_db);



        //DB 에서 선택한 코디네이터 목록 가져오기
        final String urlStr = "http://192.168.0.17:8080/webapp/webServer/selectedWorkerList.do";

        new Thread(new Runnable() {
            @Override
            public void run() {

                selectedCoordinatorList(urlStr);

            }
        }).start();


        return rootView;
    }   //end onCreateView



    public void selectedCoordinatorList(String urlStr) {

        StringBuilder output = new StringBuilder();

        Bundle bundle = this.getArguments();
        userId_db = bundle.getString("userId_db");

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
        setWorkerList(output.toString());

    }   //end coordinatorList


    public void setWorkerList(String str) {

        Document doc = Jsoup.parse(str);
        Elements workerNum_db = doc.select("ol > li.workerNum");
        Elements workerId_db = doc.select("ol > li.workerId");
        Elements workerContent_db = doc.select("ol > li.workerContent");



        for(int j = 0; j < workerId_db.size(); j++) {

            ChatListDto chatListDto = new ChatListDto();
            chatListDto.setNum(Integer.parseInt(workerNum_db.get(j).text()));
            chatListDto.setWorkerId(workerId_db.get(j).text());
            chatListDto.setContent(workerContent_db.get(j).text());

            adapter.addItem(chatListDto);

        }

//        recyclerView.setAdapter(adapter);
                println();

    }   //end setWorkerList


    public void println() {
        handler.post(new Runnable() {
            @Override
            public void run() {

                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }
}