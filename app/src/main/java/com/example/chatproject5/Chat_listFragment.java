package com.example.chatproject5;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
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

import adapter.ChatListAdapter;
import adapter.CustomListViewAdapter;
import dto.ChatListDto;


public class Chat_listFragment extends Fragment {

    String userId_db;
    String userContent_db;

    ArrayList<ChatListDto> chatList = new ArrayList<>();
    Handler handler = new Handler();

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
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView_chatList);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);

        ChatListAdapter adapter = new ChatListAdapter();
//        adapter.addItem(new ChatListDto("상담사2", "안녕하세요"));
        recyclerView.setAdapter(adapter);

        //DB 에서 코디네이터 목록 가져오기
        final String urlStr = "http://192.168.0.17:8080/webapp/webServer/selectedWorkerList.do";

        new Thread(new Runnable() {
            @Override
            public void run() {

                coordinatorList(urlStr);

            }
        }).start();


        return rootView;
    }   //end onCreateView



    public void coordinatorList(String urlStr) {

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
    }

    public void setWorkerList(String urlStr) {

        Document doc = Jsoup.parse(urlStr);
        Elements result = doc.select("p.result");
        Elements workerId_db = doc.select("ol > li.workerId");
        Elements workerContent_db = doc.select("ol > li.workerContent");

        for(int i = 0; i < result.size(); i++) {
            if(result.get(0).text().equals("등록된상담사")) {
                for(int j = 0; j < workerId_db.size(); j++) {

                    ChatListDto chatListDto = new ChatListDto();
                    chatListDto.setWorkerId(workerId_db.get(j).text());
                    chatListDto.setContent(workerContent_db.get(j).text());

                    chatList.add(0, chatListDto);   //첫번째 줄에 삽입
                    //chatList.add(chatListDto);          //마지막 줄에 삽입


                }
            }
        }

    }
}