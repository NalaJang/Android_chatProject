package com.example.chatproject5;

import android.view.View;

import adapter.Chat_room_list_Adapter;

public interface OnListItemClickListener {
    void onItemClick(Chat_room_list_Adapter.MyViewHolder holder, View view, int position);


}
