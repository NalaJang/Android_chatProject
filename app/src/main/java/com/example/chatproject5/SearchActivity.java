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

import adapter.SearchListAdapter;
import dto.SearchListDto;

/******************************************
                코디네이터 검색
 ******************************************/
public class SearchActivity extends AppCompatActivity implements TextWatcher{

    private ListView listview = null ;
    private Intent intent;

    private SearchListAdapter adapter;
    private Handler handler = new Handler();

    private ArrayList<SearchListDto> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        //정보 받기
        intent = getIntent();
        String userId_db = intent.getStringExtra("userId_db");

        adapter = new SearchListAdapter(getApplicationContext(), userId_db) ;

        listview = findViewById(R.id.listView_search);
        listview.setAdapter(adapter);


        //DB 에서 코디네이터 목록 가져오기
        final String urlStr = "http://192.168.0.17:8080/webapp/webServer/workerList.do";

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

                    listview.setFilterText(filterText) ;

                } else {
                    listview.clearTextFilter() ;
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });


        /*
                //선택한 아이디
                String clickedItem = (String)parent.getAdapter().getItem(position);

                Toast.makeText(getApplicationContext(), "클릭된 아이디 : " + clickedItem + position, Toast.LENGTH_SHORT).show();

                //정보 보내기
                intent = new Intent(getApplicationContext(), WorkerProfileActivity.class);
                intent.putExtra("userId_db", userId_db);
                intent.putExtra("workerId_db", clickedItem);
                intent.putExtra("workerContent_db", workerContent_db);
                intent.putExtra("position", listview.getCheckedItemPosition());

                startActivity(intent);

            }
        });

         */

    }// end onCreate

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

    public void setWorkerList(String str) {
        Document doc = Jsoup.parse(str);
        Elements workerNum_db = doc.select("ol > li.num");
        Elements workerId_db = doc.select("ol > li.id");
        Elements workerContent_db = doc.select("ol > li.content");

        System.out.println("=====hashSet : " + EntranceActivity.hashSet.toString());

        for(int i = 0, size = workerNum_db.size(); i < size; i++) {

            if(EntranceActivity.hashSet.contains(workerId_db.get(i).text())) {

                System.out.println("===================");
                continue;   //-> adapter 에 등록하지 않고(↓실행않고) for 문을 넘어감
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
}