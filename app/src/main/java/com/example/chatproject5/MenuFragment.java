package com.example.chatproject5;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MenuFragment extends Fragment {

    Bundle bundle;
    Intent intent;

    TextView userId, userContent;

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
        String userId_db = bundle.getString("userId_db");
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
                startActivity(intent);
            }
        });



        /***************** 내 체형 *****************/
        myBodyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = new Intent(getContext(), MyBodyActivity.class);
                intent.putExtra("userId_db", userId_db);
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
}