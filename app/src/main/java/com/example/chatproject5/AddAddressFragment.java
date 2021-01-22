package com.example.chatproject5;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import static android.app.Activity.RESULT_OK;


public class AddAddressFragment extends Fragment {

    static final int SEARCH_ADDRESS = 10000;

    EditText address_edit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_add_address, container, false);

        address_edit = rootView.findViewById(R.id.et_address);

        Button search_button = rootView.findViewById(R.id.button);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getContext(), WebViewActivity.class);
                startActivityForResult(i, SEARCH_ADDRESS);
            }
        });

        return rootView;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch(requestCode) {
            case SEARCH_ADDRESS :

                if(requestCode == RESULT_OK) {
                    String data = intent.getExtras().getString("data");

                    if(data != null) {
                        address_edit.setText(data);
                    }
                }

                break;
        }
    }

}