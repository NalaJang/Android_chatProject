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
import java.util.ArrayList;

import adapter.ChatListAdapter;
import chat.Constants;
import dto.ChatListDto;


public class Chat_listFragment extends Fragment {

    private String userId_db, userPw_db, userContent;
    private TextView textView, content;

    private ArrayList<ChatListDto> chatList = new ArrayList<>();

    private final Handler handler = new Handler();
    private RecyclerView recyclerView;
    static ChatListAdapter adapter;



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
        userPw_db = bundle.getString("userPw_db");
        userContent = bundle.getString("userContent_db");

        //사용자 정보 세팅
        TextView userId = rootView.findViewById(R.id.userId_list);
        content = rootView.findViewById(R.id.content_list);
        userId.setText(userId_db);
//        content.setText(userContent);

        textView = rootView.findViewById(R.id.text_chat_list);


        //기존 사용자 정보
        new Thread(new Runnable() {
            @Override
            public void run() {

                final String urlStr2 = Constants.SERVER_URL + "login.do";

                myInfo(urlStr2);

            }
        }).start();


        //자신의 프로필 클릭
        RelativeLayout relativeLayout = rootView.findViewById(R.id.userProfile_chat_list);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), MyProfileActivity.class);
                intent.putExtra("userId_db", userId_db);
                intent.putExtra("userContent_db", userContent);

                startActivity(intent);

            }
        }); //end 프로필 클릭



        //코디네이터 목록
        recyclerView = rootView.findViewById(R.id.recyclerView_chatList);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);


        //어댑터 설정
        chatList.clear();   // chatList 값이 계속 불러와져서 clear() 추가 *****
        adapter = new ChatListAdapter(chatList, getContext(), userId_db);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        //선택한 코디네이터 목록 DB 에서 가져오기
        final String urlStr = Constants.SERVER_URL + "selectedWorkerList.do";

        new Thread(new Runnable() {
            @Override
            public void run() {

                selectedCoordinatorList(urlStr);

            }
        }).start();

        return rootView;
    }   //end onCreateView



    //기존 정보
    public void myInfo(String urlStr) {
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

        setMyInfo(output.toString());    //잘라줄 값

    }

    public void setMyInfo(String str) {

        Document doc = Jsoup.parse(str);
        Elements result = doc.select("p.result");
        Elements userContent_db = doc.select("ol > li.content");
        Elements userProfilePhoto_db = doc.select("ol > li.profilePhoto");


        for(int i = 0; i < result.size(); i++) {

            if(result.get(0).text().equals("로그인 성공")) {

                userContent = userContent_db.text();
            }
        }
        println3();
    }

    public void println3() {
        handler.post(() -> {

            content.setText(userContent);
        });
    }


    //DB 코디네이터 목록
    public void selectedCoordinatorList(String urlStr) {

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
        setWorkerList(output.toString());

    }   //end coordinatorList

//    private HashSet<String> hashSet = new HashSet<>(); //중복 값 제거

    public void setWorkerList(String str) {

        Document doc = Jsoup.parse(str);
        Elements result = doc.select("p.result");
        Elements workerNum_db = doc.select("ol > li.workerNum");
        Elements workerId_db = doc.select("ol > li.workerId");
        Elements workerContent_db = doc.select("ol > li.workerContent");

        EntranceActivity.hashSet.clear();//추가

        for(int i = 0, size = result.size(); i < size; i++) {
            if(result.get(0).text().equals("목록있음")) {

                for(int j = 0; j < workerId_db.size(); j++) {

                    ChatListDto chatListDto = new ChatListDto();
                    chatListDto.setNum(Integer.parseInt(workerNum_db.get(j).text()));
                    chatListDto.setWorkerId(workerId_db.get(j).text());
                    chatListDto.setContent(workerContent_db.get(j).text());

                    //↓ 내가 등록한 상담사만 담기(중복해서 담을 수 없음, 꺼낼 때 순서 x, 찾는 속도가 빠름)
                    EntranceActivity.hashSet.add(chatListDto.getWorkerId());

                    adapter.addItem(chatListDto);
                }

                println();

            }
        }

    }   //end setWorkerList

    //"목록있음"에 대한 출력 결과
    public void println() {
        handler.post(new Runnable() {
            @Override
            public void run() {

                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                textView.setVisibility(View.GONE);
            }
        });
    }
}