package com.example.chatproject5;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import java.util.Vector;

import adapter.Chat_room_list_Adapter;
import chat.ChatConnThread;
import chat.MsgUtils;
import chat.ThreadUtils;
import dto.RoomList;
import dto.UserDto;


public class Chat_room_listFragment extends Fragment {

    private static final String TAG = Chat_room_listFragment.class.getSimpleName();

    RecyclerView recyclerView;
    Chat_room_list_Adapter adapter;
    LinearLayoutManager layoutManager;

    String userId_db;
    Button delete_button;

    Handler chatConnHandler;
    ChatConnThread chatConnThread;


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
        lists.add(new RoomList(R.drawable.img, "상담사1", "안녕하세요", "12:40"));
        lists.add(new RoomList(R.drawable.img, "상담사2", "[사진]", "13:02"));


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

//                Vector<RoomList> roomLists = new Vector<>();

//                activity.onChangeFragment(0);


//                for(int i =0; i < roomLists.size(); i++) {
//                    activity.onChangeFragment(i);
//                }

                //생성자
                chatConnHandler = ThreadUtils.GetMultiHandler(TAG + "_Chat");
                chatConnThread = new ChatConnThread(getContext(), userId_db);

                Log.d(TAG, "userId ====> " + userId_db);

                //핸들러 객체에 넣기
                chatConnHandler.post(chatConnThread);
                MsgUtils.setCurrentRoom(roomList.getRoom_name());


                Intent intent = new Intent(getActivity(), Chat_roomActivity.class);
                intent.putExtra("roomName", roomList.getRoom_name());
                intent.putExtra("userId_db", userId_db);

                startActivity(intent);

            }
        });

        return rootView;
    }
}