package com.example.chatproject5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;

public class QnaActivity extends AppCompatActivity {

    private QnaAddFragment qnaAddFragment;
    private QnaCheckFragment checkFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_qna);

        setTitle("고객센터");

        //정보받기
        Intent intent = getIntent();
        String userId_db = intent.getStringExtra("userId_db");

        //정보보내기
        Bundle bundle = new Bundle();
        bundle.putString("userId_db", userId_db);


        qnaAddFragment = new QnaAddFragment();
        checkFragment = new QnaCheckFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.container_qna, qnaAddFragment).commit();
        qnaAddFragment.setArguments(bundle);  //정보 보내기

        TabLayout tabs = findViewById(R.id.tabs_qna);
        tabs.addTab(tabs.newTab().setText("문의 하기"));
        tabs.addTab(tabs.newTab().setText("문의 내역 확인"));

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Fragment selected = null;

                if(position == 0) {
                    selected = qnaAddFragment;

                } else if(position == 1) {
                    selected = checkFragment;

                }
                getSupportFragmentManager().beginTransaction().replace(R.id.container_qna, selected).commit();
                selected.setArguments(bundle);  //정보 보내기
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

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