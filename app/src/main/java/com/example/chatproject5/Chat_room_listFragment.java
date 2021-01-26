package com.example.chatproject5;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.Toast;

import java.util.Vector;

import adapter.Chat_room_list_Adapter;
import chat.ChatConnThread;
import chat.MsgUtils;
import chat.Signals;
import chat.ThreadUtils;
import dto.Message;
import dto.RoomList;
import dto.UserDto;


public class Chat_room_listFragment extends Fragment {

    private static final String TAG = Chat_room_listFragment.class.getSimpleName();

    private RecyclerView recyclerView;
    private Message message;
    private Chat_room_list_Adapter adapter;
    private LinearLayoutManager layoutManager;


    private String userId_db;
    private Button delete_button;


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


        Vector<RoomList> lists = new Vector<>();
//        lists.add(new RoomList(R.drawable.img, "상담사", "안녕하세요", "12:40"));
//        lists.add(new RoomList(R.drawable.img, "ddd", "[사진]", "13:02"));




        adapter = new Chat_room_list_Adapter(lists, getContext(), getActivity());

        recyclerView = rootView.findViewById(R.id.recyclerView_room_list);
        layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged(); //새로고침

        //채팅방으로 들어가기
        adapter.setOnItemClickListener(new OnListItemClickListener() {
            @Override
            public void onItemClick(Chat_room_list_Adapter.MyViewHolder holder, View view, int position) {

                RoomList roomList = adapter.getItem(position);

                Toast.makeText(getContext(), "선택" + roomList.getRoom_name(), Toast.LENGTH_SHORT).show();


                message = new Message();
                message.setSignal(Signals.CHECK_IN.getSignal() + "");
                message.setRoomId(roomList.getRoom_name());
                message.setToId(userId_db);
                message.setPhoto("");

                MsgUtils.sendMsg(message);

                Log.d(TAG, "roomName ======> " + roomList.getRoom_name());
                Log.d(TAG, "CHECK_IN ======> " + Signals.CHECK_IN.getSignal() + "");

                Intent intent = new Intent(getActivity(), Chat_roomActivity.class);
                intent.putExtra("roomName", roomList.getRoom_name());
                intent.putExtra("userId_db", userId_db);

                startActivity(intent);

            }
        });

        return rootView;
    }   //end onCreate

    private static class ChattingRoomListAdapter extends CursorAdapter {

        public ChattingRoomListAdapter(Context context, Cursor cursor) {
            super(context, cursor, false);
        }


        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return null;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

        }
    }

}