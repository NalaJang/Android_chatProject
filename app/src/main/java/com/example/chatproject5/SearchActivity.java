package com.example.chatproject5;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import adapter.SearchListAdapter;
import chat.Constants;
import dto.ChatListDto;
import dto.SearchListDto;

/******************************************
                코디네이터 검색
 ******************************************/
public class SearchActivity extends AppCompatActivity implements TextWatcher{

    private ListView listview = null;

    private String userId_db;
    private SearchListAdapter adapter;
    private final Handler handler = new Handler();

    static HashSet<String> hashSet = new HashSet<>();

//    private ArrayList<SearchListDto> items = new ArrayList<>();

/*
    @Override
    public void onBackPressed() {
//        Chat_listFragment chat_listFragment = new Chat_listFragment();
//        Intent intent = new Intent(this, chat_listFragment.getClass());
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
        Chat_listFragment.adapter.notifyDataSetChanged();

        super.onBackPressed();
    }

 */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        //정보 받기
        Intent intent = getIntent();
        userId_db = intent.getStringExtra("userId_db");

        adapter = new SearchListAdapter(getApplicationContext(), userId_db) ;

        listview = findViewById(R.id.listView_search);
        listview.setAdapter(adapter);

        final String urlStr2 = Constants.SERVER_URL + "selectedWorkerList.do";
        new Thread(new Runnable() {
            @Override
            public void run() {

                selectedCoordinatorList(urlStr2);
            }
        }).start();


        //DB 에서 코디네이터 목록 가져오기
        final String urlStr = Constants.SERVER_URL + "workerList.do";

        new Thread(new Runnable() {
            @Override
            public void run() {

                coordinatorList(urlStr);

            }
        }).start();


/*
        //받은 코디네이터 정보 목록 파싱
        for(String s : workerId_db) {
            String[] str = s.split("/,");   //딜림 이렇게 넣어주기

            for(String item : str) {
                adapter.add(item);
//                adapter.addItem(item);
//                Log.d("searchAct", item);
            }
        }

*/
        //검색어 입력
        EditText editTextFilter = findViewById(R.id.editTextFilter);
        editTextFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable edit) {

                String filterText = edit.toString() ;

                if (filterText.length() > 0) {

                    //setFilterText 함수로 필터링을 수행한다 (작성한 텍스트를 팝업으로 알림)
                    listview.setFilterText(filterText) ;

                } else {
                    listview.clearTextFilter() ;
                }
            }
            //검색 관련 override
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

    }// end onCreate

    //검색 관련 override
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        adapter.getFilter().filter(s);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    //기존 상담사 목록
    public void coordinatorList(String urlStr) {
        StringBuilder output = new StringBuilder();

        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if(conn != null) {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);

                OutputStream outputStream = conn.getOutputStream();
                String params = "id=123";
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
    }


    public void setWorkerList(String str) {
        Document doc = Jsoup.parse(str);
        Elements workerNum_db = doc.select("ol > li.num");
        Elements workerId_db = doc.select("ol > li.id");
        Elements workerContent_db = doc.select("ol > li.content");

        for(int i = 0, size = workerNum_db.size(); i < size; i++) {

            if(hashSet.contains(workerId_db.get(i).text())) {

                continue;   //-> adapter 에 등록하지 않고(아래 코드를 실행하지 않고) for 문을 넘어감
            }

            SearchListDto searchListDto = new SearchListDto();
            searchListDto.setWorkerNum(workerNum_db.get(i).text());
            searchListDto.setWorkerId(workerId_db.get(i).text());
            searchListDto.setWorkerContent(workerContent_db.get(i).text());

            adapter.addItem(searchListDto);

        }

        println();
    }

    public void println() {
        handler.post(new Runnable() {
            @Override
            public void run() {

                listview.setAdapter(adapter);
            }
        });
    }

    public void selectedCoordinatorList(String urlStr) {

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
        setMyWorkerList(output.toString());
    }

    public void setMyWorkerList(String str) {

        Document doc = Jsoup.parse(str);
        Elements workerNum_db = doc.select("ol > li.workerNum");
        Elements workerId_db = doc.select("ol > li.workerId");
        Elements workerContent_db = doc.select("ol > li.workerContent");

        hashSet.clear();//추가

        for(int j = 0; j < workerId_db.size(); j++) {

            ChatListDto chatListDto = new ChatListDto();
            chatListDto.setNum(Integer.parseInt(workerNum_db.get(j).text()));
            chatListDto.setWorkerId(workerId_db.get(j).text());
            chatListDto.setContent(workerContent_db.get(j).text());

            //↓ 내가 등록한 상담사만 담기(중복해서 담을 수 없음, 꺼낼 때 순서 x, 찾는 속도가 빠름)
            hashSet.add(chatListDto.getWorkerId());

//            adapter.addItem(chatListDto);
        }
    }
}