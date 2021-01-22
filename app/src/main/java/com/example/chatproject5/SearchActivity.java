package com.example.chatproject5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/******************************************
                코디네이터 검색
 ******************************************/
public class SearchActivity extends AppCompatActivity {

    ListView listview = null ;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1) ;
        listview = findViewById(R.id.listView_search) ;
        listview.setAdapter(adapter);

        //정보 받기
        intent = getIntent();
        String userId_db = intent.getStringExtra("userId_db");
        ArrayList<String> workerId_db = intent.getStringArrayListExtra("strings");

        //받은 코디네이터 정보 목록 파싱
        for(String s : workerId_db) {
            String[] str = s.split("/,");   //딜림 이렇게 넣어주기

            for(String item : str) {
                adapter.add(item);
            }
        }

        //검색어 입력
        EditText editTextFilter = findViewById(R.id.editTextFilter) ;
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
        }) ;

        //코디네이터 선택
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

/*
                for(String s : workerId_db) {
                    String[] str = s.split("/,");   //딜림 이렇게 넣어주기

                    for(String item : str) {
                        adapter.add(item);
                        Toast.makeText(getApplicationContext(), "클릭된 아이디 : " + item + position, Toast.LENGTH_SHORT).show();
                    }

                }

 */
                //선택한 아이디
                String clickedItem = (String)parent.getAdapter().getItem(position);

                 Toast.makeText(getApplicationContext(), "클릭된 아이디 : " + clickedItem + position, Toast.LENGTH_SHORT).show();

                //정보 보내기
                intent = new Intent(getApplicationContext(), WorkerProfileActivity.class);
                intent.putExtra("userId_db", userId_db);
                intent.putExtra("workerId_db", clickedItem);

                intent.putExtra("position", listview.getCheckedItemPosition());

                startActivity(intent);

            }
        });
    }// end onCreate
}