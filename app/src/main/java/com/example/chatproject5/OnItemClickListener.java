package com.example.chatproject5;

import android.view.View;

import adapter.ChatListAdapter;


public interface OnItemClickListener {
    void onItemClick(ChatListAdapter.MyViewHolder holder, View view, int position);
}
