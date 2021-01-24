package com.example.chatproject5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;

import adapter.OrderAdapter;
import dto.OrderDto;

public class MyOrderListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_order_list);

        setTitle("주문배송");

        RecyclerView recyclerView = findViewById(R.id.recyclerView_order);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);

        OrderAdapter adapter = new OrderAdapter();
        adapter.addItem(new OrderDto(R.drawable.sweatshirt, "스웨이트셔츠", "핑크 스몰", 39000, 1, "1", "2021-01-17"));
        recyclerView.setAdapter(adapter);

    }   //end onCreate

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