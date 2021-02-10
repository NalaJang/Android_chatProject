package com.example.chatproject5;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import adapter.Chat_room_list_Adapter;
import chat.MsgUtils;
import chat.Signals;
import database.ChattingRoomListHelper;
import dto.ChattingRoomListDto;
import dto.Message;


public class Chat_room_listFragment extends Fragment {

    private String userId_db;

    private Message message;
    private RecyclerView recyclerView;
    private Chat_room_list_Adapter adapter;
    private LinearLayoutManager layoutManager;

    private BroadcastReceiver receiver;


//    private final Date today = new Date();
//    private final SimpleDateFormat timeNow = new SimpleDateFormat("a K:mm");


//    @Override
//    public void onResume() {
//        super.onResume();
//
//        adapter.notifyDataSetChanged();
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_chat_room_list, container, false);


        //타이틀 변경
        ActionBar actionBar = ((EntranceActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle("채팅목록");
        actionBar.setDisplayHomeAsUpEnabled(false);

        //정보 받기
        Bundle bundle = this.getArguments();
        userId_db = bundle.getString("userId_db");


        //DB 에서 채팅목록 가져오기
        ChattingRoomListHelper db = new ChattingRoomListHelper(getContext());
        ArrayList<ChattingRoomListDto> roomList = db.roomList(userId_db);


        adapter = new Chat_room_list_Adapter(roomList, getContext(), getActivity());

        recyclerView = rootView.findViewById(R.id.recyclerView_room_list);
        layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged(); //새로고침


        /**/
        IntentFilter filter = new IntentFilter();
        filter.addAction("broadcast_entrance");

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra("message");
                String fromId = intent.getStringExtra("fromId");
                String time = intent.getStringExtra("time");
                System.out.println("chatRoomListFrag time : " + time);


                ChattingRoomListHelper roomListHelper = new ChattingRoomListHelper(context);
                ChattingRoomListDto roomListDto = new ChattingRoomListDto();


                roomListDto.setLastContent(message);
                roomListDto.setOtherId(fromId);
                roomListDto.setTime(time);


                //상대방과의 채팅방이 존재할 때
                if( roomListHelper.findRoom(fromId) != null ) {

                    roomListHelper.update(message, time, fromId);


                //상대방과의 채팅방이 없으면 생성
                } else {

                    adapter.addChat(roomListDto);


//                    FragmentTransaction ft = getFragmentManager().beginTransaction();
//                    ft.detach(chat_room_listFragment).attach(chat_room_listFragment).commit();
                }

                refresh();

//                adapter.notifyDataSetChanged(); //새로고침

            }
        };

        getContext().registerReceiver(receiver, filter);
        /**/


        //채팅방으로 들어가기
        adapter.setOnItemClickListener(new OnListItemClickListener() {
            @Override
            public void onItemClick(Chat_room_list_Adapter.MyViewHolder holder, View view, int position) {

                ChattingRoomListDto roomList = adapter.getItem(position);

                Toast.makeText(getContext(), "선택" + roomList.getRoomName(), Toast.LENGTH_SHORT).show();


                message = new Message();
                message.setSignal(Signals.CHECK_IN.getSignal() + "");
                message.setRoomId(roomList.getRoomName());
                message.setToId(roomList.getRoomName());
                message.setPhoto("");


                MsgUtils.sendMsg(message);  //-> 서버에 입장 신호 보내기


                Intent intent = new Intent(getActivity(), Chat_roomActivity.class);
                intent.putExtra("roomName", roomList.getRoomName());
                intent.putExtra("userId_db", userId_db);
//                intent.putExtra("time", timeNow.format(today));

                startActivity(intent);

            }
        }); //end adapter onClick

        return rootView;
    }   //end onCreate



    // 채팅방 바로 갱신 시키기 ! **********
    private void refresh() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        //ft.detach(this).attach(this).commit();
        //에러 : Can not perform this action after onSaveInstanceState -> 같은 채팅방에서 메세지를 주고 받을 때 발생
        ft.detach(this).attach(this).commitAllowingStateLoss();
    }

}